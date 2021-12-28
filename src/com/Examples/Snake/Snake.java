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
    boolean collision = false;
    boolean shouldRender;
    int movesSinceApple = 0;
    int rows = 10;
    int cols = 10;
    private float bufferedStates[][] = new float[2][13];
    float[] prevState = new float[11];
    boolean first = true;
    boolean missedBetterMove;
    boolean won = false;
    boolean debugState = false;

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
        int startX = cols/2;//r.nextInt(cols-1)+1;
        int startY = rows/2;//r.nextInt(rows-1)+1;

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

        if(shouldDie(snake.get(0))) {
            this.collision = true;
            this.dead = true;

            //Check if there was a non-killing move
            for(Direction dir : Direction.values())
            {
                if(d != dir)
                {
                    Coords starting = oldCoords.copy();
                    moveSquare(starting, dir);
                    if(!shouldDie(starting))
                    {
                        this.missedBetterMove = true;
                    }
                }
            }
        }

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
            if(!(this.applesEaten >= this.rows * this.cols - 2))
                setFoodLocation();
            else
            {
                this.won = true;
                this.dead = true;
            }
        }

        if(shouldRender)
            pushGraphicsChanges();

        if(debugState)
            getStateDistances();
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

    public boolean shouldDie(Coords head)
    {
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

    public Direction getClockwiseDir(Direction d)
    {
        return switch (d) {
            case LEFT -> Direction.UP;
            case RIGHT -> Direction.DOWN;
            case UP -> Direction.RIGHT;
            case DOWN -> Direction.LEFT;
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
                    //Body
                    state[i * cols + j] = -1;
                }

                else if(j == food.x && i == food.y)
                {
                    //Food (used to be 2)
                    state[i * cols + j] = 1.5F;
                }

                else if(j == snake.get(0).x && i == snake.get(0).y)
                {
                    //Head (used to be 1)
                    state[i * cols + j] = 0.1F;
                }
            }
        }

        //Normalise values
        for(int i = 0; i < state.length; i++)
        {
            state[i] = state[i]/2F;
        }

        return state;
    }

    public float[] getStateDistances()
    {
        float[] output = new float[10];
        float fxdir = this.snake.get(0).x < 0 ? -1F : 1F;
        float fydir = this.snake.get(0).y < 0 ? -1F : 1F;
        output[0] = fxdir * Math.min(Math.abs(this.snake.get(0).x - this.food.x), 8)/8F;
        output[1] = fydir * Math.min(Math.abs(this.snake.get(0).y - this.food.y), 8)/8F;

        int outputIndex = 2;
        //Cardinal
        for(Direction d : Direction.values())
        {
            Coords c = this.snake.get(0).copy();
            moveSquare(c, d);
            int dist = 0;
            //if(head.x > cols-1 || head.x < 0 || head.y > rows-1 || head.y < 0)
            while(c.x < cols && c.x >= 0 && c.y < rows && c.y >= 0 && !coordInsideSnake(c))
            {
                moveSquare(c, d);
                dist++;
            }
            output[outputIndex] = dist;
            outputIndex++;
        }

        //Diagonals
        //For each direction, rotate 45 degrees clockwise
        for(Direction d : Direction.values())
        {
            Coords c = this.snake.get(0).copy();
            int dist = 0;
            moveSquare(c, d);
            moveSquare(c, getClockwiseDir(d));

            while(c.x < cols && c.x >= 0 && c.y < rows && c.y >= 0 && !coordInsideSnake(c))
            {
                moveSquare(c, d);
                moveSquare(c, getClockwiseDir(d));
                dist++;
            }
            output[outputIndex] = dist;
            outputIndex++;
        }

        //Normalise values
        for(int i = 2; i < output.length; i++)
        {
            float ceil = 8;
            output[i] = Math.min(ceil, output[i])/ceil;
        }

//        if(!first && movesSinceApple % 10 == 0)
//        {
//            bufferedStates[0] = prevState;
//            bufferedStates[1] = output;
//        }

        if(first)
            first = false;

        //prevState = output;

        if(!debugState)
            return output;
        //Print output
        System.out.println("--------------");
        int n = 0;
        for(float f : output)
        {
            n++;
            System.out.println(n + ": " + f);
        }
        return output;
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
        applesEaten = 0;
        Random r = new Random();
        int startX = cols/2;//r.nextInt(cols-1)+1;
        int startY = rows/2;//r.nextInt(rows-1)+1;
        this.dead=false;
        this.hasEaten = false;
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
