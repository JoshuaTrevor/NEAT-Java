package com.NEAT.Workers;

import com.NEAT.Config;
import com.NEAT.EvolutionController;
import com.NEAT.IdDispenser;

public class Pruner extends Thread
{
    final EvolutionController controller;
    Config config;
    public boolean waiting = false;
    public boolean exit = false;
    public Pruner(EvolutionController controller, Config config)
    {
        super();
        this.controller = controller;
        this.config = config;
    }

    public void prune()
    {
        if(waiting) {
            await();
        }
        if(controller.evaluatedSpecies.size() > config.preservedSpeciesLimit)
        {
            //If the population is too large, prune the least fit species until it is not too large
            IdDispenser.recycleID(controller.evaluatedSpecies.first().id, config.populationSize);
            controller.evaluatedSpecies.pollFirst();
            return;
        }
        waiting = true;
    }

    public void run()
    {
        while(!Thread.currentThread().isInterrupted() && !exit)
        {
            prune();
        }
    }

    public synchronized void await()
    {
        try {
            wait();
        } catch (InterruptedException e) {
            controller.debug("Pruner wait interrupted.");;
            exit = true;
        }
        waiting = false;
    }
}
