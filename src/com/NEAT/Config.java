package com.NEAT;

//Should probably be a list of different enum types, maybe instead of classes it's just enums
//Because each method may only need a fraction of the config, so that would be a way of decoupling
public class Config
{
    //Mutation options
    float mutateRate = 1.0F;
    float mutateAmount = 0.2F; //Weight will be mutated by at most +- this number
    float nodeAddRate = 0.5F;
    float nodeDeleteRate = 0.1F;
    float connectionAddRate = 0.1F;
    float connectionDeleteRate = 0.08F;

}
