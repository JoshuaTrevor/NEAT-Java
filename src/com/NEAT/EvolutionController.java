package com.NEAT;

//This class will basically have methods which are called by the users program and will use the methods defined in NeatTrainer
// It should aim to set up concurrency and stuff, and deal with storing and saving different Neural networks
// Basically, every other class should only deal with 1 NN at most, this is the only class that deals with many NN.


import com.NEAT.Hierarchy.Population;

public class EvolutionController
{
    //todo - return the highest fitness NN of the most recent generation
    public FFNeuralNetwork getRecentBest()
    {
        return null;
    }

    //While small improvements in efficiency in other classes are important, computing and storing results needs to be memory optimised
//very well to avoid ram blowouts like in neat-python
    public void evolveGeneration(Population p)
    {
        //So the memory optimisation issue is:
        // To choose which species to keep and breed, need to compare their fitnesses
        // That means keeping one neural network for each member of the population which is huge
        // To fix this we should proactively discard species as soon as it's possible to know they won't be selected for next generation

        //My general structure idea is to put everything into a queue and have it accessed by X worker threads
        // These worker threads will each select NN's 1 by 1, then evolve them and return the results to this class in another data struct
        // This data struct is the thing which will have to be pruned in real time by yet another thread
        // Use wait() when the dat struct is pruned effectively and use notify() from the worker threads once its size gets too alrge
        // Eg if size is > X, worker thread will check size when placing new entries and then notify pruning thread to do work
        //Use this stuff https://stackoverflow.com/questions/5999100/is-there-a-block-until-condition-becomes-true-function-in-java/26218153

    }

    public void exit()
    {
        //Try to save progress
        //Then exit
    }
}
