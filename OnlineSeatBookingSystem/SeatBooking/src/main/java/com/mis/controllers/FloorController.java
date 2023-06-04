package com.mis.controllers;

import com.mis.CustException.CustException;
import com.mis.bookingmodels.Building;
import com.mis.bookingmodels.Floor;
import com.mis.bookingmodels.User;
import com.mis.bookingservices.BuildingService;
import com.mis.bookingservices.FloorService;
import com.mis.bookingservices.UserService;
import com.mis.customclasses.Custom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class FloorController {

    private final FloorService floorService;
    private final UserService userService;
    private final BuildingService buildingService;

    public FloorController(FloorService floorService, UserService userService, BuildingService buildingService) {
        this.floorService = floorService;
        this.userService = userService;
        this.buildingService = buildingService;

    }

    @PostMapping("/addFloor")
    public ResponseEntity<String> addFloor(@RequestBody Custom custom) throws CustException {
       //verify user.
        if(!userService.verifyUser(custom.getUser()))throw new CustException("No Such User Exist");
        //verifyBuilding
        if(!buildingService.verifyBuilding(custom.getBuildingName()))throw new CustException("No Such Building Exist");
        Optional<Floor> fl =  floorService.getFloor(custom);
        if(fl.isPresent())throw new CustException("Floor already Exist");
        Building b = buildingService.getBuilding(custom.getBuilding());
        Floor newf = new Floor(custom.getFloor().getFloorNo(), custom.getFloor().getFloorCapacity(),b);
        floorService.addFloor(newf);
        return new ResponseEntity<>("Floor in corresponding Building Added successfully", HttpStatus.ACCEPTED);
    }
    @GetMapping("/findByFloor")
    public ResponseEntity<String> findByFloor(Custom custom) throws CustException{
        //verifyBuilding
        if(!buildingService.verifyBuilding(custom.getBuildingName()))throw new CustException("No Such Building Exist");
        List<Floor> floorList = floorService.listAllFloors(custom.getBuildingName());
        StringBuilder floors = new StringBuilder();
        floors.append("Floors Registered:\n");
        for(Floor f: floorList){
            floors.append(f.toString1()).append("\n");
        }
        return new ResponseEntity<>(floors.toString(), HttpStatus.FOUND);
    }
}
