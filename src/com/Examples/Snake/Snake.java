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
    int[] usedMoves = {0, 0, 0, 0};
    boolean dead;
    boolean shouldRender;
    int movesSinceApple = 0;
    int rows = 7;
    int cols = 7;

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
        int startX = 3;//r.nextInt(cols-1)+1;
        int startY = 3;//r.nextInt(rows-1)+1;

        //Create a second body part which is offset to teach the snake not to eat itself
        int offset = r.nextInt(4);
        Direction dir = Direction.values()[offset];
        Coords c = new Coords(startX, startY);
        moveSquare(c, dir);

        snake.add(c);
        snake.add(new Coords(startX, startY));


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
        usedMoves[moveToIndex(d)] = 1;
        movesSinceApple++;
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
            movesSinceApple = 0;
            setFoodLocation();
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

    public int moveToIndex(Direction d)
    {
        return switch (d) {
            case LEFT -> 0;
            case RIGHT -> 1;
            case UP -> 2;
            case DOWN -> 3;
        };
    }

    public float[] getState()
    {
        float[] state = new float[rows*cols];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                if(coordInsideSnake(new Coords(j, i)))
                {
                    state[i * cols + j] = -1;
                }
                else if(j == food.x && i == food.y)
                {
                    state[i * cols + j] = 2;
                }

                else if(j == snake.get(0).x && i == snake.get(0).y)
                {
                    state[i * cols + j] = 1;
                }
            }
        }
//        float[] state = new float[6];
//        state[0] = snake.get(0).x;
//        state[1] = snake.get(0).y;
//        state[2] = snake.get(1).x;
//        state[3] = snake.get(1).y;
//        state[4] = food.x;
//        state[5] = food.y;

        //Normalise values
        for(int i = 0; i < state.length; i++)
        {
            state[i] = state[i]/2F;
        }

        return state;
    }

    public int manhattanDistanceToApple()
    {
        int xDist = Math.abs(snake.get(0).x - food.x);
        int yDist = Math.abs(snake.get(0).y - food.y);
        return xDist + yDist;
    }

    public void reset()
    {
        snake.clear();
        movesSinceApple = 0;
        Random r = new Random();
        int startX = 3;//r.nextInt(cols-1)+1;
        int startY = 3;//r.nextInt(rows-1)+1;
        this.dead=false;

        //Create a second body part which is offset to teach the snake not to eat itself
        int offset = r.nextInt(4);
        Direction dir = Direction.values()[offset];
        Coords c = new Coords(startX, startY);
        moveSquare(c, dir);

        snake.add(c);
        snake.add(new Coords(startX, startY));

        setFoodLocation();
        pushGraphicsChanges();
    }
}
