package terrains;

import java.util.List;

public class BuildingProperty {

    private final List<XY> blocking;
    private final List<XY> additionalReserving;

    public BuildingProperty(final List<XY> blocking, final List<XY> additionalReserving) {
        this.blocking = blocking;
        this.additionalReserving = additionalReserving;
    }

    public List<XY> getBlocking() {
        return blocking;
    }

    public List<XY> getAdditionalReserving() {
        return additionalReserving;
    }
}
