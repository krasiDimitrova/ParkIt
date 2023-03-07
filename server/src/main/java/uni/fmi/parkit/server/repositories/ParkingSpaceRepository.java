package uni.fmi.parkit.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uni.fmi.parkit.server.models.ParkingSpace;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    /**
     * Find a parking space associated with a given sensor identifier
     *
     * @param sensorIdentifier
     * @return {@link ParkingSpace}
     */
    ParkingSpace findBySensorIdentifier(String sensorIdentifier);


    /**
     * Find all parking spaces that are in the given radius of the given coordinates
     *
     * @param searchLongitude
     * @param searchLatitude
     * @param radius
     * @return {@link  List} of {@link ParkingSpace}
     */
    @Query(value = "SELECT parkingSpace "
            + "FROM ParkingSpace parkingSpace "
            + "WHERE parkingSpace.isFree IS TRUE AND parkingSpace.reservedBy IS NULL "
            + "AND parkingSpace.latitude BETWEEN (?2 - (?3 / 111.045)) AND (?2 + (?3 / 111.045)) "
            + "AND parkingSpace.longitude BETWEEN (?1 - (?3 / (111.045 * COS(RADIANS(?1))))) AND (?1 + (?3 / (111.045 * COS(RADIANS(?1)))))")
    List<ParkingSpace> findAllNotReservedCloseInRadiusAndInCoordinates(Double searchLongitude, Double searchLatitude, double radius);

    /**
     * Check if a parking space with the given sensor identifier already exists
     * @param sensorIdentifier
     * @return boolean
     */
    boolean existsBySensorIdentifier(String sensorIdentifier);

    /**
     * Check if a parking space with the given coordinates already exists
     * @param longitude
     * @param latitude
     * @return boolean
     */
    boolean existsByLongitudeAndLatitude(Double longitude, Double latitude);

    /**
     * Check if parking space reserved by user with given id already exists
     * @param userId
     * @return boolean
     */
    boolean existsByReservedById(Long userId);

    List<ParkingSpace> findAllByReservedByIsNotNullAndReservationStartTimestampIsBefore(long reservationTimestamp);
}
