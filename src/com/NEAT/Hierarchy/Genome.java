package com.NEAT.Hierarchy;

import com.NEAT.IdDispenser;

import java.util.ArrayList;

public class Genome
{
    ArrayList<Species> species = new ArrayList();
    long id = IdDispenser.getID();
    //I want to do this different from other neat applications if possible
    //Rather than having genomes being defined only by a certain genetic distance, I want the partitions to be defined dynamically
    //But it's also important to have a minimum genetic distance to avoid unnecessarily preserving bad genes.
    //This avoids situations where every species is its own genus, but also allows multiple genuses with smaller variation

    //Right now this class is really just a list of species and maybe an average fitness
    //possible approach to make it worthwhile:
    // Compute average/max fitness, use this to score the genome
    // As well as keeping top species, keep a few extra results from the top genome
    // Might not be worth it idk.
}
