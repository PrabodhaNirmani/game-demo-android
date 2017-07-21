package com.irononetech.prabodhah.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import java.util.HashMap;

public class MyGdxGame extends ApplicationAdapter {

	static final float STEP_TIME = 1f / 60f;
	static final int VELOCITY_ITERATIONS = 6;
	static final int POSITION_ITERATIONS = 2;
	static final float SCALE = 0.05f;

	TextureAtlas textureAtlas;
	SpriteBatch batch;
	final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

	OrthographicCamera camera;
	ExtendViewport viewport;

	World world;
	Box2DDebugRenderer debugRenderer;
	PhysicsShapeCache physicsBodies;
	Body banana;
	float accumulator = 0;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(50, 50, camera);

		batch = new SpriteBatch();
		textureAtlas = new TextureAtlas("sprites.txt");
		addSprites();

		Box2D.init();
		world = new World(new Vector2(0, -10), true);
		physicsBodies = new PhysicsShapeCache("physics.xml");

		debugRenderer = new Box2DDebugRenderer();

		banana = createBody("banana", 10, 50, 0);

	}

	private Body createBody(String name, float x, float y, float rotation) {
		Body body = physicsBodies.createBody(name, world, SCALE, SCALE);
		body.setTransform(x, y, rotation);

		return body;
	}

	private void addSprites(){
		Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

		for (TextureAtlas.AtlasRegion region : regions) {
			Sprite sprite = textureAtlas.createSprite(region.name);

			float width = sprite.getWidth() * SCALE;
			float height = sprite.getHeight() * SCALE;

			sprite.setSize(width,height);

			sprites.put(region.name, sprite);
		}
	}
	private void stepWorld() {
		float delta = Gdx.graphics.getDeltaTime();

		accumulator += Math.min(delta, 0.25f);

		if (accumulator >= STEP_TIME) {
			accumulator -= STEP_TIME;

			world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}
	}

	private void drawSprite(String name, float x, float y){
		Sprite sprite = sprites.get(name);

		sprite.setPosition(x, y);

		sprite.draw(batch);
	}





	@Override
	public void render () {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
//		Gdx.gl.glClearColor(0.57f, 0.77f, 0.85f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stepWorld();

		batch.begin();

		Vector2 position = banana.getPosition();
		drawSprite("banana", position.x, position.y);
//		drawSprite("banana",0,0);
//		drawSprite("cherries",5,5);
		batch.end();
		debugRenderer.render(world, camera.combined);
	}
	
	@Override
	public void dispose () {
		textureAtlas.dispose();
		batch.dispose();


		sprites.clear();
		world.dispose();
		debugRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height,true);
		batch.setProjectionMatrix(camera.combined);
	}
}
