package com.NEAT;

import java.util.ArrayList;
import java.util.Random;

public class Mutator
{
    public void mutate(FFNeuralNetwork originalNN)
    {
        FFNeuralNetwork nn = originalNN;
        Random r = new Random();
        //Don't loop through output layer when adding forwards connections so its -1
        //Also don't loop through input layer when adding nodes
        for (int i = 0; i < originalNN.layers.length-1; i++)
        {
            for(NNNode n : nn.layers[i])
            {
                n.mutate(nn.config);

                if(r.nextFloat() < nn.config.connectionAddRate)
                {
                    n.addRandomConnection(nn.layers[i+1]);
                }
            }

            // If working with a hidden layer consider adding a node
            if(i > 0 && r.nextFloat() < nn.config.nodeAddRate)
            {
                //Create a new node, this adds a lot of complexity and room for failure so I will leave this until later.
//                NNNode newNode = createNewNode(nn.layers[i-1], nn.layers[i], i);
//                originalNN.layers[i].add(newNode);
            }


        }
    }

    //Needs some work before integration
    public NNNode createNewNode(ArrayList<NNNode> prevLayer, ArrayList<NNNode> layer, int layerID)
    {
        int uniqueID = layer.get(layer.size()-1).id + 1;
        NNNode n = new NNNode(layerID, uniqueID);
        //Now add some random connections to this node and see if good stuff happens

        return n;
    }

}
