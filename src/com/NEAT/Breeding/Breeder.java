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

    //This will have different "phases", it may be possible to break these into different threads
    //Depending on how intensive this is, I might just leave breeding and mutation as single threaded
    //Otherwise I will have a selector who chooses pairs, then gives them to workers with instruction sets like breed + mutate these pairs etc..
    //maybe they take these instructions from a central queue?
    public void reproduce(ConcurrentSkipListSet<Species> evaluatedSpecies) //This should pass a reference coz mutability stuff
    {
        //-----------
        //Add check to see if the size of the set is what it should be relative to population size based on config
        //Don't necessarily need to handle, but should at least check I think to find concurrency issues later
        ArrayList<Species> parentSpecies = new ArrayList<>();
        //Remove and store all elements of the previous population
        while(evaluatedSpecies.size() > 0)
        {
            //High to low
            parentSpecies.add(evaluatedSpecies.pollLast());
        }
        //The above step should probably be completed before the evaluation starts, otherwise excessive additions may be recorded

        //Mutate top entries
        //TODO - Problem for next time
        //The number of species which have to be created is the population size
        //the size of each step should be determined based on the ratio between preserved species and population
        //Eg if preserved species is 100% of the population then some of the entries will be ignored
        //If the preserved species is very small, then the top entries will have to be mutated and bred in more combinations
        //This is decently complex so don't rush it, may need many helper methods

        //Go through species high to low
        //Currently defining genuses is quite complicated but I'll hopefully come back to it. K-means is promising


        //Temporary method to ensure minimal backwards evolution
        controller.unevaluatedSpecies.add(parentSpecies.get(0));
        controller.unevaluatedSpecies.add(parentSpecies.get(1));
        controller.unevaluatedSpecies.add(parentSpecies.get(2));
        controller.unevaluatedSpecies.add(parentSpecies.get(3));
        controller.unevaluatedSpecies.add(parentSpecies.get(4));
        controller.unevaluatedSpecies.add(parentSpecies.get(5));
        for(int i = 0; i < config.populationSize; i++)
        {
            //System.out.println("called");
            Random r = new Random();
            int val = r.nextInt(parentSpecies.size());
            Species copiedSelection = parentSpecies.get(val).clone();
            //System.out.println(copiedSelection.id);
            mutator.mutate(copiedSelection.brain);
            controller.unevaluatedSpecies.add(copiedSelection);
            if(i%10==0)
                controller.notifyEvaluators();
            if(config.preservedSpeciesRate < 0.1)
            {

                //Neat idea- instead of going down the list, assign species probabilities of breeding based on their fitness
                //Then roll these probabilities until we reach the required size.
                //For efficiency define each action a segment of the range 0-1 and then use nextFloat(), then perform action
                //If there is room, roll again to determine if multiple offspring, single mutation or single breeding occur.
            }
        }
    }

    //Set up random distribution
    //Pass this to the breedingaction decider later
    public void initDistribution()
    {
        HashMap<Integer, Integer> specWeightings = new HashMap<>();

    }

    //For offspring, randomly combine different connections/weight, different combo for each child, sometimes apply mutation as well?
    //This will probably become more complex with adding nodes, make sure added connections have a higher chance of being transferred
    //So basically separate into three stages: Add nodes from parents, add connections from parents, add weights from parents (2/3 linked heavily obvs)
    //Then at the end mutate/don't mutate
    public void breedPair(Species s1, Species s2, int offspringCount)
    {
        for(int i = 0; i < offspringCount; i++)
        {
            FFNeuralNetwork nn = new FFNeuralNetwork(FFNeuralNetwork.ConnectionStrategy.MANUAL, config);
            combineNetwork(s1, s2);
            Species offspring = new Species(nn);
        }
    }

    private void combineNetwork(Species s1, Species s2)
    {
        //The nodes and layers are already defined based on the config file, so here we just add the connections

    }
}
