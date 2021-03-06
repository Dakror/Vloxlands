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


package de.dakror.vloxlands.util.base;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

import de.dakror.vloxlands.layer.Layer;

/**
 * @author Dakror
 */
public abstract class GameBase extends ApplicationAdapter implements InputProcessor, GestureListener {
	public CopyOnWriteArrayList<Layer> layers = new CopyOnWriteArrayList<Layer>();
	private InputMultiplexer multiplexer = new InputMultiplexer();
	
	public void addLayer(Layer layer) {
		layer.show();
		getMultiplexer().addProcessor(0, layer.gestureDetector);
		getMultiplexer().addProcessor(0, layer);
		if (layer.getStage() != null) getMultiplexer().addProcessor(0, layer.getStage());
		layers.add(layer);
	}
	
	public void toggleLayer(Layer layer) {
		if (hasLayer(layer.getClass())) removeLayer(layer.getClass());
		else addLayer(layer);
	}
	
	public boolean removeLayer(Layer layer) {
		getMultiplexer().removeProcessor(layer);
		getMultiplexer().removeProcessor(layer.gestureDetector);
		if (layer.getStage() != null) getMultiplexer().removeProcessor(layer.getStage());
		layer.dispose();
		return layers.remove(layer);
	}
	
	public boolean removeLayer(Class<?> layerClass) {
		for (Layer layer : layers)
			if (layer.getClass().equals(layerClass)) removeLayer(layer);
		
		return false;
	}
	
	public boolean hasLayer(Class<?> layerClass) {
		for (Layer layer : layers)
			if (layer.getClass().equals(layerClass)) return true;
		
		return false;
	}
	
	public Layer getActiveLayer() {
		return layers.get(layers.size() - 1);
	}
	
	public void clearLayers() {
		for (Layer l : layers)
			removeLayer(l);
	}
	
	public void setLayer(Layer layer) {
		clearLayers();
		addLayer(layer);
	}
	
	public InputMultiplexer getMultiplexer() {
		return multiplexer;
	}
	
	@Override
	public void resize(int width, int height) {
		for (Layer l : layers)
			l.resize(width, height);
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}
	
	@Override
	public boolean longPress(float x, float y) {
		return false;
	}
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}
	
	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}
	
	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
}
