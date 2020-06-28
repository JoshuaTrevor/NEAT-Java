package com.NEAT.Workers;

import com.NEAT.Config;
import com.NEAT.EvolutionController;

public class WorkerMonitor extends Thread
{
    final EvolutionController controller;
    boolean exit;
    public boolean finished = false;
    public WorkerMonitor(EvolutionController controller)
    {
        super();
        this.controller = controller;
    }

    //If every worker is waiting, then the step is complete
    public void checkWorkers()
    {
        finished = workersFinished();
        if (finished && controller.waiting)
        {
            synchronized (controller)
            {
                //System.out.println("Finish condition detected");
                controller.notify();
            }
        }
    }

    public boolean workersFinished()
    {
        if (controller.waiting && controller.pruner.waiting)
        {
            for (SpeciesEvaluator s : controller.evaluators)
            {
                if (!s.waiting)
                {
                    return false;
                }
            }
            return true;
        }
        return false;
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
        } catch (InterruptedException e) {
            controller.debug("Monitor wait interrupted.");;
            exit = true;
        }
    }
}
