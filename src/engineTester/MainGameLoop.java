package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTextureePack;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		final var grassTexture = new TerrainTexture(loader.loadTexture("grass"));
		final var snowTexture = new TerrainTexture(loader.loadTexture("snow"));
		final var terrainTexturePack = new TerrainTextureePack(grassTexture, snowTexture);

		RawModel model = OBJLoader.loadObjModel("tree", loader);

		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
		final var houseModel = new TexturedModel(OBJLoader.loadObjModel("house", loader),new ModelTexture(loader.loadTexture("image")));

		final var terrain = new Terrain(0,-1,loader,terrainTexturePack, "heightmap3");
		final var terrain2 = new Terrain(-1,-1,loader,terrainTexturePack, "heightmap3");

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		final var x = 10;
		final var z = -100;
		entities.add(new Entity(staticModel, new Vector3f(x, 0, z),0,0,0,3));
		//for(int i=0;i<500;i++){
		//	final var x = random.nextFloat()*800 - 400;
		//	final var z = random.nextFloat() * -600;
		//	entities.add(new Entity(staticModel, new Vector3f(x, terrain.getHeightAt(x, z) + terrain2.getHeightAt(x, z), z),0,0,0,3));
		//}
		entities.add(new Entity(houseModel, new Vector3f(0,0,-200),0,-90,0,6));
		entities.add(new Entity(houseModel, new Vector3f(50,0,-150),0,-90,0,6));

		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));

		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()){
			camera.move();
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
