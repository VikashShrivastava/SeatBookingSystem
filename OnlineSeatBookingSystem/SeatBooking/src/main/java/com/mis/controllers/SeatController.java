package com.mis.controllers;

import com.mis.CustException.CustException;
import com.mis.bookingservices.FloorService;
import com.mis.bookingservices.RoomService;
import com.mis.bookingservices.SeatService;
import com.mis.customclasses.Custom;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeatController {
    private final RoomService roomService;
    private final FloorService floorService;
    private final SeatService seatService;

    public SeatController(RoomService roomService, FloorService floorService, SeatService seatService) {
        this.roomService = roomService;
        this.floorService = floorService;
        this.seatService = seatService;
    }

    @GetMapping("/listAllSeats")
    public ResponseEntity<String> listAllSeats(@RequestBody Custom custom)throws CustException {
       roomService.validateRoom(custom);
       return seatService.listAllSeats(custom);
    }
}
