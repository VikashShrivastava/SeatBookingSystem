package com.mis.bookingservices;

import com.mis.CustException.CustException;
import com.mis.bookingmodels.Booking;
import com.mis.bookingmodels.Seat;
import com.mis.bookingrepositories.FloorRepo;
import com.mis.bookingrepositories.SeatRepo;
import com.mis.customclasses.Custom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {
    private final SeatRepo seatRepo;
    private final FloorRepo floorRepo;
    private final BookingService bookingService;

    public SeatService(SeatRepo seatRepo, FloorRepo floorRepo, BookingService bookingService) {
        this.seatRepo = seatRepo;
        this.floorRepo = floorRepo;
        this.bookingService = bookingService;
    }

    public void addSeat(Seat s){
        seatRepo.save(s);
    }

    public ResponseEntity<String> listAllSeats(Custom custom) {
        int fNo = custom.getFloor().getFloorNo();

        List<Seat> seats =  seatRepo.getAllSeats(fNo).get();
        StringBuilder str = new StringBuilder();
        for(Seat s:seats){
            str.append(s.toString1()).append("\n");
        }
        return new ResponseEntity<>(str.toString(), HttpStatus.ACCEPTED);
    }

    public List<Seat> getAllRoom() {
        return seatRepo.getAllSeat();
    }
    public List<Seat> getAllSeats(Custom custom) throws CustException, ParseException {
        List<Seat> seatList = seatRepo.getAllSeatsInRoom(custom.getBuilding().getBuildingName(), custom.getFloor().getFloorNo(), custom.getRoom().getRoomNo());
        List<Booking> clasBookings = bookingService.getClashingSeats();
        List<Integer>clashSeatIds = new ArrayList<>();
        for(Booking b: clasBookings){
            for(Seat s: seatList){
                if(s.getSeatId() == b.getSeatId()){
                    if(bookingService.isClash(b.getStartDate(), b.getStartTime().substring(0,5), b.getEndTime().substring(0,5), b.getEndDate(), custom)){
                        clashSeatIds.add(s.getSeatId());
                    }
                }
            }
        }
        List<Seat>availableSeats = new ArrayList<>();
        for(Seat s: seatList){
            if(!clashSeatIds.contains(s.getSeatId()))availableSeats.add((s));
        }
        return availableSeats;
    }

}
