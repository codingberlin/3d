package engineTester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.*;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.*;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) throws IOException {

		DisplayManager.createDisplay();
		Loader loader = new Loader();


		RawModel model = OBJLoader.loadObjModel("tree", loader);

		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree")));
		final var houseModel = new TexturedModel(OBJLoader.loadObjModel("house", loader),new ModelTexture(loader.loadTexture("image")));

		final var map = new GameMap();
		final var grassTexture = new TerrainTexture(loader.loadTexture("grass"));
		final var snowTexture = new TerrainTexture(loader.loadTexture("snow"));
		final var terrainTexturePack = new TerrainTextureePack(grassTexture, snowTexture);
		final var terrainTile = new TerrainTile(map, loader, terrainTexturePack);

		final var buildingLoader = new BuildingLoader();
		final var house1 = new Building(new XY(80, 10), buildingLoader.loadBuilding("house"));
		final var house2 = new Building(new XY(80, 40), buildingLoader.loadBuilding("house"));
		if (map.doesBuildingFit(house1)) {
			map.placeBuilding(house1);
		} else {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ 1 passt nicht");
		}
		if (map.doesBuildingFit(house2)) {
			map.placeBuilding(house2);
		} else {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ 2 passt nicht");
		}

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
		entities.add(new Entity(houseModel, new Vector3f(0,0,-200),0,0,0,1));
		entities.add(new Entity(houseModel, new Vector3f(50,0,-150),0,0,0,1));

		Light light = new Light(new Vector3f(-20000,20000,2000),new Vector3f(1,1,1));

		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()){
			camera.move();
			renderer.processTerrain(terrainTile);
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
