package com.NEAT.Hierarchy;

import com.NEAT.FFNeuralNetwork;
import com.NEAT.IdDispenser;

public class Species
{
    public long id; // Will probably need a concurrency safe ID dispenser class! Otherwise maybe just hash the brain somehow?
    // Investigate https://stackoverflow.com/questions/11597386/objects-hash-vs-objects-hashcode-clarification-needed
    //!!!!The default hashCode() of Object returns the memory address for the object.
    //So it should work for ID as long as a deep copy is used for the brain!
    public float fitness;
    public FFNeuralNetwork brain;

    public Species(FFNeuralNetwork brain, float fitness)
    {
        this.id = IdDispenser.getID();
    }
}
