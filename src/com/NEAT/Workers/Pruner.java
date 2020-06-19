package com.NEAT.Workers;

import com.NEAT.Config;
import com.NEAT.EvolutionController;

public class Pruner extends Thread
{
    EvolutionController controller;
    Config config;
    public Pruner(EvolutionController controller, Config config)
    {
        super();
        this.controller = controller;
        this.config = config;
    }

    public void prune()
    {
        while(!Thread.currentThread().isInterrupted())
        {
            if(controller.evaluatedSpecies.size() > config.storedSpeciesLimit)
            {
                //If the population is too large, reduce the least fit species
                //This should later be adjust to take a more sophisticated measure and remove things which don't foster diversity
                // Eg it might be better to kill off some very similar high fitness species in order to preserve a very different upper-middle fitness species
                controller.evaluatedSpecies.remove(controller.evaluatedSpecies.first());
            }
        }
    }

    public void run()
    {
        prune();
    }
}
