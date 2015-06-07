/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.vloxlands.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Controls: <br>
 * <ul>
 * <li>x - reset bezier
 * <li>c - copy bezier to clipboard
 * <li>v - load bezier from clipboard</li>
 * <li>b - run a example of possible voxel generation using this bezier curve with custom params</li>
 * <li>n - run a example of possible voxel generation using this bezier curve with last used params</li>
 * <li>SHIFT+DRAG - drag only on X-Axis</li>
 * <li>CTRL+DRAG - drag only on Y-Axis</li>
 * </ul>
 *
 * @author Dakror
 */
public class BezierEditor extends InputListener implements ApplicationListener {
	Stage stage;
	SpriteBatch fontBatch;
	Skin skin;
	AssetManager assets;
	BitmapFont font;
	
	final float SIZE = 200f;
	final float X = (380 - SIZE) / 2;
	
	final Vector2[] startPos = { new Vector2(X + SIZE, X + SIZE), new Vector2(X + SIZE, X), new Vector2(X, X + SIZE), new Vector2(X, X) };
	final Vector2 tmpV = new Vector2();
	final Vector2 tmpV2 = new Vector2();
	
	Actor selected;
	
	int SAMPLE_POINTS = 100;
	float SAMPLE_POINT_DISTANCE = 1f / SAMPLE_POINTS;
	
	int lastRad;
	int lastHeight;
	
	ImmediateModeRenderer20 renderer;
	Image[] knobs;
	Bezier<Vector2> bezier;
	Bezier<Vector2> bezierLogic;
	
	@Override
	public void create() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		stage = new Stage(new ScreenViewport());
		fontBatch = new SpriteBatch();
		
		font = new BitmapFont();
		assets = new AssetManager();
		assets.load("img/gui/knob.png", Texture.class);
		assets.finishLoading();
		
		skin = new Skin(Gdx.files.internal("skin/default/uiskin.json"));
		skin.add("knob", assets.get("img/gui/knob.png", Texture.class));
		
		knobs = new Image[4];
		renderer = new ImmediateModeRenderer20(false, false, 0);
		
		Vector2[] v = new Vector2[4];
		Vector2[] w = new Vector2[4];
		
		for (int i = 0; i < knobs.length; i++) {
			knobs[i] = new Image(skin.getDrawable("knob"));
			knobs[i].setPosition(startPos[i].x, startPos[i].y);
			stage.addActor(knobs[i]);
			v[i] = new Vector2(startPos[i].x, startPos[i].y);
			w[i] = new Vector2((startPos[i].x - X) / SIZE, (startPos[i].y - X) / SIZE);
		}
		
		bezier = new Bezier<Vector2>(v);
		bezierLogic = new Bezier<Vector2>(w);
		
		stage.addListener(this);
		stage.getCamera().rotate(Vector3.Z, -270);
		
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		for (int i = 0; i < knobs.length; i++) {
			bezier.points.get(i).set(knobs[i].getX() + 12, knobs[i].getY() + 12);
			bezierLogic.points.get(i).set((knobs[i].getX() - X) / SIZE, (knobs[i].getY() - X) / SIZE);
		}
		
		renderer.begin(stage.getBatch().getProjectionMatrix(), GL20.GL_LINE_STRIP);
		float val = 0f;
		while (val <= 1f) {
			renderer.color(0f, 0f, 0f, 1f);
			bezier.valueAt(tmpV, val);
			renderer.vertex(tmpV.x, tmpV.y, 0);
			val += SAMPLE_POINT_DISTANCE;
		}
		renderer.end();
		
		renderer.begin(stage.getBatch().getProjectionMatrix(), GL20.GL_LINE_STRIP);
		renderer.color(0f, 0f, 0f, 1f);
		renderer.vertex(bezier.points.get(0).x, bezier.points.get(0).y, 0);
		renderer.vertex(bezier.points.get(1).x, bezier.points.get(1).y, 0);
		renderer.end();
		renderer.begin(stage.getBatch().getProjectionMatrix(), GL20.GL_LINE_STRIP);
		renderer.color(0f, 0f, 0f, 1f);
		renderer.vertex(bezier.points.get(2).x, bezier.points.get(2).y, 0);
		renderer.vertex(bezier.points.get(3).x, bezier.points.get(3).y, 0);
		renderer.end();
		
		stage.act();
		stage.draw();
		
		fontBatch.begin();
		fontBatch.setColor(Color.WHITE);
		font.draw(fontBatch, s(), 0, 400);
		fontBatch.end();
	}
	
	public String s() {
		return String.format(Locale.ENGLISH, "%.1ff, %.1ff, %.1ff, %.1ff, %.1ff, %.1ff, %.1ff, %.1ff", //
				bezierLogic.points.get(0).x,//
				bezierLogic.points.get(0).y,//
				bezierLogic.points.get(1).x,//
				bezierLogic.points.get(1).y,//
				bezierLogic.points.get(2).x,//
				bezierLogic.points.get(2).y,//
				bezierLogic.points.get(3).x,//
				bezierLogic.points.get(3).y);
	}
	
	@Override
	public void resize(int width, int height) {}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}
	
	@Override
	public void dispose() {}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		Actor a = stage.hit(x, y, true);
		if (a != null) selected = a;
		return true;
	}
	
	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		if (selected == null) return;
		
		float x1 = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? selected.getX() : x - 12;
		float y1 = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? selected.getY() : y - 12;
		
		selected.setPosition(x1, y1);
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		selected = null;
	}
	
	@Override
	public boolean keyUp(InputEvent event, int keycode) {
		if (keycode == Keys.X) {
			for (int i = 0; i < knobs.length; i++) {
				knobs[i].setPosition(startPos[i].x, startPos[i].y);
			}
		} else if (keycode == Keys.C) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s()), null);
		} else if (keycode == Keys.V) {
			try {
				String s = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				String[] p = s.split(", ");
				if (p.length != 8) return true;
				for (int i = 0, j = 0; i < knobs.length; i++) {
					knobs[i].setX(Float.parseFloat(p[j++]) * SIZE + X);
					knobs[i].setY(Float.parseFloat(p[j++]) * SIZE + X);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (keycode == Keys.B || keycode == Keys.N) {
			try {
				if (keycode == Keys.B || lastRad == 0) {
					lastRad = Integer.parseInt(JOptionPane.showInputDialog("Maximum Radius (int)"));
					lastHeight = Integer.parseInt(JOptionPane.showInputDialog("Height / Length (int)"));
				}
				
				String[] lines = new String[lastHeight];
				int highest = 0;
				for (int i = 0; i < lastHeight; i++) {
					bezierLogic.valueAt(tmpV2, i / (float) lastHeight);
					int y = (int) Math.floor(tmpV2.y * lastRad);
					if (y > highest) highest = y;
					lines[i] = "";
					for (int j = 0; j < y; j++)
						lines[i] += "=";
				}
				
				for (String line : lines) {
					for (int i = 0; i < Math.round(highest - line.length() / 2f); i++)
						System.out.print(" ");
					System.out.print(line);
					System.out.println();
				}
			} catch (Exception e) {
				return true;
			}
		}
		return true;
	}
}
