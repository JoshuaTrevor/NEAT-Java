package com.company;

import java.util.Scanner;

public class Main {

    //where I'm up to:
    // Created and initialised a feed forward NN with random weights when given layer dimensions.

    // Need to add:
    // - Ability to mutate new nodes and connections
    // - General breeding/evolution mechanics (fitness function, genus and species separation, breeding top 20%, clever mutation)
    // - Concurrency in training (and maybe in initialisation)
    // - Memory efficiency (Currently implementation will run out of ram with large NN architecture)
    // - Interface to allow adaptively displaying the best of each generation (Create API with getCurrentBest method)

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        //System.out.println("Please enter test param");
        int testParam = 5;//sc.nextInt();
        FFNeuralNetwork nn = new FFNeuralNetwork(new int[] {testParam, testParam, testParam});
        //System.out.println(nn.toString());
    }
}
