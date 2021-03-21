package terrains;

import entities.Building;
import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameMap {

    private static final int SIZE = 100;
    private static final int VERTEX_SIZE = SIZE + 1;
    private static final float HEIGHT_FACTOR = 0.5F;
    private boolean depictionHasChanged = true;
    private final int[][] height = new int[SIZE][SIZE];
    private boolean[][] blocked = new boolean[SIZE][SIZE];
    private boolean[][] reserved = new boolean[SIZE][SIZE];

    public GameMap() {
        setupTerrain();
    }

    private void setupTerrain() {
        final var random = new Random(1701L);
        IntStream.range(0, SIZE).forEach(x ->
                IntStream.range(0, SIZE).forEach(y -> {
                    height[x][y] = random.nextInt(4);
                    blocked[x][y] = false;
                    reserved[x][y] = false;
                })
        );
    }

    public boolean doesBuildingFit(final Building building) {
        return !Stream.concat(building.getBuildingProperty().getBlocking().stream(), building.getBuildingProperty().getAdditionalReserving().stream()).anyMatch(xy -> {
            final var absoluteX = building.getPosition().getX() + xy.getX();
            final var absoluteY = building.getPosition().getY() + xy.getY();
            return absoluteX < 0 || absoluteX >= SIZE || absoluteY < 0 || absoluteY >= SIZE || blocked[absoluteX][absoluteY] || reserved[absoluteX][absoluteY];
        });
    }

    public void placeBuilding(final Building building) {
        depictionHasChanged = true;
        var heightSum = 0;
        for (var xy : building.getBuildingProperty().getBlocking()) {
            final var absoluteX = building.getPosition().getX() + xy.getX();
            final var absoluteY = building.getPosition().getY() + xy.getY();
            blocked[absoluteX][absoluteY] = true;
            reserved[absoluteX][absoluteY] = true;
            heightSum += height[absoluteX][absoluteX];
        }
        building.getBuildingProperty().getAdditionalReserving().forEach(xy -> {
            final var absoluteX = building.getPosition().getX() + xy.getX();
            final var absoluteY = building.getPosition().getY() + xy.getY();
            reserved[absoluteX][absoluteY] = true;
        });
        var newHeight = Math.round((float) heightSum / (float) building.getBuildingProperty().getBlocking().size());
        for (var xy : building.getBuildingProperty().getBlocking()) {
            final var absoluteX = building.getPosition().getX() + xy.getX();
            final var absoluteY = building.getPosition().getY() + xy.getY();
            height[absoluteX][absoluteY] = newHeight;
        }
    }

    public RawModel generateRawModel(final Loader loader) {
        this.depictionHasChanged = false;
        final var count = VERTEX_SIZE * VERTEX_SIZE;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * SIZE * SIZE];
        int vertexPointer = 0;
        for (int x = 0; x < VERTEX_SIZE; x++) {
            for (int y = 0; y < VERTEX_SIZE; y++) {
                vertices[vertexPointer * 3] = (float) y / ((float) SIZE) * VERTEX_SIZE;
                vertices[vertexPointer * 3 + 1] = HEIGHT_FACTOR * vertexHeight(x, y);
                vertices[vertexPointer * 3 + 2] = (float) x / ((float) SIZE) * VERTEX_SIZE;
                final var normal = calculateNormal(x, y);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) y / (float) SIZE;
                textureCoords[vertexPointer * 2 + 1] = (float) x / (float) SIZE;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < SIZE; gz++) {
            for (int gx = 0; gx < SIZE; gx++) {
                int topLeft = (gz * VERTEX_SIZE) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_SIZE) + gx;
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

    private float vertexHeight(final int x, final int y) {
        if (x == 0 && y == 0) {
            return height[x][y];
        } else if (x == 0 && y == SIZE) {
            return height[x][y - 1];
        } else if (x == 0) {
            return ((float) height[x][y] + (float) height[x][y - 1]) / 2F;
        } else if (y == 0 && x == SIZE) {
            return height[x - 1][y];
        } else if (y == 0) {
            return ((float) height[x][y] + (float) height[x - 1][y]) / 2F;
        } else if (x == SIZE && y == SIZE) {
            return height[x - 1][y - 1];
        } else if (x == SIZE) {
            return ((float) height[x - 1][y] + (float) height[x - 1][y - 1]) / 2F;
        } else if (y == SIZE) {
            return ((float) height[x][y - 1] + (float) height[x - 1][y - 1]) / 2F;
        } else {
            return ((float) height[x][y] + (float) height[x][y - 1] + (float) height[x - 1][y] + (float) height[x - 1][y - 1]) / 4F;
        }
    }

    public boolean hasDepictionChanged() {
        return depictionHasChanged;
    }

    private Vector3f calculateNormal(final int x, final int y) {
        final var xx = Math.max(1, Math.min(SIZE - 1, x));
        final var yy = Math.max(1, Math.min(SIZE - 1, y));
        final var heightL = vertexHeight(xx - 1, yy);
        final var heightR = vertexHeight(xx + 1, yy);
        final var heightD = vertexHeight(xx, yy - 1);
        final var heightU = vertexHeight(xx, yy + 1);
        final var normal = new Vector3f(heightL - heightR, 2F, heightD - heightU);
        normal.normalise();
        return normal;
    }
}
