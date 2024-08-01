package com.NEAT;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class NNNode
{
    int layer;
    public int id;
    HashMap connections = new HashMap<Integer, Float>(); // A list of connected nodes and their weights
    public float input;

    //Important point, NNNodes are not technically necessary, as connections store all necessary information
    //the inefficiency is probably minor though as there are no redundant connections and connections are 99% of the memory cost
    //Importantly though for k means clustering that means that only connections are necessary, where each ID can be a dimension
    public NNNode(int layer, int nodeID)
    {
        this.layer = layer;
        this.id = nodeID;
    }

    //Use sigmoid activation function
    float activation()
    {
        return (float) (1/( 1 + Math.pow(Math.E, (-1 * input))));
    }

    public void addConnection(int nodeID, float weight)
    {
        //System.out.println("Adding "+ id + " -> " + nodeID + " W=" + weight + " layer=" + layer);
        connections.put(nodeID, weight);
    }

    public void addRandomConnection(ArrayList<NNNode> nextLayer)
    {
        if(nextLayer.size() == connections.size())
            return;

        //Naive approach is to pick a random entry from the next layer, but have to make sure the connection doesn't exist
        Random r = new Random();
        ArrayList<Integer> candidates = new ArrayList<Integer>();

        for (NNNode node : nextLayer)
        {
            if (!connections.keySet().contains(node.id))
            {
                candidates.add(node.id);
            }
        }

        int target = candidates.size() > 1 ? r.nextInt(candidates.size()-1) : 0;
        this.addConnection(candidates.get(target), initWeight());
    }

    public float initWeight()
    {
        Random r = new Random();
        return r.nextFloat();
    }

    public void mutate(Config config)
    {
        //Mutate existing connections based on configuration values
        Random r = new Random();
        for(Object o : connections.keySet())
        {
            if (r.nextFloat() < config.mutateRate)
            {
                float newWeight = (float)connections.get(o) + (r.nextFloat() * 2 * config.mutateAmount) - config.mutateAmount;

                if(r.nextFloat() < config.superMutateRate)
                {
                    newWeight = (float)connections.get(o) + (r.nextFloat() * 2 * config.superMutateAmount) - config.superMutateAmount;
                }
                connections.put(o, newWeight);
            }
        }
    }

    public HashMap<Integer, Float> cloneConnections()
    {
        HashMap clonedConnections = new HashMap<Integer, Float>();
        for(Object key : connections.keySet())
        {
            int cloneID = (int)key;
            float weight = (float)connections.get(key);
            clonedConnections.put(cloneID, weight);
        }
        return clonedConnections;
    }

    public String toString()
    {
        String output = "--------\nNode ID: " + this.id + "\nConnections:\n";
        for(Object otherID : connections.keySet())
        {
            output += id + " -> " + otherID + " W=" + connections.get(otherID) + "\n";
        }
        return output;
    }
}
