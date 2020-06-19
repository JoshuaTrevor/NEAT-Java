package com.NEAT;

import java.util.Scanner;

public class Main {

    //where I'm up to:
    // Created and initialised a feed forward NN with random weights when given layer dimensions.

    // Need to add:
    // - Ability to mutate new nodes
    // - General breeding/evolution mechanics (fitness function, genus and species separation, breeding top 20%, clever mutation)
    // - Concurrency in training (and maybe in initialisation)
    // - Memory efficiency (Currently implementation will run out of ram with large NN architecture)
    // - Interface to allow adaptively displaying the best of each generation (Create API with getCurrentBest method)

    public static void main(String[] args)
    {
        ExampleImplementation e = new ExampleImplementation();
        e.evolve();
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
