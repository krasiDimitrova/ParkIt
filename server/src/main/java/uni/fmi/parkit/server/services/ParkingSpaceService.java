package uni.fmi.parkit.server.services;

import uni.fmi.parkit.server.models.ParkingSpace;

public interface ParkingSpaceService {

    /**
     * Get the parking space by id
     * @param parkingSpaceId
     * @return {@link ParkingSpace}
     */
    ParkingSpace get(Long parkingSpaceId);

    /**
     * Onboard a parking space
     * @param parkingSpaceSpec
     * @return {@link ParkingSpace}
     */
    ParkingSpace onboard(ParkingSpaceSpec parkingSpaceSpec);

    /**
     * Update the free status flag of the {@link ParkingSpace} with given sensor identifier
     * @param parkingSpaceFreeStatusSpec
     */
    void changeParkingSpaceFreeStatus(ParkingSpaceFreeStatusSpec parkingSpaceFreeStatusSpec);

    /**
     * Find the free parking spaces in the given radius of the given coordinates
     * @param parkingSpaceCoordinatesFilterSpec
     * @return {@link }
     */
    ParkingSpace findTheClosestFreeParkingSpace(ParkingSpaceCoordinatesFilterSpec parkingSpaceCoordinatesFilterSpec);

    void reserveParkingSpaceForCurrentUser(Long parkingSpaceId);
}
