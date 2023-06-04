package com.mis.controllers;

import com.mis.CustException.CustException;
import com.mis.bookingmodels.Building;
import com.mis.bookingservices.BookingService;
import com.mis.bookingservices.BuildingService;
import com.mis.bookingservices.UserService;
import com.mis.customclasses.Custom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BuildingController {

    private final BuildingService buildingService;
    private final UserService userService;
    private final BookingService bookingService;

    public BuildingController(BuildingService buildingService, UserService userService, BookingService bookingService) {
        this.buildingService = buildingService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping("/addBuilding")
    public ResponseEntity<String> addBuilding(@RequestBody Custom custom) throws CustException {
        //verifyUser
        if(!userService.verifyUser(custom.getUser()))
        {
            return new ResponseEntity<>("Invalid Password",HttpStatus.FORBIDDEN);
        }

        //user verified, now adding building

        buildingService.addBuilding(custom.getBuilding());

        return new ResponseEntity<>("Bravo...you added a new building",HttpStatus.ACCEPTED);
    }
    @GetMapping("/findByBuilding")
    public ResponseEntity<String> findByBuilding(@RequestBody Custom custom) throws CustException {
        if(!userService.verifyUser(custom.getUser())){
            return new ResponseEntity<>("No such user exist.", HttpStatus.BAD_REQUEST);
        }
        return buildingService.findByBuilding(custom);
    }
    @DeleteMapping("/deletebuilding")
    public ResponseEntity<String> deleteBuilding(@RequestBody Building building) throws CustException {
        buildingService.deletebuilding(building);
        return new ResponseEntity<>("Building Deleted Successfully",HttpStatus.ACCEPTED);
    }
}
