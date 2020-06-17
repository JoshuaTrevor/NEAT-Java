package com.NEAT;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class NNNode
{
    int layer; // The layer this node is in
    int id;
    HashMap connections = new HashMap<Integer, Float>(); // A list of connected nodes and their weights
    public float input;

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
        System.out.println("Connections size: " + connections.size());

        for (NNNode node : nextLayer)
        {
            if (!connections.keySet().contains(node.id))
            {
                candidates.add(node.id);
            }
        }
        System.out.println("Candidates size: " + candidates.size());

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