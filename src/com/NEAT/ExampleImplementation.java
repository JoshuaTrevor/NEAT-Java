package com.NEAT;

public class ExampleImplementation implements NeatTrainer
{

    //There should be a max value implemented that's related to the sigmoid function
    //Maybe the sigmoid function should use it for upper/lower bound? Just to make sure it doesn't get ludicrous with incrementing
    @Override
    public float getFitness(FFNeuralNetwork nn)
    {
        //This should just produce the sum of all outputs, therefore each value should be maxxed by training
        float sum = 0;
        float[] output = nn.feed(new float[] {2, 2, 2});
        for (float elem : output)
        {
            sum+=elem;
        }
        return sum;
    }

    public void evolve()
    {
        EvolutionController ec = new EvolutionController(new Config(), this);
        ec.train();
    }

    //Using default config setup
}
