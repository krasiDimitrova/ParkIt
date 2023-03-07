package uni.fmi.parkit.server.models;

import com.fasterxml.jackson.annotation.JsonView;
import uni.fmi.parkit.server.View;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.StringJoiner;

@Entity
@Table(name = "parking_space")
public class ParkingSpace extends BaseEntity {
    @NotNull
    @JsonView(View.PublicParkingSpaceView.class)
    private Double latitude;

    @NotNull
    @JsonView(View.PublicParkingSpaceView.class)
    private Double longitude;

    @NotNull
    @Column(unique = true)
    private String sensorIdentifier;

    @JsonView(View.PublicParkingSpaceView.class)
    private boolean isFree;

    private Long reservationStartTimestamp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by", referencedColumnName = "id", nullable = false)
    private ParkItUser reservedBy;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSensorIdentifier() {
        return sensorIdentifier;
    }

    public void setSensorIdentifier(String sensorIdentifier) {
        this.sensorIdentifier = sensorIdentifier;
    }

    public boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public Long getReservationStartTimestamp() {
        return reservationStartTimestamp;
    }

    public void setReservationStartTimestamp(Long reservationStartTimestamp) {
        this.reservationStartTimestamp = reservationStartTimestamp;
    }

    public ParkItUser getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(ParkItUser reservedBy) {
        this.reservedBy = reservedBy;
    }

    @JsonView(View.PublicParkingSpaceView.class)
    public boolean getIsReserved() {
        return getReservedBy() != null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParkingSpace.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .add("sensorIdentifier='" + sensorIdentifier + "'")
                .add("isFree=" + isFree)
                .toString();
    }
}
