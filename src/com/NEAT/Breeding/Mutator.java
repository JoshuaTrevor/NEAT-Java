package com.NEAT.Breeding;

import com.NEAT.FFNeuralNetwork;
import com.NEAT.NNNode;

import java.util.ArrayList;
import java.util.Random;

public class Mutator
{
    public void mutate(FFNeuralNetwork nn)
    {
        Random r = new Random();
        for (int i = 0; i < nn.layers.length-1; i++)
        {
            for(NNNode n : nn.layers[i])
            {
                n.mutate(nn.config);

                if(r.nextFloat() < nn.config.connectionAddRate)
                {
                    n.addRandomConnection(nn.layers[i+1]);
                }
            }
        }
    }

    public NNNode createNewNode(ArrayList<NNNode> prevLayer, ArrayList<NNNode> layer, int layerID)
    {
        int uniqueID = layer.get(layer.size()-1).id + 1;
        NNNode n = new NNNode(layerID, uniqueID);
        return n;
    }

}
