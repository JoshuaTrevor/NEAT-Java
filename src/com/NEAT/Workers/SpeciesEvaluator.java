package com.NEAT.Workers;

import com.NEAT.EvolutionController;
import com.NEAT.Species;
import com.NEAT.NeatTrainer;

import java.util.NoSuchElementException;

public class SpeciesEvaluator extends Thread
{
    NeatTrainer implementation;
    EvolutionController controller;
    public SpeciesEvaluator(NeatTrainer implementation, EvolutionController controller)
    {
        super();
        this.implementation = implementation;
        this.controller = controller;
    }

    public void run()
    {
        processQueue();
    }

    //Calculate the fitness of species from the queue of unevaluated species
    public void processQueue()
    {
        //Maybe make this check a flag in the contrller instead of requiring manual interrupt
        while(!Thread.currentThread().isInterrupted())
        {
            if(controller.unevaluatedSpecies.size() > 0)
            {
                try {
                    Species spec = controller.unevaluatedSpecies.remove();
                    if(spec == null)
                        continue;
                    spec.fitness = implementation.getFitness(spec.brain);
                    controller.evaluatedSpecies.add(spec);
                } catch (NoSuchElementException e) {
                    ;
                }
            }
        }
    }
}
