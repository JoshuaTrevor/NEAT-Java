package com.NEAT.Workers;

import com.NEAT.EvolutionController;
import com.NEAT.Species;
import com.NEAT.NeatTrainer;

import java.util.NoSuchElementException;

public class SpeciesEvaluator extends Thread
{
    NeatTrainer implementation;
    EvolutionController controller;
    public boolean waiting = false;
    boolean exit = false;
    boolean evaluationComplete = false;
    //Do not need to check if pruning is complete, when evaluation is complete we can just drop the last elements of the set at once.
    public SpeciesEvaluator(NeatTrainer implementation, EvolutionController controller)
    {
        super();
        this.implementation = implementation;
        this.controller = controller;
    }

    public void run()
    {
        while(!Thread.currentThread().isInterrupted() && !exit)
        {
            processQueue();
        }
    }

    //Calculate the fitness of species from the queue of unevaluated species
    public void processQueue()
    {
        //Maybe make this check a flag in the contrller instead of requiring manual interrupt
        if(waiting) {
            System.out.println("Evaluator waiting for permission to resume");
            await();
        }
        if(controller.unevaluatedSpecies.size() > 0)
        {
            try {
                Species spec = controller.unevaluatedSpecies.remove();
                if(spec == null)
                    return;
                spec.fitness = implementation.getFitness(spec.brain);
                controller.evaluatedSpecies.add(spec);
                if(controller.pruner.waiting)
                {
                    synchronized(controller.pruner)
                    {
                        controller.pruner.notify();
                    }
                }
            } catch (NoSuchElementException e) {
                return;
            }
            return;
        }
        waiting = true;
    }

    public synchronized void await()
    {
        try {
            synchronized(controller.workerMonitor)
            {
                controller.workerMonitor.notify();
            }
            wait();
            System.out.println("Permission received");
        } catch (InterruptedException e) {
            System.out.println("Evaluator wait interrupted.");;
            exit = true;
        }
        waiting = false;
    }
}
