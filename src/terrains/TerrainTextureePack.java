package terrains;

public class TerrainTextureePack {

    private final TerrainTexture grass;
    private final TerrainTexture snow;

    public TerrainTextureePack(TerrainTexture grass, TerrainTexture snow) {
        this.grass = grass;
        this.snow = snow;
    }

    public TerrainTexture getGrass() {
        return grass;
    }

    public TerrainTexture getSnow() {
        return snow;
    }
}
