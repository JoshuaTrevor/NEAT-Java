package com.NEAT;

import com.Examples.Snake.Display;
import com.Examples.Snake.Grid;
import com.Examples.Snake.NeatImplementation;
import com.Examples.Snake.Snake;
import com.NEAT.Breeding.Mutator;

public class Main {
    public static void main(String[] args)
    {
        //Snake snake = new Snake(true);
        NeatImplementation n = new NeatImplementation();
        n.evolve();
    }
}
