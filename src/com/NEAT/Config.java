package com.NEAT;

//Should probably be a list of different enum types, maybe instead of classes it's just enums
//Because each method may only need a fraction of the config, so that would be a way of decoupling
public class Config
{
    public int checkpointFreq = 10;
    public int[] initialDimensions = new int[] {100, 200, 25};
    public int populationSize = 5000;
    public int workers = 4;
    public float preservedSpeciesRate = 0.08F;
    public final int preservedSpeciesLimit = Math.round(preservedSpeciesRate*populationSize);

    //Mutation options
    public float mutateRate = 0.85F;
    public float mutateAmount = 0.15F; //Weight will be mutated by at most +- this number
    public float superMutateRate = 0.01F;
    public float superMutateAmount = 0.8F;

    public float nodeAddRate = 0.5F;
    public float nodeDeleteRate = 0.1F;
    public float connectionAddRate = 1.0F;
    public float connectionDeleteRate = 0.08F;

}
