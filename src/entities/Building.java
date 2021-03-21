package entities;

import terrains.BuildingProperty;
import terrains.XY;

public class Building {

    private final BuildingProperty buildingProperty;
    private final XY position;

    public Building(final XY position, final BuildingProperty buildingProperty) {
        this.buildingProperty = buildingProperty;
        this.position = position;
    }

    public BuildingProperty getBuildingProperty() {
        return buildingProperty;
    }

    public XY getPosition() {
        return position;
    }
}
