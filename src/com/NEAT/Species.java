package com.NEAT;

public class Species implements Comparable
{
    public long id;
    public float fitness;
    public FFNeuralNetwork brain;

    public Species(FFNeuralNetwork brain)
    {
        this.brain = brain;
        this.id = IdDispenser.getID();
    }

    @Override
    public int compareTo(Object o) {
        Species s = (Species)o;
        int comparison = Float.compare(this.fitness, s.fitness);
        //Since we're adding to a set we still want to create a concrete sorting for equal fitnesses
        if(comparison == 0)
            return Long.compare(this.id, s.id);
        else
            return comparison;
    }

    @Override
    public boolean equals(Object o)
    {
        if (getClass() != o.getClass())
            return false;

        Species s = (Species)o;
        return this.id == s.id;
    }

    @Override
    public int hashCode() {
        return (int)this.id;
    }
}
