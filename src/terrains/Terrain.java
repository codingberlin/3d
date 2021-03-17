package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTextureePack terrainTexturePack;
	private float[][] heights;
	private Loader loader;

	public Terrain(final int gridX, final int gridZ, final Loader loader, final TerrainTextureePack terrainTextureePack, final String heightMap){
		this.loader = loader;
		this.terrainTexturePack = terrainTextureePack;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		loadHeightsFromHeightmap(heightMap);
		this.model = generateTerrain();
	}

	public TerrainTextureePack getTerrainTexturePack() {
		return terrainTexturePack;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	private RawModel generateTerrain(){
		int count = heights.length * heights.length;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(heights.length-1)*(heights.length-1)];
		int vertexPointer = 0;
		for(int i=0;i<heights.length;i++){
			for(int j=0;j<heights.length;j++){
				vertices[vertexPointer*3] = (float)j/((float)heights.length - 1) * SIZE;
				vertices[vertexPointer*3+1] = getHeight(j, i);
				vertices[vertexPointer*3+2] = (float)i/((float)heights.length - 1) * SIZE;
				final var normal = calculateNormal(j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)heights.length - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)heights.length - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<heights.length-1;gz++){
			for(int gx=0;gx<heights.length-1;gx++){
				int topLeft = (gz*heights.length)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*heights.length)+gx;
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

	private void loadHeightsFromHeightmap(final String heightMap) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		for(int i=0;i<VERTEX_COUNT;i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				heights[i][j] = getHeightFromImage(j,i, image);
			}
		}
	}

	private Vector3f calculateNormal(final int x, final int y) {
		final var heightL = getHeight(x-1, y);
		final var heightR = getHeight(x+1, y);
		final var heightD = getHeight(x, y-1);
		final var heightU = getHeight(x, y+1);
		final var normal = new Vector3f(heightL-heightR, 2F, heightD  - heightU);
		normal.normalise();
		return normal;
	}

	private float getHeight(final int x, final int y) {
		if (x < 0 || x >= heights.length || y < 0 || y >= heights.length) {
			return 0;
		}
		return heights[x][y];
	}

	private float getHeightFromImage(final int x, final int y, final BufferedImage heightMap) {
		if (x<0 || x>=heightMap.getHeight() || y<0 || y>=heightMap.getHeight()) {
			return 0;
		}
		float height = heightMap.getRGB(x, y);
		height += MAX_PIXEL_COLOR/2F;
		height /= MAX_PIXEL_COLOR/2F;
		height *= MAX_HEIGHT;
		return height;
	}

}
