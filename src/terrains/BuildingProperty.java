package terrains;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildingProperty {

    private final XY entrance;
    private final List<XY> blocking;
    private final List<XY> additionalReserving;

    public BuildingProperty(final XY entrance, final List<XY> blocking, final List<XY> additionalReserving) {
        this.entrance = entrance;
        this.blocking = blocking;
        this.additionalReserving = Stream.concat(Stream.of(entrance), additionalReserving.stream()).collect(Collectors.toList());
    }

    public XY getEntrance() {
        return entrance;
    }

    public List<XY> getBlocking() {
        return blocking;
    }

    public List<XY> getAdditionalReserving() {
        return additionalReserving;
    }
}
