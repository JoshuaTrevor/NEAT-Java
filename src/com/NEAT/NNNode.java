package com.NEAT;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class NNNode
{
    int layer; // The layer this node is in
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
    //There should be a max value implemented that's related to the sigmoid function
    //Maybe the sigmoid function should use it for upper/lower bound? Just to make sure it doesn't get ludicrous with incrementing
    float activation()
    {
        //System.out.println("input: " + input);
        //System.out.println("output " + (float) (1/( 1 + Math.pow(Math.E, (-1 * input)))));
        return (float) (1/( 1 + Math.pow(Math.E, (-1 * input))));
    }

    public void addConnection(int nodeID, float weight)
    {
        //System.out.println("Adding "+ id + " -> " + nodeID + " W=" + weight + " layer=" + layer);

        connections.put(nodeID, weight);
        //System.out.println(toString());
    }

    public void addRandomConnection(ArrayList<NNNode> nextLayer)
    {
        //there are no vacant connections to form
        //This should avoid any null pointers
        if(nextLayer.size() == connections.size())
            return;

        //Naive approach is to pick a random entry from the next layer, but have to make sure the connection doesn't exist
        Random r = new Random();
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        //System.out.println("Connections size: " + connections.size());

        for (NNNode node : nextLayer)
        {
            if (!connections.keySet().contains(node.id))
            {
                candidates.add(node.id);
            }
        }
        //System.out.println("Candidates size: " + candidates.size());

        int target = candidates.size() > 1 ? r.nextInt(candidates.size()-1) : 0;
        this.addConnection(candidates.get(target), initWeight());
    }

    //This function is shared between this class and the NN class, should abstract out later and make more complex
    public float initWeight()
    {
        Random r = new Random();
        return r.nextFloat();
    }

    //Todo implement actual config
    public void mutate(Config config)
    {
        //Mutate each existing connection
        Random r = new Random();
        for(Object o : connections.keySet())
        {
            if (r.nextFloat() < config.mutateRate)
            {
                //System.out.println("old: " + connections.get(o));
                float newWeight = (float)connections.get(o) + (r.nextFloat() * 2 * config.mutateAmount) - config.mutateAmount;
                //System.out.println("new: " + newWeight);
                connections.put(o, newWeight);
            }
//            System.out.println("CHANGED NODE IS: ");
//            System.out.println(this.toString());
        }


        //-------
        // Maybe handle this in outer class when I have access to whole NN to use next layer?
        //---------
        //Have some chance of adding a new connection, to do this first compare length of connections to size of next layer
        //Iterate over each connection and roll a random which is compared to config mutate rate, replace rate etc.
        //handle delete case in mutator class, don't worrya bout it here.
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
