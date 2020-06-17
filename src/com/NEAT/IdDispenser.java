package com.NEAT;

public class IdDispenser
{
    private static long lastID = 0;
    public static synchronized long getID()
    {
        return lastID += 1;
    }
}
