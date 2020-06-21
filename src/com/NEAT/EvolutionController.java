package com.NEAT;

//This class will basically have methods which are called by the users program and will use the methods defined in NeatTrainer
// It should aim to set up concurrency and stuff, and deal with storing and saving different Neural networks
// Basically, every other class should only deal with 1 NN at most, this is the only class that deals with many NN.


import com.NEAT.Workers.Pruner;
import com.NEAT.Workers.SpeciesEvaluator;
import com.NEAT.Workers.WorkerMonitor;

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
    public Object evaluationFinishedObject = new Object();
    public boolean waiting = false;

    public final Pruner pruner;
    public final SpeciesEvaluator[] evaluators;
    public final WorkerMonitor workerMonitor;
    Config config;
    public EvolutionController(Config config, NeatTrainer implementation)
    {
        this.config = config;
        SpeciesEvaluator[] tempEvaluators = new SpeciesEvaluator[config.workers];
        for(int i = 0; i < config.workers; i++)
        {
            tempEvaluators[i] = (new SpeciesEvaluator(implementation, this));
        }
        evaluators = tempEvaluators;
        pruner = new Pruner(this, config);
        workerMonitor = new WorkerMonitor(this);
    }

    public void train()
    {
        workerMonitor.start();
        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.start();
        pruner.start();


        //If this works way faster than worker threads might use more memory than I want.
        //Eventually should have a "wait" system where if unevaluatedSpecies backlog reaches a certain size it should pause.

        //Also another todo for tomorrow, have a "finished" flag from both this and the mutator,
        // if finishFlag AND queueSize == 0 then go to next step
        initPopulation(config);


        final Mutator mutator = new Mutator();

        //check if done then do mutate
        //mutator.mutate

        //Evaluators should be the ones which notify pruners, not other things.
        //I don't think anything in this class should regularly notify pruners
        for(int j = 0; j < evaluators.length; j++)
        {
            if(evaluators[j].waiting)
            {
                synchronized(evaluators[j])
                {
                    evaluators[j].notify();
                }
            }
        }

        waiting = true;
        await();

        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.interrupt();
        pruner.interrupt();
        workerMonitor.interrupt();

//        for(Species s : evaluatedSpecies)
//            System.out.println(s.fitness);
        System.out.println("Best fitness: " + evaluatedSpecies.last().fitness);
        System.out.println(evaluatedSpecies.size());
        System.out.println("leftovers: " + unevaluatedSpecies.size());
        //Still need to deal with leftovers
    }

    //Mutate species, this may need to be delegated to worker threads depending on how intensive it is.
    public void evolve(Mutator mutator)
    {
        try {
            evaluationFinishedObject.wait(); //NEED to deal with edge cases where evaluation of worker threads finishes before this wait is called so it never gets finished. Maybe handshake? So they call wait constantly until this class sends acknowledgement? I like that method.
            //NEED GOOD EVOLUTION HERE, for now just mutate

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

            for(int j = 0; j < evaluators.length; j++)
            {
                if(evaluators[j].waiting)
                {
                    synchronized(evaluators[j])
                    {
                        evaluators[j].notify();
                    }
                }
            }
        }
        //System.out.println(unevaluatedSpecies.remove().brain.toString());;
        System.out.println("Initial population created in " + (System.currentTimeMillis() - start)/1000F + " seconds");
    }

    public synchronized void await()
    {
        try {
            wait();
            System.out.println("Permission received");
        } catch (InterruptedException e) {
            System.out.println("Main wait interrupted.");
        }
        waiting = false;
    }

    public void exit()
    {
        //Try to save progress
        //Close all processor threads
        //Then exit program
    }
}
