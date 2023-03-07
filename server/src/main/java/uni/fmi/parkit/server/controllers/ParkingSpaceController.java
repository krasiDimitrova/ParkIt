package uni.fmi.parkit.server.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uni.fmi.parkit.server.View;
import uni.fmi.parkit.server.models.ParkingSpace;
import uni.fmi.parkit.server.services.ParkingSpaceCoordinatesFilterSpec;
import uni.fmi.parkit.server.services.ParkingSpaceFreeStatusSpec;
import uni.fmi.parkit.server.services.ParkingSpaceService;
import uni.fmi.parkit.server.services.ParkingSpaceSpec;

@RestController("parkingSpaceController")
public class ParkingSpaceController {

    public static final String PARKING_SPACE_STATUS_UPDATE_ENDPOINT = "/api/v1/parking-space/status";

    public static final String PARKING_SPACE_ONBOARDING_ENDPOINT = "/api/v1/parking-space/onboard";

    private ParkingSpaceService parkingSpaceService;

    @Autowired
    public void setParkingSpaceService(ParkingSpaceService parkingSpaceService) {
        this.parkingSpaceService = parkingSpaceService;
    }

    @GetMapping("/api/v1/parking-space")
    @ResponseStatus(value = HttpStatus.OK)
    @JsonView(View.PublicParkingSpaceView.class)
    public ParkingSpace find(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam Double radius) {
        ParkingSpaceCoordinatesFilterSpec parkingSpaceCoordinatesFilterSpec = new ParkingSpaceCoordinatesFilterSpec();
        parkingSpaceCoordinatesFilterSpec.setLatitude(latitude);
        parkingSpaceCoordinatesFilterSpec.setLongitude(longitude);
        parkingSpaceCoordinatesFilterSpec.setRadius(radius);
        return parkingSpaceService.findTheClosestFreeParkingSpace(parkingSpaceCoordinatesFilterSpec);
    }

    @GetMapping("/api/v1/parking-space/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    @JsonView(View.PublicParkingSpaceView.class)
    public ParkingSpace get(@PathVariable Long id) {
        return parkingSpaceService.get(id);
    }

    @PostMapping(PARKING_SPACE_ONBOARDING_ENDPOINT)
    @ResponseStatus(value = HttpStatus.OK)
    public ParkingSpace onboard(@RequestBody ParkingSpaceSpec parkingSpaceSpec) {
        return parkingSpaceService.onboard(parkingSpaceSpec);
    }

    @PostMapping(PARKING_SPACE_STATUS_UPDATE_ENDPOINT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void changeFreeStatus(@RequestBody ParkingSpaceFreeStatusSpec parkingSpaceFreeStatusSpec) {
        parkingSpaceService.changeParkingSpaceFreeStatus(parkingSpaceFreeStatusSpec);
    }

    @PostMapping("/api/v1/parking-space/{id}/reserve")
    @ResponseStatus(value = HttpStatus.OK)
    public void reserve(@PathVariable Long id) {
        parkingSpaceService.reserveParkingSpaceForCurrentUser(id);
    }
}
