package com.NEAT;

public class ExampleImplementation implements NeatTrainer
{
    //Trivial example to test functionality. By training with this fitness function fitness should quickly converge to the number of outputs.
    @Override
    public float evaluateSpecies(FFNeuralNetwork nn)
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
