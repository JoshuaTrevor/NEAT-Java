package com.NEAT;

import java.util.*;

public class FFNeuralNetwork
{
    //should have a list of layers
    //Each layer is a list of node objects
    public ArrayList<NNNode>[] layers;
    public enum ConnectionStrategy {FULLY_CONNECTED, INDIRECTLY_CONNECTED, MANUAL}
    public Config config; //Todo - Try make this non global to avoid storing so many copies, may break mutator and some others
    public FFNeuralNetwork(ConnectionStrategy strategy, Config config)
    {
        this.config = config;
        layers = new ArrayList[config.initialDimensions.length];

        int layerID = 0;
        for (int layerSize : config.initialDimensions)
        {
            layers[layerID] = new ArrayList<>();
            for(int i = 0; i < layerSize; i++)
            {
                layers[layerID].add(new NNNode(layerID, i));
            }
            layerID++;
        }
        initConnections(strategy, 1F);
    }

    // Default to 70%
    public void initConnections(ConnectionStrategy strategy)
    {
        initConnections(strategy, 0.7F);
    }

    public void initConnections(ConnectionStrategy strategy, float par1)
    {
        //Estimate connections based layer size, connectivity and strategy (whether to include last layer or not)
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
    //This initialising doesn't change the size of any of the layers, but later that might mess up ors omething idk

    //Connects given layer to successive layer
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
        int hiddenNodes = 0;
        for(int i = 1; i < layers.length-1; i++)
        {
            hiddenNodes += layers[i].size();
        }
        double bound = (1 / Math.sqrt(layers[0].size() + hiddenNodes));
        int multi = r.nextFloat() < 0.5 ? -1 : 1;
        return (float)(r.nextFloat() * bound * multi);
    }

    // Generate the output of the NN
    public float[] feed(float[] inputs)
    {
        if (inputs.length != layers[0].size())
        {
            System.out.println(inputs.length + "!= " + layers[0].size());
            System.out.println("The given inputs do not match the size of the input layer!");
            return null;
            //This should probably be replaced with "throw MyCustomException" and some kind of better error handling
            //Maybe surround the uses of user implementations (from the NeatTrainer interface) in try catch
        }

        for(int i = 0; i < layers[0].size(); i++)
        {
            layers[0].get(i).input = inputs[i];
        }

        float[] output = new float[layers[layers.length-1].size()];
        //Propagate activation energy forward
        for (int i = 0; i < layers.length-1; i++)
        {
            for (NNNode n1 : layers[i])
            {
                float activation = n1.activation();
                for (Object o : n1.connections.keySet())
                {
                    int n2 = (int) o;
                    int index = -1;
                    //TODO: Make this a binary search for efficiency later!
                    for(int j = 0; j < layers[i+1].size(); j++)
                    {
                        //efficientify later
                        if(layers[i+1].get(j).id == n2)
                            index = j;
                    }

                    layers[i+1].get(index).input += activation * (float)n1.connections.get(n2);
                }
            }
        }

        //This part is inefficient but not significant
        //Save the output layer
        Object[] outputObjects = layers[layers.length-1].toArray();
        for (int i = 0; i < outputObjects.length; i++)
        {
            //System.out.println("checked output: " + ((NNNode)outputObjects[i]).activation());
            output[i] = ((NNNode)outputObjects[i]).activation();
        }

        //Reset the incoming energy to each node (start at one because don't need to reset input layer)
        for (int i = 1; i < layers.length; i++)
        {
            for (NNNode n : layers[i])
            {
                n.input = 0;
            }
        }
        return output;
    }

    //Todo - make this work with added nodes, currently assumes the same nodes, then just adds corresp connections
    public FFNeuralNetwork copy()
    {
        FFNeuralNetwork copy = new FFNeuralNetwork(ConnectionStrategy.MANUAL, this.config);
        for(int i = 0; i < this.layers.length-1; i++)
        {
            for(int j = 0; j < this.layers[i].size(); j++)
            {
                NNNode n = copy.layers[i].get(j);
                n.connections = this.layers[i].get(j).cloneConnections();
            }
        }

        return copy;
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder("-------------------------------\n");
        for (int i = 0; i < layers.length; i++)
        {
            for (int j = 0; j < layers[i].size(); j++)
            {
                output.append(layers[i].get(j).toString());
            }
            output.append("-----------------\n");
        }
        return output.toString();
    }

    public String saveToString()
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < layers.length; i++)
        {
            output.append("Layer: " + i + "\n");
            for (int j = 0; j < layers[i].size(); j++)
            {
                output.append("Node: " + j + "\n");
                for(Object o : layers[i].get(j).connections.keySet())
                {
                    int toNode = (int)o;
                    output.append("Connection to: " + toNode + " -- " +
                            layers[i].get(j).connections.get(toNode) + "\n");
                }
                output.append("Node END\n");
            }
            output.append("Layer END\n");
        }
        return output.toString();
    }

    public void loadFromString(String strInput)
    {
        String[] lines = strInput.split("\n");
        int targetLayer = 0;
        int targetNode = -1;
        for(int pointer = 0; pointer < lines.length; pointer++)
        {
            if(lines[pointer].startsWith("Layer END"))
            {
                targetLayer++;
            }

            else if(lines[pointer].startsWith("Node: "))
            {
                String lineNumberOnly = lines[pointer].replaceAll("\\D","");
                targetNode = Integer.parseInt(lineNumberOnly);
            }

            else if(lines[pointer].startsWith("Connection to:"))
            {
                int nodeID = Integer.parseInt(lines[pointer].split(": ")[1].split(" --")[0]);
                float connectionWeight = Float.parseFloat(lines[pointer].split(" -- ")[1]);
                this.layers[targetLayer].get(targetNode).connections.put(nodeID, connectionWeight);
            }
        }
    }
}
