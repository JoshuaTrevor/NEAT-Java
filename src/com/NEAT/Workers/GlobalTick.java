package com.NEAT.Workers;


import com.NEAT.EvolutionController;

public class GlobalTick extends Thread
{
    EvolutionController controller;
    public GlobalTick(EvolutionController controller)
    {
        this.controller = controller;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            try
            {
                Thread.sleep(300);
                synchronized (controller.workerMonitor)
                {
                    controller.workerMonitor.checkWorkers();
                }
            }

            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
