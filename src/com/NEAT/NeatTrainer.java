package com.NEAT;

public interface NeatTrainer
{
    float getFitness(FFNeuralNetwork nn);

    default Config getConfig()
    {
        return new Config();
    }
}
