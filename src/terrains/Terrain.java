package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.ModelTexture;

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
	private ModelTexture texture;
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightMap){
		this.texture = texture;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
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



	public ModelTexture getTexture() {
		return texture;
	}

	private RawModel generateTerrain(Loader loader, String heightMap){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final int VERTEX_COUNT = image.getHeight();
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = getHeight(j, i, image);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				final var normal = calculateNormal(j, i, image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
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

	private Vector3f calculateNormal(final int x, final int y, final BufferedImage heightMap) {
		final var heightL = getHeight(x-1, y, heightMap);
		final var heightR = getHeight(x+1, y, heightMap);
		final var heightD = getHeight(x, y-1, heightMap);
		final var heightU = getHeight(x, y+1, heightMap);
		final var normal = new Vector3f(heightL-heightR, 2F, heightD  - heightU);
		normal.normalise();
		return normal;
	}

	private float getHeight(final int x, final int y, final BufferedImage heightMap) {
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
