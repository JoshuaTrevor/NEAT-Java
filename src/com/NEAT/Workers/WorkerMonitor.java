package com.NEAT.Workers;

import com.NEAT.Config;
import com.NEAT.EvolutionController;

public class WorkerMonitor extends Thread
{
    final EvolutionController controller;
    boolean exit;
    public WorkerMonitor(EvolutionController controller)
    {
        super();
        this.controller = controller;
    }

    //If every worker is waiting, then the step is complete
    public void checkWorkers()
    {
        if (controller.waiting && controller.pruner.waiting)
        {
            for(SpeciesEvaluator s : controller.evaluators)
            {
                if(!s.waiting)
                {
                    return;
                }

            }
            synchronized (controller)
            {
                controller.notify();
            }
        }

        await();
    }

    public void run()
    {
        while(!Thread.currentThread().isInterrupted() && !exit)
        {
            checkWorkers();
        }
    }

    public synchronized void await()
    {
        try {
            wait();
            System.out.println("Permission received");
        } catch (InterruptedException e) {
            System.out.println("Monitor wait interrupted.");;
            exit = true;
        }
    }
}
