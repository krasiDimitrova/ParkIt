package uni.fmi.parkit.server.services;

import javax.validation.constraints.NotNull;
import java.util.StringJoiner;

public class ParkingSpaceFreeStatusSpec {

    @NotNull
    private String sensorIdentifier;

    private boolean isFree;

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
        return new StringJoiner(", ", ParkingSpaceFreeStatusSpec.class.getSimpleName() + "[", "]")
                .add("sensorIdentifier='" + sensorIdentifier + "'")
                .add("isFree=" + isFree)
                .toString();
    }
}
