package entities;

import terrains.BuildingProperty;
import terrains.XY;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildingLoader {
    final HashMap<String, BuildingProperty> buildingProperties = new HashMap<>();

    public BuildingProperty loadBuilding(final String filename) throws IOException {
        if (buildingProperties.containsKey(filename)) {
            return buildingProperties.get(filename);
        }

        final var br = new BufferedReader(new FileReader(new File("res/" + filename + ".building")));
        XY entrance = null;
        var blocked = new ArrayList<XY>();
        var reserved = new ArrayList<XY>();
        String objectFile;
        String textureFile;
        String line;
        var i = 0;
        while ((line = br.readLine()) != null) {
            if (i == 0) {
                objectFile = line;
            } else if (i == 1) {
                textureFile = line;
            } else {
                final var x = i - 2;
                final var chars = line.toCharArray();
                for (int y = 0; y < line.length(); y++) {
                    final var character = chars[y];
                    if (character == 'E') {
                        entrance = new XY(x, y);
                    } else if (character == 'B') {
                        blocked.add(new XY(x, y));
                    } else if (character == 'R') {
                        reserved.add(new XY(x, y));
                    }
                }
            }
            i++;
        }
        for (var xy:blocked) {
            xy.adjustToNewZero(entrance);
        }
        for (var xy:reserved) {
            xy.adjustToNewZero(entrance);
        }
        entrance.adjustToNewZero(entrance);
        final var buildingProperty = new BuildingProperty(entrance, blocked, reserved);
        buildingProperties.put(filename, buildingProperty);
        return buildingProperty;
    }
}
