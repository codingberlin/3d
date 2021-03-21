package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameMap {

    private static final int SIZE = 100;
    private boolean depictionHasChanged = true;
    private final int[][] height = new int[SIZE][SIZE];
    private boolean[][] blocked = new boolean[SIZE][SIZE];
    private boolean[][] reserved = new boolean[SIZE][SIZE];

    public GameMap() {
        setupTerrain();
    }

    private void setupTerrain() {
        IntStream.range(0, SIZE).forEach(x ->
                IntStream.range(0, SIZE).forEach(y -> {
				    height[x][y] = y % 3;
				    blocked[x][y] = false;
                    reserved[x][y] = false;
                })
        );
    }

    public boolean doesBuildingFit(final int x, final int y, final BuildingProperty building) {
        return !Stream.concat(building.getBlocking().stream(), building.getAdditionalReserving().stream()).anyMatch(xy -> {
            final var absoluteX = x + xy.getX();
            final var absoluteY = y + xy.getY();
            return absoluteX < 0 || absoluteX >= SIZE || absoluteY < 0 || absoluteY >= SIZE || blocked[absoluteX][absoluteY] || reserved[absoluteX][absoluteY];
        });
    }

    public void placeBuilding(final int x, final int y, final BuildingProperty building) {
        depictionHasChanged = true;
        var heightSum = 0;
        for (var xy:building.getBlocking()) {
            final var absoluteX = x + xy.getX();
            final var absoluteY = y + xy.getY();
            blocked[absoluteX][absoluteY] = true;
            reserved[absoluteX][absoluteY] = true;
            heightSum += height[absoluteX][absoluteX];
        };
        building.getAdditionalReserving().forEach(xy -> {
            final var absoluteX = x + xy.getX();
            final var absoluteY = y + xy.getY();
            reserved[absoluteX][absoluteY] = true;
        });
        var newHeight = Math.round((float) heightSum / (float) building.getBlocking().size());
        for (var xy:building.getBlocking()) {
            final var absoluteX = x + xy.getX();
            final var absoluteY = y + xy.getY();
            height[absoluteX][absoluteY] = newHeight;
        }
    }

    public RawModel generateRawModel(final Loader loader) {
        this.depictionHasChanged = false;
        int count = SIZE * SIZE;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (SIZE - 1) * (SIZE - 1)];
        int vertexPointer = 0;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                vertices[vertexPointer * 3] = (float) y / ((float) SIZE - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = height[x][y];
                vertices[vertexPointer * 3 + 2] = (float) x / ((float) SIZE - 1) * SIZE;
                final var normal = calculateNormal(x, y);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) y / ((float) SIZE - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) x / ((float) SIZE - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < SIZE - 1; gz++) {
            for (int gx = 0; gx < SIZE - 1; gx++) {
                int topLeft = (gz * SIZE) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * SIZE) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    public boolean hasDepictionChanged() {
        return depictionHasChanged;
    }

    private Vector3f calculateNormal(final int x, final int y) {
        final var xx = Math.max(1, Math.min(SIZE - 2, x));
        final var yy = Math.max(1, Math.min(SIZE - 2, y));
        final var heightL = height[xx - 1][yy];
        final var heightR = height[xx + 1][yy];
        final var heightD = height[xx][yy - 1];
        final var heightU = height[xx][yy + 1];
        final var normal = new Vector3f(heightL - heightR, 2F, heightD - heightU);
        normal.normalise();
        return normal;
    }
}
