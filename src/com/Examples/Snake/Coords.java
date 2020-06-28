package com.Examples.Snake;

public class Coords
{
    int x;
    int y;
    public Coords(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Coords copy()
    {
        return new Coords(this.x, this.y);
    }
}