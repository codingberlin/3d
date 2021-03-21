package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		final var map = new GameMap();
		final var buildingProperty = new BuildingProperty(List.of(
				new XY(1,1),
		        new XY(2,1),
				new XY(5,1),
				new XY(6,1),
				new XY(1,2),
				new XY(2,2),
				new XY(5,2),
				new XY(6,2),
				new XY(1,3),
				new XY(2,3),
				new XY(3,3),
				new XY(4,3),
				new XY(5,3),
				new XY(6,3),
				new XY(1,4),
				new XY(2,4),
				new XY(3,4),
				new XY(4,4),
				new XY(5,4),
				new XY(6,4)
		), List.of(
				new XY(0,0),
				new XY(1,0),
				new XY(2,0),
				new XY(3,0),
				new XY(4,0),
				new XY(5,0),
				new XY(6,1),
				new XY(7,1),
				new XY(0,1),
				new XY(3,1),
				new XY(4,1),
				new XY(7,1),
				new XY(0,2),
				new XY(3,2),
				new XY(4,2),
				new XY(7,2),
				new XY(0,3),
				new XY(7,3),
				new XY(0,4),
				new XY(7,4),
				new XY(0,7),
				new XY(1,7),
				new XY(2,7),
				new XY(3,7),
				new XY(4,7),
				new XY(5,7),
				new XY(6,7),
				new XY(7,7)
		));
		if (map.doesBuildingFit(10, 10, buildingProperty)) {
			map.placeBuilding(10, 10, buildingProperty);
		} else {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ passt nicht");
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
