package com.NEAT;

//This class will basically have methods which are called by the users program and will use the methods defined in NeatTrainer
// It should aim to set up concurrency and stuff, and deal with storing and saving different Neural networks
// Basically, every other class should only deal with 1 NN at most, this is the only class that deals with many NN.


import com.NEAT.Breeding.Breeder;
import com.NEAT.Breeding.Mutator;
import com.NEAT.Workers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.*;

public class EvolutionController
{
    public Species recentBest;
    public int currentGeneration = 0;
    public ConcurrentLinkedQueue<Species> unevaluatedSpecies = new ConcurrentLinkedQueue<>();
    public ConcurrentSkipListSet<Species> evaluatedSpecies = new ConcurrentSkipListSet<>();
    public boolean waiting = false;
    final boolean debug = true;
    public int trialCount = 30;

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

    public void train(boolean loadPrevious)
    {
        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.start();
        pruner.start();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(ticker, 0, 1, TimeUnit.SECONDS);
        initPopulation(config, loadPrevious);

        long startTime = System.currentTimeMillis();
        waiting = true;
        notifyEvaluators();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!workerMonitor.finished)
            await();

        final Mutator mutator = new Mutator();
        
        for(int i = 0; i < 1000000; i++)
        {

            currentGeneration = i + 1;
            System.out.println("(Gen " + currentGeneration + ")" +
                    "fitness: " + Math.round(mean(evaluatedSpecies.toArray())) +" Best fitness: " +
                    Math.round(evaluatedSpecies.last().fitness) + " (Kept species: " + evaluatedSpecies.size() + ")" +
                    " Time taken: " + (System.currentTimeMillis() - startTime)/1000 + "s");
            recentBest = evaluatedSpecies.last().clone();
            if(currentGeneration % config.checkpointFreq == 0)
                saveRecent();
            evolve(mutator);
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            notifyEvaluators();
            waiting = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startTime = System.currentTimeMillis();
            if (!workerMonitor.finished)
                await();
        }

        for(SpeciesEvaluator evaluator : evaluators)
            evaluator.interrupt();
        pruner.interrupt();


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

    public void initPopulation(Config config, boolean loadPrevious)
    {
        //Keeping the full population ever is super memory inefficient, so only keep the top X results relative to both population size and absolute
        System.out.println("Creating initial population");

        if(loadPrevious)
        {
            FFNeuralNetwork nn = new FFNeuralNetwork(FFNeuralNetwork.ConnectionStrategy.MANUAL, config);
            try {
                String content = Files.readString(Paths.get("BestBrain"), StandardCharsets.US_ASCII);
                nn.loadFromString(content);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < config.populationSize; i++)
            {

                Species s = new Species(nn.copy());
                unevaluatedSpecies.add(s);
                notifyEvaluators();
                this.recentBest = s;
            }
            System.out.println("Loaded previous model");
            return;
        }

        long start = System.currentTimeMillis();
        //Create species
        for(int i = 0; i < config.populationSize; i++)
        {
            if(i%100==0)
                System.out.println(i);
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

    public float mean(Object[] list) {
        float sum = 0;
        for (int i = 0; i < list.length; i++) {
            sum += ((Species)list[i]).fitness;
        }
        return sum / list.length;
    }

    public void saveRecent()
    {
        String brainStr = recentBest.brain.saveToString();
        try {
            PrintWriter out = new PrintWriter("BestBrain");
            for(String line : brainStr.split("\n"))
                out.println(line);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Brain file not found");
        }
        System.out.println("Saved!");

    }

    public void exit()
    {
        //Try to save progress
        //Close all processor threads
        //Then exit program
    }
}
