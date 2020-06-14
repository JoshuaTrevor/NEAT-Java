package com.company;

import java.util.*;

public class FFNeuralNetwork
{
    //should have a list of layers
    //Each layer is a list of node objects
    //List<List<NNNode>> layers;
    ArrayList<NNNode>[] layers;
    public enum ConnectionStrategy {DIRECTLY_CONNECTED, INDIRECTLY_CONNECTED}
    public FFNeuralNetwork(int[] layerSizes)
    {
        layers = new ArrayList[layerSizes.length];

        int layerID = 0;
        for (int layerSize : layerSizes)
        {
            layers[layerID] = new ArrayList<>();
            for(int i = 0; i < layerSize; i++)
            {
                layers[layerID].add(new NNNode(layerID, i));
            }
            layerID++;
        }
        addConnections(ConnectionStrategy.INDIRECTLY_CONNECTED, 0.8F);
    }

    public void addConnections(ConnectionStrategy strategy)
    {
        addConnections(strategy, 0F);
    }

    public void addConnections(ConnectionStrategy strategy, float par1)
    {
        for(int i = 0; i < layers.length; i++)
        {
            switch(strategy)
            {
                case INDIRECTLY_CONNECTED:
                    if(i < layers.length-1)
                        initConnectForward(i, par1);
                break;
            }
        }
    }

    //Should make concurrent at some point
    //Remember: if the size of one of the other layers changes I won't know concurrently
    //This initialising doens't fchange the size of any of the layers, but later that might mess up ors omething idk
    public void initConnectForward(int layerID, float connectivity)
    {
        //Using connectivity determine how many nodes to connect to
        int possibilities;
        try
        {
            possibilities = layers[layerID+1].size();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("You're trying to connect to a layer that does not exist!");
            return;
        }

        int necessaryConnections = Math.round((float)possibilities * connectivity);
        ArrayList<Integer> possibleNodeIndices = new ArrayList<Integer>();

        for(int i = 0; i < possibilities; i++)
        {
            possibleNodeIndices.add(i);
        }

        Random r = new Random();
        for(NNNode n : layers[layerID])
        {
            ArrayList<Integer> pni = (ArrayList) possibleNodeIndices.clone();
            int connectionsMade = 0;

            while(connectionsMade < necessaryConnections)
            {
                int chosenNode = r.nextInt(pni.size());

                n.addConnection(pni.get(chosenNode), initWeight());
                pni.remove(chosenNode);

                connectionsMade++;
            }
        }
    }

    public float initWeight()
    {
        Random r = new Random();
        return r.nextFloat();
    }



    public String toString()
    {
        String output = "-------------------------------\n";
        for (int i = 0; i < layers.length; i++)
        {
            for (int j = 0; j < layers[i].size(); j++)
            {
                output += layers[i].get(j) + "\n";
            }
            output += "-----------------\n";
        }
        return output;
    }
}
