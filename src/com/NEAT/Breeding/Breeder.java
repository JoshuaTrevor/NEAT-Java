package com.NEAT.Breeding;

import com.NEAT.Config;
import com.NEAT.EvolutionController;
import com.NEAT.FFNeuralNetwork;
import com.NEAT.Species;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

public class Breeder
{
    Mutator mutator;
    Config config;
    EvolutionController controller;
    public Breeder(EvolutionController controller, Mutator mutator, Config config)
    {
        this.controller = controller;
        this.mutator = mutator;
        this.config = config;
    }

    public void reproduce(ConcurrentSkipListSet<Species> evaluatedSpecies) //This should pass a reference coz mutability stuff
    {
        ArrayList<Species> parentSpecies = new ArrayList<>();
        //Remove and store all elements of the previous population
        while(evaluatedSpecies.size() > 0)
        {
            //High to low
            parentSpecies.add(evaluatedSpecies.pollLast());
        }

        //Temporary method to ensure minimal backwards evolution
        controller.unevaluatedSpecies.addAll(parentSpecies);
        for(int i = 0; i < config.populationSize; i++)
        {
            Random r = new Random();
            int val = r.nextInt(parentSpecies.size());
            Species copiedSelection = parentSpecies.get(val).clone();
            //System.out.println(copiedSelection.id);
            mutator.mutate(copiedSelection.brain);
            controller.unevaluatedSpecies.add(copiedSelection);
            if(i%10==0)
                controller.notifyEvaluators();
        }
    }

    public void initDistribution()
    {
        HashMap<Integer, Integer> specWeightings = new HashMap<>();

    }

    //For offspring, randomly combine different connections/weight, different combo for each child, sometimes apply mutation as well?
    public void breedPair(Species s1, Species s2, int offspringCount)
    {
        for(int i = 0; i < offspringCount; i++)
        {
            FFNeuralNetwork nn = new FFNeuralNetwork(FFNeuralNetwork.ConnectionStrategy.MANUAL, config);
            combineNetwork(s1, s2);
            Species offspring = new Species(nn);
        }
    }
}
