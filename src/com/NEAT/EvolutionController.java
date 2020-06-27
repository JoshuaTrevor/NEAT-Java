package com.NEAT;

//This class will basically have methods which are called by the users program and will use the methods defined in NeatTrainer
// It should aim to set up concurrency and stuff, and deal with storing and saving different Neural networks
// Basically, every other class should only deal with 1 NN at most, this is the only class that deals with many NN.


import com.NEAT.Breeding.Breeder;
import com.NEAT.Breeding.Mutator;
import com.NEAT.Workers.GlobalTick;
import com.NEAT.Workers.Pruner;
import com.NEAT.Workers.SpeciesEvaluator;
import com.NEAT.Workers.WorkerMonitor;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class EvolutionController
{
    //todo - return the highest fitness NN of the most recent generation
    public FFNeuralNetwork getRecentBest()
    {
        return null;
    }
    public ConcurrentLinkedQueue<Species> unevaluatedSpecies = new ConcurrentLinkedQueue<>();
    public ConcurrentSkipListSet<Species> evaluatedSpecies = new ConcurrentSkipListSet<>();
    public Object evaluationFinishedObject = new Object();
    public boolean waiting = false;
    protected final boolean debug = false;

    public final Pruner pruner;
    public final SpeciesEvaluator[] evaluators;
    public final WorkerMonitor workerMonitor;
    public final GlobalTick ticker;
    Config config;
    public EvolutionController(Config config, NeatTrainer implementation)
    {
        this.config = config;
        SpeciesEvaluator[] tempEvaluators = new SpeciesEvaluator[config.workers];
        for(int i = 0; i < config.workers; i++)
        {
            tempEvaluators[i] = (new SpeciesEvaluator(implementation, this));
        }
        evaluators = tempEvaluators.clone();
        pruner = new Pruner(this, config);
        workerMonitor = new WorkerMonitor(this);
        ticker = new GlobalTick(this);
    }

    public void train()
    {
        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.start();
        pruner.start();
        ticker.start();
        initPopulation(config);


        waiting = true;
        notifyEvaluators();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!workerMonitor.finished)
            await(); //FIX! Only await if the condition is not ALREADY finished! Have a flag in monitor to show if everything was waiting last query
        System.out.println("Passed the await");
        //Now that the population has been initialised and evaluated, create a new population
        //This population should be evaluated and created simultaneously
        final Mutator mutator = new Mutator();

        for(int i = 0; i < 50; i++)
        {
            System.out.println("Best fitness: " + evaluatedSpecies.last().fitness);
            evolve(mutator);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            notifyEvaluators();
            waiting = true;
            if (!workerMonitor.finished)
                await(); //FIX! Only await if the condition is not ALREADY finished! Have a flag in monitor to show if everything was waiting last query
        }

        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.interrupt();
        pruner.interrupt();
        ticker.interrupt();

//        for(Species s : evaluatedSpecies)
//            System.out.println(s.fitness);
        System.out.println("Best fitness: " + evaluatedSpecies.last().fitness);
        System.out.println("Evaluated Species: " + evaluatedSpecies.size());
        System.out.println("leftovers: " + unevaluatedSpecies.size());
    }

    //Mutate species, this may need to be delegated to worker threads depending on how intensive it is.
    public void evolve(Mutator mutator)
    {
        Breeder b = new Breeder(this, mutator, config);
        b.reproduce(evaluatedSpecies);
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
            notifyEvaluators();
        }
        //System.out.println(unevaluatedSpecies.remove().brain.toString());;
        System.out.println("Initial population created in " + (System.currentTimeMillis() - start)/1000F + " seconds");
    }

    public synchronized void notifyEvaluators()
    {
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

    public synchronized void await()
    {
        try {
            wait();
        } catch (InterruptedException e) {
            debug("Main wait interrupted");
        }
        waiting = false;
    }

    public void debug(String str)
    {
        if(debug)
        {
            System.out.println("[DEBUG] " + str);
        }
    }

    public void exit()
    {
        //Try to save progress
        //Close all processor threads
        //Then exit program
    }
}
