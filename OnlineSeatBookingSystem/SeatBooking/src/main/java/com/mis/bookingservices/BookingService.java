package com.mis.bookingservices;

import com.mis.CustException.CustException;
import com.mis.EmailService.EmailSenderService;
import com.mis.PdfGenerate.PdfService;
import com.mis.bookingmodels.*;
import com.mis.bookingrepositories.BookingRepo;
import com.mis.bookingrepositories.BuildingRepo;
import com.mis.bookingrepositories.SeatRepo;
import com.mis.bookingrepositories.UserRepo;
import com.mis.customclasses.Custom;
import com.mis.customclasses.Location;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepo bookingRepo;
    private final BuildingRepo buildingRepo;
    private final SeatRepo seatRepo;
    private final EmailSenderService emailSenderService;
    private final UserRepo userRepo;
    private final UserService userService;
    private final PdfService pdfService;
    public BookingService(BookingRepo bookingRepo, BuildingRepo buildingRepo, SeatRepo seatRepo, EmailSenderService emailSenderService, UserRepo userRepo, UserService userService, PdfService pdfService){
        this.bookingRepo = bookingRepo;
        this.buildingRepo = buildingRepo;
        this.seatRepo = seatRepo;
        this.emailSenderService = emailSenderService;
        this.userRepo = userRepo;
        this.userService = userService;
        this.pdfService = pdfService;
    }

    public ResponseEntity<String> getHistory(Custom custom) {
        System.out.println(custom.getUser().getUserId());
        Optional<List<Booking>>userHistory = bookingRepo.getUserHistory(custom.getUser().getUserId());
        if(userHistory.isEmpty())return new ResponseEntity<>("No bookings Found.", HttpStatus.FOUND);
        List<Booking>history = userHistory.get();
        StringBuilder his = new StringBuilder();
        for(Booking b: history){
            his.append(b.toString()).append("\n");
        }
        return new ResponseEntity<>(his.toString(), HttpStatus.FOUND);
    }

    public ResponseEntity<String> findByLocation(Location custom) {
        Optional<List<Building>> buildingLists = bookingRepo.findAvailableSeatsAtLocation(custom.getBuilding_location());
        StringBuilder buildings = new StringBuilder();
        List<Building>buildingList = new ArrayList<>();
        if(buildingLists.isPresent())buildingList = buildingLists.get();
        if(buildingList.isEmpty())return new ResponseEntity<>("No Buildings Available at given Location", HttpStatus.NOT_FOUND);
        for(Building b : buildingList){
            buildings.append(b.toString1()).append("\n");
        }
        return new ResponseEntity<>(buildings.toString(),HttpStatus.FOUND);
    }
public void validateTime(Custom custom)throws CustException{
    String startTime =  custom.getBooking().getStartTime();
    String endTime = custom.getBooking().getEndTime();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    try {
        LocalTime.parse(startTime, formatter);
        LocalTime.parse(endTime, formatter);
    }catch (Exception e){
        throw new CustException("Invalid time");
    }
}
    public void validateDates(Custom custom)throws CustException{
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        String startingDate = custom.getBooking().getStartDate();
        String endDate = custom.getBooking().getEndDate();
        try{
            Date d1 = dtf.parse(startingDate);
            Date d2 = dtf.parse(endDate);
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String today = currentDate.format(formatter);
            Date d3 = dtf.parse(today);
            if(d1.compareTo(d2) > 1 || d1.compareTo(d3) < 0)throw new CustException("End date should be greater than start date start date should be greater then current date.");
        }
        catch (Exception e){
            throw new CustException("Invalid date or time format.");
        }
    }

    public void bookseat(Custom custom) throws CustException, ParseException, MessagingException {
        validateDates(custom);
        validateTime(custom);
        if(!userService.verifyUser(custom.getUser()))
        {
            throw new CustException("Invalid ID/Password");
        }
        Optional<User> user=userRepo.findById(custom.getUser().getUserId());
        User user1=user.get();
        Optional<Building>building=buildingRepo.findById(custom.getBuildingName());
        if(building.isEmpty())
        {
            throw  new CustException("Invalid Building Details");
        }
        List<Floor>floors=building.get().getFloorList();
        Floor tempfloor=new Floor();
        boolean b=false;
        for(Floor obj:floors)
        {
            if(obj.getFloorNo()==custom.getFloor().getFloorNo())
            {
                b=true;
                tempfloor.setRoomList(obj.getRoomList());
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid Floor no");
        }
        List<Room>roomList=tempfloor.getRoomList();
        b=false;
        Room temproom=new Room();
        for(Room obj:roomList)
        {
            if(obj.getRoomNo()==custom.getRoom().getRoomNo())
            {
                b=true;
                temproom.setSeatList(obj.getSeatList());
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid Room Details");
        }
        List<Seat>seats=temproom.getSeatList();
        b=false;
        for(Seat obj:seats)
        {
            if(obj.getSeatNo() == custom.getSeat().getSeatNo())
            {
                b=true;
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid seat no");
        }
        String date1=custom.getBooking().getStartDate();
        String date2=custom.getBooking().getEndDate();
        Integer seatId=seatRepo.findId(custom.getSeat().getSeatNo(),custom.getFloor().getFloorNo(),custom.getBuilding().getBuildingName(),custom.getRoom().getRoomNo());
        List<Booking>bookingList=bookingRepo.getBookinglist1(custom.getBuilding().getBuildingName(),date1,date2,seatId);
        for(Booking obj:bookingList)
        {
            if(isClash(obj.getStartDate(),obj.getStartTime(), obj.getEndTime(), obj.getEndDate(), custom))
           {
               throw new CustException("Seat already booked");
           }
        }
               Booking b1 = new Booking(custom.getBooking().getStartDate(),custom.getBooking().getEndDate(),custom.getBooking().getStartTime(),custom.getBooking().getEndTime(),user1,custom.getBuilding().getBuildingName(),seatId);
               bookingRepo.save(b1);
               pdfService.generatepdf("\n\n\nHi "+user1.getName()+"!",b1.toString(),b1.getBookingId());
               emailSenderService.sendMailWithAttachment(user1.getUserEmail(), "Booking Details","This is to inform you that you have booked a seat with following details "+b1+"\n * Seat No : "+custom.getSeat().getSeatNo()+"\n * Floor No : "+custom.getFloor().getFloorNo()+"\n * Room No : "+custom.getRoom().getRoomNo()+"\n\n\n\n In case of any query reach out to our customer care service"+"\n Email : onlineseatbookingsystem@gmail.com","C:\\Users\\DEVANSH DIXIT\\Downloads\\"+b1.getBookingId()+".pdf");
    }


    public boolean isClash(String date1, String startTime, String endTime, String date2, Custom custom) throws ParseException {

        String event1Start = date1+" "+startTime;
        String event1End = date2+" "+endTime;
        String event2Start = custom.getBooking().getStartDate()+" "+custom.getBooking().getStartTime();
        String event2End = custom.getBooking().getEndDate()+" "+custom.getBooking().getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime1Start = LocalDateTime.parse(event1Start, formatter);
        LocalDateTime dateTime1End = LocalDateTime.parse(event1End, formatter);
        LocalDateTime dateTime2Start = LocalDateTime.parse(event2Start, formatter);
        LocalDateTime dateTime2End = LocalDateTime.parse(event2End, formatter);

        if(dateTime1Start.isBefore(dateTime2End) && dateTime1End.isAfter(dateTime2Start) || (dateTime2Start.isBefore(dateTime1End) && dateTime2End.isAfter(dateTime1Start))||(dateTime1Start.equals(dateTime1Start) && dateTime1End.equals(dateTime2End))){
            return true;
        }
        return false;
    }

    public List<Booking> getClashingSeats() {
        return bookingRepo.getClashingSeats();
    }

    public void cancelseat(Custom custom) throws CustException {
        if(!userService.verifyUser(custom.getUser()))
            throw new CustException("Invalid User Details");
        Optional<User>user=userRepo.findById(custom.getUserId());
        if(user.isEmpty())
        {
            throw new CustException("Invalid User Details");
        }
        User temp=user.get();
        Optional<Booking>booking=bookingRepo.findById(custom.getBooking().getBookingId());
        if(booking.isEmpty())
        {
            throw new CustException("Invalid Booking Details");
        }
        LocalTime time=LocalTime.parse(booking.get().getStartTime());
        LocalDate date=LocalDate.parse(booking.get().getStartDate());
        LocalDate date1=LocalDate.now();
        LocalTime time1=LocalTime.now();
        if(time1.isAfter(time)&&date1.isEqual(date))
        {
            throw new CustException("Cannot cancel Booking ");
        }
        emailSenderService.sendSimpleEmail(temp.getUserEmail(),"Booking Cancellation Request ","Hi "+temp.getName()+" as per request we have cancelled your booking with following details \n"+booking.get()+"\n\n\n\n In case of any query reach to our customer care service\n Email : onlineseatbookingsystem@gmail.com");
        bookingRepo.deleteById(booking.get().getBookingId());
    }
    public String bookNSeat(Custom custom) throws CustException, ParseException {
        validateDates(custom);
        validateTime(custom);
        if(!userService.verifyUser(custom.getUser()))
        {
            throw new CustException("Invalid ID/Password");
        }
        Optional<User> user=userRepo.findById(custom.getUser().getUserId());
        User user1=user.get();
        Optional<Building>building=buildingRepo.findById(custom.getBuildingName());
        if(building.isEmpty())
        {
            throw  new CustException("Invalid Building Details");
        }
        List<Floor>floors=building.get().getFloorList();
        Floor tempfloor=new Floor();
        boolean b=false;
        for(Floor obj:floors)
        {
            if(obj.getFloorNo()==custom.getFloor().getFloorNo())
            {
                b=true;
                tempfloor.setRoomList(obj.getRoomList());
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid Floor no");
        }
        List<Room>roomList=tempfloor.getRoomList();
        b=false;
        Room temproom=new Room();
        for(Room obj:roomList)
        {
            if(obj.getRoomNo()==custom.getRoom().getRoomNo())
            {
                b=true;
                temproom.setSeatList(obj.getSeatList());
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid Room Details");
        }
        List<Seat>seats=temproom.getSeatList();
        b=false;
        for(Seat obj:seats)
        {
            if(obj.getSeatNo() == custom.getSeat().getSeatNo())
            {
                b=true;
                break;
            }
        }
        if(!b)
        {
            throw new CustException("Invalid seat no");
        }
        String date1=custom.getBooking().getStartDate();
        String date2=custom.getBooking().getEndDate();
        Integer seatId=seatRepo.findId(custom.getSeat().getSeatNo(),custom.getFloor().getFloorNo(),custom.getBuilding().getBuildingName(),custom.getRoom().getRoomNo());
        List<Booking>bookingList=bookingRepo.getBookinglist1(custom.getBuilding().getBuildingName(),date1,date2,seatId);
        for(Booking obj:bookingList)
        {
            if(isClash(obj.getStartDate(),obj.getStartTime(), obj.getEndTime(), obj.getEndDate(), custom))
            {
                throw new CustException("Seat already booked");
            }
        }
        Booking b1 = new Booking(custom.getBooking().getStartDate(),custom.getBooking().getEndDate(),custom.getBooking().getStartTime(),custom.getBooking().getEndTime(),user1,custom.getBuilding().getBuildingName(),seatId);
        bookingRepo.save(b1);
        return b1.toString();
    }
    public void sentEmail(User u, String s){
        Optional<User> user=userRepo.findById(u.getUserId());
        User user1=user.get();
        emailSenderService.sendSimpleEmail(user1.getUserEmail(), "Booking Details","This is to inform you that you have booked a seat with following details:"+s+"\n\n\n\n In case of any query reach out to our customer care service"+"\n Email : onlineseatbookingsystem@gmail.com");
    }
}
