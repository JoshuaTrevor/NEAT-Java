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
        System.out.println("finished");
    }

    //Calculate the fitness of species from the queue of unevaluated species
    public void processQueue()
    {
        if(waiting) {
            await();
        }

        if(controller.unevaluatedSpecies.size() > 0)
        {
            try {
                Species spec = controller.unevaluatedSpecies.remove();
                if(spec == null)
                {
                    waiting = true;
                    return;
                }
                spec.fitness = implementation.evaluateSpecies(spec.brain);
                controller.evaluatedSpecies.add(spec);
                if(controller.pruner.waiting)
                {
                    synchronized(controller.pruner)
                    {
                        controller.pruner.notify();
                    }
                }

            } catch (NoSuchElementException e) {
                //System.out.println("failed to remove element");
                waiting = true;
                return;
            }
            return;
        }
        waiting = true;
    }

    public synchronized void await()
    {
        try {
            wait();
            //System.out.println("Evaluator - Permission received");
        } catch (InterruptedException e) {
            controller.debug("Evaluator wait interrupted.");;
            exit = true;
        }
        waiting = false;
    }
}
