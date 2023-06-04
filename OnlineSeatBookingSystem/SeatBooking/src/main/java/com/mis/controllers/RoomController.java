package com.mis.controllers;

import com.mis.CustException.CustException;
import com.mis.bookingmodels.Floor;
import com.mis.bookingmodels.Room;
import com.mis.bookingservices.*;
import com.mis.customclasses.Custom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {
    private final UserService userService;
    private final BuildingService buildingService;
    private final FloorService floorService;
    private final RoomService roomService;
    public RoomController(UserService userService, BuildingService buildingService, FloorService floorService, RoomService roomService){
        this.userService = userService;
        this.buildingService = buildingService;
        this.floorService = floorService;
        this.roomService = roomService;
    }

    @PostMapping("/addNewRoom")
    public ResponseEntity<String> addNewRoom(@RequestBody Custom custom) throws CustException {
        userService.verifyUser(custom.getUser());
        buildingService.verifyBuilding(custom.getBuildingName());
        if(!floorService.verifyFloor(custom))throw new CustException("No such floor exist");
        return roomService.addRoom(custom);
    }
    @GetMapping("/listAllRooms")
    public ResponseEntity<String> ListAllRooms(@RequestBody Custom custom) throws CustException {
        userService.verifyUser(custom.getUser());
        buildingService.verifyBuilding(custom.getBuilding().getBuildingName());
        floorService.verifyFloor(custom);
        List<Room> roomList =  roomService.getAllRooms(custom.getBuilding().getBuildingName(), custom.getFloor().getFloorNo());
        StringBuilder rooms =  new StringBuilder();
        rooms.append("Rooms Available:-\n");
        for(Room r: roomList){
            rooms.append(r.toString1()).append("\n");
        }
        return new ResponseEntity<>(rooms.toString(), HttpStatus.FOUND);
    }
}
