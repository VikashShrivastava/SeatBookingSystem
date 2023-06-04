package com.mis.bookingservices;

import com.mis.CustException.CustException;
import com.mis.EmailService.EmailSenderService;
import com.mis.bookingmodels.Booking;
import com.mis.bookingmodels.Building;
import com.mis.bookingmodels.Floor;
import com.mis.bookingrepositories.BookingRepo;
import com.mis.bookingrepositories.BuildingRepo;
import com.mis.bookingrepositories.FloorRepo;
import com.mis.customclasses.Custom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {

    private final BuildingRepo buildingRepo;
    private final FloorRepo floorRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final BookingRepo bookingRepo;

    public BuildingService(BuildingRepo buildingRepo, FloorRepo floorRepo, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService, BookingRepo bookingRepo){
        this.buildingRepo =buildingRepo;
        this.floorRepo =floorRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.bookingRepo = bookingRepo;
    }
    public boolean validateBuilding(String buildingName){
        return buildingRepo.findById(buildingName).isPresent();
    }

    public void addBuilding(Building building) throws CustException {
        Optional<Building> opt = buildingRepo.findById(building.getBuildingName());
        if(opt.isPresent()){
            throw new CustException("Building Already Exists");
        }
        buildingRepo.save(building);
    }

    public ResponseEntity<String> findByBuilding(Custom custom) {
        String buildingName = custom.getBuildingName();
        if(!validateBuilding(buildingName)){
            return new ResponseEntity<>("No such building Exist.", HttpStatus.BAD_REQUEST);
        }
        List<Floor> floorsList = floorRepo.findByBuilding(buildingName);
        StringBuilder floors = new StringBuilder();
        for(Floor f : floorsList) floors.append(f.toString1()).append("\n");
        return new ResponseEntity<>(floors.toString(),HttpStatus.FOUND);
    }

    public boolean verifyBuilding(String buildingName) throws CustException {
        Optional<Building> opt = buildingRepo.findById(buildingName);
        return opt.isPresent();

    }

    public Building getBuilding(Building building) {
        return buildingRepo.findById(building.getBuildingName()).get();
    }

    public void deletebuilding(Building building) throws CustException {
        Optional<Building>build=buildingRepo.findById(building.getBuildingName());
        if(build.isEmpty())
        {
            throw new CustException("No such building found");
        }
        LocalDate date=LocalDate.now();
        String d=date.toString();
        List<Booking>bookingList= bookingRepo.getBookinglist(building.getBuildingName(),d);
        if(!bookingList.isEmpty())
        {
           for(Booking b:bookingList)
           {
               LocalTime time=LocalTime.now();
               LocalTime time1=LocalTime.parse(b.getStartTime());
            if(time1.isAfter(time))
            {
             emailSenderService.sendSimpleEmail(b.getUserinfo().getUserEmail(),"Booking Cancelled ","Hi "+b.getUserinfo().getName()+" this is to inform you that your booking with following details  has been cancelled due to some reasons so please take note of this.\n"+b+" \n\n\n\n\nIn case of any query reach out to our customer care service. /n Thanks ");
            }
               bookingRepo.deleteById(b.getBookingId());
           }
        }
        buildingRepo.deleteById(building.getBuildingName());
    }

    public void updateCapacity(String buildingName, int numberOfSeats) {
        buildingRepo.updateCapacity(buildingName, numberOfSeats);
    }
}
