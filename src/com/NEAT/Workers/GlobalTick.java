package com.NEAT.Workers;


import com.NEAT.EvolutionController;

public class GlobalTick implements Runnable
{
    EvolutionController controller;
    public GlobalTick(EvolutionController controller)
    {
        this.controller = controller;
    }

    @Override
    public void run()
    {
            //System.out.println("tick");
            controller.workerMonitor.checkWorkers();
    }
}
