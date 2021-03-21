package terrains;

import models.RawModel;
import renderEngine.Loader;

public class TerrainTile {

    public final GameMap gameMap;
    public final TerrainTextureePack terrainTexturePack;
    public final Loader loader;
    public RawModel rawModel;

    public TerrainTile(final GameMap gameMap, final Loader loader, final TerrainTextureePack terrainTexturePack) {
        this.terrainTexturePack = terrainTexturePack;
        this.loader = loader;
        this.gameMap = gameMap;
        this.rawModel = gameMap.generateRawModel(loader);
    }

    public RawModel getRawModel() {
        // TODO remove old raw model
        if (gameMap.hasDepictionChanged()) {
            rawModel = gameMap.generateRawModel(loader);
        }
        return rawModel;
    }

    public TerrainTextureePack getTerrainTexturePack() {
        return terrainTexturePack;
    }

    public float getX() {
        return 0;
    }

    public float getY() {
        return 0;
    }
}
