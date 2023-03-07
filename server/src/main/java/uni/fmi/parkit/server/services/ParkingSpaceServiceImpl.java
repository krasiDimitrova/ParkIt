package uni.fmi.parkit.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uni.fmi.parkit.server.models.ParkItUser;
import uni.fmi.parkit.server.models.ParkingSpace;
import uni.fmi.parkit.server.repositories.ParkingSpaceRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingSpaceServiceImpl.class);

    private static final Double DISTANCE_UNIT_IN_KM = 6371.0;

    private ParkingSpaceRepository parkingSpaceRepository;

    private UserService userService;

    @Autowired
    public void setParkingSpaceRepository(ParkingSpaceRepository parkingSpaceRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ParkingSpace get(Long parkingSpaceId) {
        return parkingSpaceRepository.findById(parkingSpaceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("parking space with id does not exists", parkingSpaceId)));
    }

    @Override
    public ParkingSpace onboard(ParkingSpaceSpec parkingSpaceSpec) {

        String sensorIdentifier = parkingSpaceSpec.getSensorIdentifier();
        if (parkingSpaceRepository.existsBySensorIdentifier(sensorIdentifier)) {
            throw new EntityExistsException(String.format("ParkingSpace with sensor identifier %s already exists.", sensorIdentifier));
        }

        Double longitude = parkingSpaceSpec.getLongitude();
        Double latitude = parkingSpaceSpec.getLatitude();
        if (parkingSpaceRepository.existsByLongitudeAndLatitude(longitude, latitude)) {
            throw new EntityExistsException(String.format("ParkingSpace with longitude %s and latitude %s already exists.", longitude, latitude));
        }

        ParkingSpace parkingSpace = new ParkingSpace();
        BeanUtils.copyProperties(parkingSpaceSpec, parkingSpace);

        return parkingSpaceRepository.saveAndFlush(parkingSpace);
    }

    @Override
    public void changeParkingSpaceFreeStatus(ParkingSpaceFreeStatusSpec parkingSpaceFreeStatusSpec) {
        logger.info("Updating parking space status: {}", parkingSpaceFreeStatusSpec);
        ParkingSpace parkingSpace = parkingSpaceRepository.findBySensorIdentifier(parkingSpaceFreeStatusSpec.getSensorIdentifier());

        if (parkingSpace == null) {
            return;
        }

        parkingSpace.setIsFree(parkingSpaceFreeStatusSpec.getIsFree());
        parkingSpace.setReservedBy(null);
        parkingSpace.setReservationStartTimestamp(null);

        parkingSpace = parkingSpaceRepository.saveAndFlush(parkingSpace);
        logger.info("Updated parking space status: {}", parkingSpace);

    }

    @Override
    public ParkingSpace findTheClosestFreeParkingSpace(ParkingSpaceCoordinatesFilterSpec parkingSpaceCoordinatesFilterSpec) {
        double radiusInKilometers = parkingSpaceCoordinatesFilterSpec.getRadius() / 1000d;

        List<ParkingSpace> foundParkingSpaces = parkingSpaceRepository.findAllNotReservedCloseInRadiusAndInCoordinates(
                parkingSpaceCoordinatesFilterSpec.getLongitude(), parkingSpaceCoordinatesFilterSpec.getLatitude(), radiusInKilometers);

        Optional<ParkingSpace> closestParkingSpace = foundParkingSpaces.stream()
                .filter(space -> radiusInKilometers > calculateDistanceBetweenCoordinatePointsWithHaversineFormula(
                        parkingSpaceCoordinatesFilterSpec.getLatitude(), parkingSpaceCoordinatesFilterSpec.getLongitude(),
                        space.getLatitude(), space.getLongitude()))
                .min(Comparator.comparing((ParkingSpace space) -> calculateDistanceBetweenCoordinatePointsWithHaversineFormula(
                        parkingSpaceCoordinatesFilterSpec.getLatitude(), parkingSpaceCoordinatesFilterSpec.getLongitude(),
                        space.getLatitude(), space.getLongitude())));

        if (closestParkingSpace.isEmpty()) {
            throw new EntityNotFoundException("A free parking space for the given location and search radius is not found");
        }

        return closestParkingSpace.get();
    }

    @Override
    public void reserveParkingSpaceForCurrentUser(Long parkingSpaceId) {
        ParkItUser currentUser = userService.getCurrentUser();
        Long currentUserId = currentUser.getId();

        if (parkingSpaceRepository.existsByReservedById(currentUserId)) {
            throw new IllegalStateException(String.format("User with id %s already has a reserved parking space", currentUserId));
        }

        ParkingSpace parkingSpace = parkingSpaceRepository.findById(parkingSpaceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Parking space with id %s does not exists.", parkingSpaceId)));

        if (parkingSpace.getReservedBy() != null) {
            throw new IllegalArgumentException(String.format("Parking space with id %s is already reserved.", parkingSpaceId));
        }

        parkingSpace.setReservedBy(currentUser);
        parkingSpace.setReservationStartTimestamp(System.currentTimeMillis());
        parkingSpaceRepository.saveAndFlush(parkingSpace);
    }

    private double calculateDistanceBetweenCoordinatePointsWithHaversineFormula(double firstLatitude, double firstLongitude,
                                                                                double secondLatitude, double secondLongitude) {
        double latitudeDistance = Math.toRadians(firstLatitude - secondLatitude);
        double longitudeDistance = Math.toRadians(firstLongitude - secondLongitude);
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) +
                Math.cos(Math.toRadians(secondLatitude)) * Math.cos(Math.toRadians(secondLongitude))
                        * Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return DISTANCE_UNIT_IN_KM * c;
    }

    @Scheduled(fixedDelay = 120000, initialDelay = 120000)
    public void clearExpiredReservations() {
        logger.info("Start removing expired reservations");
        Instant expiredReservationsEndTimestamp = Instant.now().minus(15, ChronoUnit.MINUTES);
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findAllByReservedByIsNotNullAndReservationStartTimestampIsBefore(
                expiredReservationsEndTimestamp.toEpochMilli());

        parkingSpaces.forEach(parkingSpace -> {
            parkingSpace.setReservedBy(null);
            parkingSpace.setReservationStartTimestamp(null);
        });

        parkingSpaceRepository.saveAllAndFlush(parkingSpaces);
        logger.info("Finished removing expired reservations");
    }
}
