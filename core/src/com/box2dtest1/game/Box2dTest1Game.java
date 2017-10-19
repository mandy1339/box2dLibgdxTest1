package com.box2dtest1.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import static com.box2dtest1.game.utils.Constants.PPM;

public class Box2dTest1Game extends ApplicationAdapter {

	// CLASS INSTANCE DATA
	// -	-	-	-	-	-	-	-	-	-	-	-	-	-
	private boolean DEBUG =  false;
	private final float SCALE = 2.0f;
	private OrthographicCamera camera;
	private World world;
	private Body player;
	private Body platform;
	private Box2DDebugRenderer b2dr;			// View debug borders
	private SpriteBatch batch;					// Our batch to print things
	private Texture vampTex;

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / SCALE, h / SCALE);

		world = new World(new Vector2(0, -9.8f), false);
		b2dr = new Box2DDebugRenderer();

		player = createPlayer();
		platform = createPlatform();
		batch = new SpriteBatch();
		vampTex = new Texture("loving-vampire2.png");
	}

	@Override
	public void render () {
		update(1f);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		b2dr.render(world, camera.combined.scl(PPM));		// Render the debugging world / shapes

		// Rendering Batch (keep logic off of here
		batch.begin();
		// Draw the texture where the square of player is
		batch.draw(vampTex, player.getPosition().x * PPM - vampTex.getWidth()/2, player.getPosition().y * PPM - vampTex.getHeight()/2);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width / SCALE, height / SCALE);
	}

	@Override
	public void dispose () {
		b2dr.dispose();
	}



	// HELPING FUNCTIONS
	// -	-	-	-	-	-	-	-	-	-	-	-	-	-
	public void update(float delta) {
		world.step(1/ 60f, 6, 2);					// Calculate how smooth the motion is
		inputUpdate(delta);
		cameraUpdate(delta);						// Center camera on player via helping function
		batch.setProjectionMatrix(camera.combined);

	}

	public void inputUpdate(float delta) {
		int horizontalForce = 0;
		int verticalForce = 0;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			horizontalForce -= 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			horizontalForce += 1;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			player.setLinearVelocity(player.getLinearVelocity().x, 0);	// Reset vertical accel before reapplying next jump
			player.applyForceToCenter(0, 400, false);
		}

		player.setLinearVelocity(horizontalForce * 5, player.getLinearVelocity().y);
	}

	public void cameraUpdate(float delta) {
		Vector3 position = camera.position;			// Create a coordinates holder
		position.x = player.getPosition().x * PPM;	// Get x coordinate of player
		position.y = player.getPosition().y * PPM;	// Get y coordinate of player
		camera.position.set(position);				// Set cam to those coordinates
		camera.update();							// Update the cam
	}

	public Body createPlayer() {
		Body pBody;
		BodyDef def = new BodyDef();				// Need this to define body properties
		def.type = BodyDef.BodyType.DynamicBody;	// Dynamic == moves
		def.position.set(20 / PPM, 400 / PPM);					// set position of body
		def.fixedRotation = true;					// prevent rotation interference from forces
		pBody = world.createBody(def);				// Create the pbody in the world
		PolygonShape shape = new PolygonShape();	// Create a shape to give to body later
		shape.setAsBox(32/2 / PPM, 32/2 / PPM);		// Divide by pixels per minute
		pBody.createFixture(shape, 1.0f);			// Give shape to body
		shape.dispose();							// Dispose shape right after using it
		return pBody;
	}

	public Body createPlatform() {
		Body pBody;
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.StaticBody;
		def.position.set(20 / PPM, 0 / PPM) ;
		def.fixedRotation = true;
		pBody = world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(64/ 2 / PPM, 16/ 2 / PPM);
		pBody.createFixture(shape, 1.0f);
		shape.dispose();
		return pBody;
	}
}

