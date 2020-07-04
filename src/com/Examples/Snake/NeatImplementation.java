package com.Examples.Snake;

import com.NEAT.Config;
import com.NEAT.EvolutionController;
import com.NEAT.FFNeuralNetwork;
import com.NEAT.NeatTrainer;

public class NeatImplementation implements NeatTrainer
{
    int thingsToDo = 10;
    float bestAvg = 0;
    @Override
    public float evaluateSpecies(FFNeuralNetwork nn)
    {
        float fitnessSum = 0;
        int fitnessCount = 0;
        for(int i = 0; i < thingsToDo; i++)
        {
            Snake snake = new Snake(false);
            //int startingManhattan = snake.manhattanDistanceToApple()+3;
            int moves = 0;
            while(!snake.dead)
            {
                float[] output = nn.feed(snake.getState());

                //Convert to direction
                float maxVal = -1000;
                int maxValIndex = -1;
                for(int j = 0; j < output.length; j++)
                {
                    if(output[j] > maxVal)
                    {
                        maxVal = output[j];
                        maxValIndex = j;
                    }
                }

                if(maxValIndex == -1) {
                    System.out.println("An error has occurred");
                    System.exit(0);
                }
                snake.move(Snake.Direction.values()[maxValIndex]);
                if(moves < 30)
                    moves++;
                else if (snake.movesSinceApple > 100)
                    snake.dead = true;
            }
            float moveBonus = Math.min(moves, 20);
            float usedMoveBonus = 0;
            for(int x =0; x <4 ; x++)
            {
                usedMoveBonus += snake.usedMoves[x];
            }
            usedMoveBonus = usedMoveBonus * 0.2F;
            fitnessSum += snake.applesEaten + moveBonus/20000F + (usedMoveBonus);
            fitnessCount++;
        }
        if(fitnessSum/fitnessCount > bestAvg)
        {
            bestAvg = fitnessSum/fitnessCount;
            if(bestAvg > 3)
            {
                thingsToDo = 6;
            }
        }

        //System.out.println("Apples eaten: " + snake.applesEaten);
        return fitnessSum / fitnessCount;
    }

    public void evolve()
    {
        Config config = new Config();
        config.initialDimensions = new int[] {49, 27, 15, 4};
        EvolutionController ec = new EvolutionController(config, this);
        EvolveThread evolver = new EvolveThread(ec);
        evolver.start();
        Snake snake = new Snake(true);
        while(true)
        {
            if(ec.currentGeneration < 1)
            {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            try
            {
                snake.reset();
                int moves = 0;
                while(!snake.dead) {
                    moves++;
                    if(snake.movesSinceApple > snake.rows*snake.cols+10)
                        snake.dead = true;
                    float[] output = ec.recentBest.brain.feed(snake.getState());

                    //Convert to direction
                    float maxVal = -1000;
                    int maxValIndex = -1;
                    for (int j = 0; j < output.length; j++) {
                        if (output[j] > maxVal) {
                            maxVal = output[j];
                            maxValIndex = j;
                        }
                    }

                    if (maxValIndex == -1) {
                        System.out.println("An error has occurred");
                        System.exit(0);
                    }
                    snake.move(Snake.Direction.values()[maxValIndex]);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }
}

class EvolveThread extends Thread
{
    EvolutionController controller;
    public EvolveThread(EvolutionController controller)
    {
        this.controller = controller;
    }

    public void evolve()
    {
        controller.train();
    }

    @Override
    public void run()
    {
        this.setPriority(8);
        evolve();
    }
}
