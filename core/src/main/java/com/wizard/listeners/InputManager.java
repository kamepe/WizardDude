package com.wizard.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputManager implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            System.out.println("Jump!");
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            System.out.println("Fall!");
        }
        return true;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }
    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return false;
    }
}
