package com.mis.bookingservices;

import com.mis.CustException.CustException;
import com.mis.bookingmodels.Floor;
import com.mis.bookingrepositories.FloorRepo;
import com.mis.customclasses.Custom;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FloorService {
    private final FloorRepo floorRepo;
    private final BuildingService buildingService;

    public FloorService(FloorRepo floorRepo, BuildingService buildingService) {
        this.floorRepo = floorRepo;
        this.buildingService = buildingService;
    }
    public void addFloor(Floor floor) {
        floorRepo.save(floor);
    }

    public boolean verifyFloor(Custom custom) throws CustException {
        boolean opt = buildingService.verifyBuilding(custom.getBuildingName());

        if(opt == false){
            throw new CustException("No mentioned floor found in given building");
        }
        Optional<Floor> opt1 = floorRepo.findFloor(custom.getFloor().getFloorNo(), custom.getBuilding().getBuildingName());
        if(opt1.isEmpty()){
            throw new CustException("No mentioned floor available");
        }
        return true;
    }

    public List<Floor> listAllFloors(String buildingName) {
        return floorRepo.findAllFloors(buildingName);
    }

    public Optional<Floor> getFloor(Custom custom) {
        Optional<Floor> opt1 = floorRepo.findFloor(custom.getFloor().getFloorNo(), custom.getBuilding().getBuildingName());
        return opt1;
    }

    public void updateCapacity(String buildingName, Integer floorNo, int numberOfSeats) {
        floorRepo.updateCapacity(buildingName, floorNo, numberOfSeats);
    }
}
