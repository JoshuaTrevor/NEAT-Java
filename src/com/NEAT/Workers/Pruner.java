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
            System.out.println("Pruner waiting for permission to resume");
            await();
        }
        if(controller.evaluatedSpecies.size() > config.storedSpeciesLimit)
        {
            //If the population is too large, reduce the least fit species
            //This should later be adjust to take a more sophisticated measure and remove things which don't foster diversity
            // Eg it might be better to kill off some very similar high fitness species in order to preserve a very different upper-middle fitness species
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
            synchronized(controller.workerMonitor)
            {
                controller.workerMonitor.notify();
            }
            wait();
            System.out.println("Permission received");
        } catch (InterruptedException e) {
            System.out.println("Pruner wait interrupted.");;
            exit = true;
        }
        waiting = false;
    }
}
