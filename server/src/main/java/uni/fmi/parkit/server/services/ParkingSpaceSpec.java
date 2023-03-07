package uni.fmi.parkit.server.services;

import javax.validation.constraints.NotNull;
import java.util.StringJoiner;

public class ParkingSpaceSpec {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private String sensorIdentifier;

    private boolean isFree;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", ParkingSpaceSpec.class.getSimpleName() + "[", "]")
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .add("sensorIdentifier='" + sensorIdentifier + "'")
                .add("isFree=" + isFree)
                .toString();
    }
}
