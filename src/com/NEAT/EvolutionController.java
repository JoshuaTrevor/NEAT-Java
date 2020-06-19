package com.NEAT;

//This class will basically have methods which are called by the users program and will use the methods defined in NeatTrainer
// It should aim to set up concurrency and stuff, and deal with storing and saving different Neural networks
// Basically, every other class should only deal with 1 NN at most, this is the only class that deals with many NN.


import com.NEAT.Workers.Pruner;
import com.NEAT.Workers.SpeciesEvaluator;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class EvolutionController
{
    //todo - return the highest fitness NN of the most recent generation
    public FFNeuralNetwork getRecentBest()
    {
        return null;
    }
    public Queue<Species> unevaluatedSpecies = new LinkedList<>();
    public ConcurrentSkipListSet<Species> evaluatedSpecies = new ConcurrentSkipListSet<>();
    public void train(Config config, NeatTrainer implementation)
    {
        //Create the worker threads

        //Create evaluators which compute the fitness of species from the queue
        ArrayList<SpeciesEvaluator> evaluators = new ArrayList<>();
        for(int i = 0; i < config.workers; i++)
        {
            evaluators.add(new SpeciesEvaluator(implementation, this));
        }

        //Create a "pruner" who removes undesirable species
        Pruner pruner = new Pruner(this, config);


        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.start();
        pruner.start();

        initPopulation(config);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.interrupt();
        pruner.interrupt();

        for(Species s : evaluatedSpecies)
            System.out.println(s.fitness);
        System.out.println(evaluatedSpecies.size());
    }

    public void initPopulation(Config config)
    {
        //Keeping the full population ever is super memory inefficient, so only keep the top X results relative to both population size and absolute
        System.out.println("Creating initial population");
        long start = System.currentTimeMillis();
        //Create species
        for(int i = 0; i < config.populationSize; i++)
        {
            FFNeuralNetwork nn = new FFNeuralNetwork(FFNeuralNetwork.ConnectionStrategy.INDIRECTLY_CONNECTED, config);
            Species s = new Species(nn);
            unevaluatedSpecies.add(s);
        }
        //System.out.println(unevaluatedSpecies.remove().brain.toString());;
        System.out.println("Initial population created in " + (System.currentTimeMillis() - start)/1000F + " seconds");
    }

    public void exit()
    {
        //Try to save progress
        //Then exit
    }
}
