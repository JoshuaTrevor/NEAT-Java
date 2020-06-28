package com.NEAT;

//Should probably be a list of different enum types, maybe instead of classes it's just enums
//Because each method may only need a fraction of the config, so that would be a way of decoupling
public class Config
{
    public int[] initialDimensions = new int[] {5, 200, 25};
    public int populationSize = 100000;
    public int workers = 4;
    public float preservedSpeciesRate = 0.05F;
    public final int preservedSpeciesLimit = Math.round(preservedSpeciesRate*populationSize);  // This should maybe be defined as a fraction of the population size

    //Mutation options
    public float mutateRate = 1.0F;
    public float mutateAmount = 0.2F; //Weight will be mutated by at most +- this number
    public float nodeAddRate = 0.5F;
    public float nodeDeleteRate = 0.1F;
    public float connectionAddRate = 0.1F;
    public float connectionDeleteRate = 0.08F;

}
