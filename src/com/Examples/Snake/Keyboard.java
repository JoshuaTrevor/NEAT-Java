package com.Examples.Snake;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {
    Snake snake;
    public Keyboard(Snake snake)
    {
        super();
        this.snake = snake;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch(keyCode)
        {
            case KeyEvent.VK_LEFT -> snake.move(Snake.Direction.LEFT);
            case KeyEvent.VK_UP -> snake.move(Snake.Direction.UP);
            case KeyEvent.VK_RIGHT -> snake.move(Snake.Direction.RIGHT);
            case KeyEvent.VK_DOWN -> snake.move(Snake.Direction.DOWN);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }
}
