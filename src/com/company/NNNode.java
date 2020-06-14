package com.company;


import java.util.HashMap;

public class NNNode
{
    int layer; // The layer this node is in
    HashMap connections = new HashMap<Integer, Float>(); // A list of connected nodes and their weights
    public float input;

    public NNNode(int layer, int nodeID)
    {
        this.layer = layer;
    }

    boolean activated()
    {
        return input > 1;
    }

    public void addConnection(int nodeID, float weight)
    {
        connections.put(nodeID, weight);
    }
}
