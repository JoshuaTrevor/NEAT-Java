package com.Examples.Snake;

import com.NEAT.Config;
import com.NEAT.EvolutionController;
import com.NEAT.FFNeuralNetwork;
import com.NEAT.NeatTrainer;

import java.util.ArrayList;
import java.util.Stack;

// This class is where the Snake implementation is 'plugged in' to the evolutionary training library
public class NeatImplementation implements NeatTrainer
{
    int thingsToDo = 5;
    @Override
    public float evaluateSpecies(FFNeuralNetwork nn)
    {
        float fitnessSum = 0;
        int fitnessCount = 0;
        float min = 9999999F;
        for(int i = 0; i < thingsToDo; i++)
        {
            Snake snake = new Snake(false);
            int moves = 0;
            int productiveMoves = 0;
            int destructiveMoves = 0;
            while(!snake.dead)
            {
                float[] output = nn.feed(snake.getStateDistances());

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
                int prevManhattan = snake.manhattanDistanceToApple();
                snake.move(Snake.Direction.values()[maxValIndex]);
                if(snake.manhattanDistanceToApple() > prevManhattan)
                    destructiveMoves++;
                else
                    productiveMoves++;
                if(moves < 20)
                    moves++;
                else if (snake.movesSinceApple > snake.rows * snake.cols * Math.min(0.4F + snake.applesEaten * 0.1F, 1F))
                    snake.dead = true;
            }
            float moveBonus = 100-(Math.min(moves, 10) * 10);
            float usedMoveBonus = 0;
            for(int x =0; x <4 ; x++)
            {
                usedMoveBonus += snake.usedMoves[x];
            }
            usedMoveBonus = usedMoveBonus * 0.2F;
            float collisionThing = 0;//snake.collision ? -100F : 0F;
            float suicidePenalty = moves < 5 ? -100F : 0;
            float tempFit = suicidePenalty + collisionThing +
                    (snake.applesEaten*250) + (productiveMoves) - (destructiveMoves*1.3F);
            fitnessSum += tempFit;
            fitnessCount++;
            if(tempFit < min)
                min = tempFit;
        }

        return (fitnessSum / fitnessCount);// + min;
    }

    public void evolve()
    {
        Config config = new Config();
        config.initialDimensions = new int[] {10, 30, 10, 4};
        EvolutionController ec = new EvolutionController(config, this);
        EvolveThread evolver = new EvolveThread(ec);
        evolver.start();

        long startTime = System.currentTimeMillis();
        //Visual stuff
        Snake snake = new Snake(true);
        int bestScore = 0;
        ArrayList<Integer> scores = new ArrayList<>();
        while(true)
        {
            if(ec.recentBest == null && ec.currentGeneration < 1)
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
                    if(snake.movesSinceApple > snake.rows * snake.cols * Math.min(0.4F + snake.applesEaten * 0.1F, 1F))
                        snake.dead = true;
                    float[] output = ec.recentBest.brain.feed(snake.getStateDistances());

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
                        Thread.sleep(500/(snake.cols*5));
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                if(snake.applesEaten > bestScore)
                    bestScore = snake.applesEaten;
                if(scores.size() >= 100)
                    scores.remove(scores.get(0));
                scores.add(snake.applesEaten);
                int sum = 0;
                for(int score : scores)
                    sum+=score;
                float mean = sum/(scores.size()*1.0F);
                snake.d.setTitle("Generation: " + ec.currentGeneration + ", Best displayed score: " + bestScore + ", 100 attempt average: " + (mean));

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
        controller.train(true);
    }

    @Override
    public void run()
    {
        this.setPriority(8);
        evolve();
    }
}
