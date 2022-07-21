package com.NEAT;

import com.Examples.Snake.Display;
import com.Examples.Snake.Grid;
import com.Examples.Snake.NeatImplementation;
import com.Examples.Snake.Snake;
import com.NEAT.Breeding.Mutator;

public class Main {
    public static void main(String[] args)
    {
        //Snake snake = new Snake(true);

        NeatImplementation n = new NeatImplementation();
        n.evolve();

//        for(int i = 0; i < 18; i++) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            snake.move(Snake.Direction.RIGHT);
//        }



        //ExampleImplementation e = new ExampleImplementation();
        //e.evolve();
    }

    public static void testMutation()
    {
        int testParam = 3;//sc.nextInt();
        FFNeuralNetwork nn = new FFNeuralNetwork(FFNeuralNetwork.ConnectionStrategy.INDIRECTLY_CONNECTED, new Config());
        Mutator mutator = new Mutator();
        System.out.println(nn.toString());
        mutator.mutate(nn);
        System.out.println(nn.toString());
    }
}
