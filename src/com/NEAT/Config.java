package com.NEAT;

public class Config
{
    public int checkpointFreq = 5;
    public int[] initialDimensions = new int[] {100, 200, 25};
    public int populationSize = 2500;
    public int workers = 14;
    public float preservedSpeciesRate = 0.08F;
    public final int preservedSpeciesLimit = Math.round(preservedSpeciesRate*populationSize);

    //Mutation options
    public float mutateRate = 0.25F;
    public float mutateAmount = 0.2F; //Weight will be mutated by at most +- this number
    public float superMutateRate = 0.01F;
    public float superMutateAmount = 0.8F;

    public float nodeAddRate = 0.5F;
    public float nodeDeleteRate = 0.1F;
    public float connectionAddRate = 1.0F;
    public float connectionDeleteRate = 0.08F;

}
