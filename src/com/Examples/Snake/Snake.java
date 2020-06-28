package com.Examples.Snake;

import com.NEAT.Config;
import com.NEAT.EvolutionController;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Snake
{
    Grid grid;
    Display d;
    boolean hasEaten = false;
    boolean dead;
    boolean shouldRender;
    int rows = 10;
    int cols = 10;

    public int applesEaten = 0;

    private ArrayList<Coords> snake = new ArrayList<>();
    private Coords food = new Coords(0, 0);

    public enum Direction{
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Snake(boolean shouldRender)
    {
        this.shouldRender = shouldRender;
        //Init snake position
        Random r = new Random();
        int startX = r.nextInt(cols-2)+2;
        int startY = r.nextInt(rows-2)+2;

        //Create a second body part which is offset to teach the snake not to eat itself
        int offset = r.nextInt(4);
        Direction dir = Direction.values()[offset];
        Coords c = new Coords(startX, startY);
        moveSquare(c, dir);

        snake.add(new Coords(startX, startY));
        //snake.add(c); //For now single segment start

        setFoodLocation();
        if(shouldRender)
        {
            this.grid = new Grid(400, 400, rows, cols);
            this.d = new Display(this.grid);
            d.setVisible(true);
            pushGraphicsChanges();
            Keyboard keyboard = new Keyboard(this);
            d.addKeyListener(keyboard);
        }


    }

    public void pushGraphicsChanges()
    {
        grid.snake = this.snake;
        grid.food = this.food;
        grid.repaint();
    }

    public void move(Direction d)
    {
        //Move the head and save it's old position
        Coords oldCoords = snake.get(0).copy();
        Coords temp;
        moveSquare(snake.get(0), d);

        if(shouldDie())
            this.dead = true;

        //Move all other parts into the position that their "leading part" was in
        for(int i = 1; i < snake.size(); i++)
        {
            temp = snake.get(i).copy();
            snake.set(i, oldCoords.copy());
            oldCoords = temp.copy();
        }

        //Deal with apple eating
        if(hasEaten)
        {
            snake.add(oldCoords.copy());
            hasEaten = false;
        }
        if(snake.get(0).x == food.x && snake.get(0).y == food.y)
        {
            hasEaten = true;
            applesEaten++;

            //Temporary change to teach nn to go to first apple properly
            dead=true;
            //setFoodLocation();
        }

        if(shouldRender)
            pushGraphicsChanges();
    }

    public boolean coordInsideSnake(Coords query)
    {
        for(Coords part : snake)
        {
            if(part.x == query.x && part.y == query.y)
                return true;
        }
        return false;
    }

    //Currently implementation does not scale that well with longer games where the snake takes up most squares
    //Once longer games become more common an approach which checks which squares are free then selects one from that list is better.
    public void setFoodLocation()
    {
        Random r = new Random();
        food.x = r.nextInt(cols);
        food.y = r.nextInt(rows);

        if(coordInsideSnake(food))
            setFoodLocation();
    }

    public boolean shouldDie()
    {
        Coords head = snake.get(0);
        if(head.x > cols-1 || head.x < 0 || head.y > rows-1 || head.y < 0)
        {
            return true;
        }

        boolean first = true;
        for(Coords part : snake)
        {
            if(first)
            {
                first = false;
                continue;
            }

            if(part.x == head.x && part.y == head.y)
                return true;
        }
        return false;
    }

    public void moveSquare(Coords c, Direction d)
    {
        switch (d) {
            case LEFT -> c.x--;
            case RIGHT -> c.x++;
            case UP -> c.y--;
            case DOWN -> c.y++;
        }
    }

    public float[] getState()
    {
        float[] state = new float[4];
        state[0] = snake.get(0).x;
        state[1] = snake.get(0).y;
        state[2] = food.x;
        state[3] = food.y;

        return state;
    }

    public int manhattanDistanceToApple()
    {
        int xDist = Math.abs(snake.get(0).x - food.x);
        int yDist = Math.abs(snake.get(0).y - food.y);
        return xDist + yDist;
    }
}
