package com.Examples.Snake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel
{
    int width, height;
    int rows, cols;
    int rowHeight;
    int colWidth;

    ArrayList<Coords> snake = new ArrayList<>();
    Coords food = new Coords(5, 0);
    //Need to separate game logic from other logic later, so essentially everything in this class should be manually updated by the SNAKE class
    //Including snake arraylist, etc.
    //That way this graphical stuff doesn't need to be calculated for training purposes.

    public Grid(int w, int h, int r, int c) {
        setSize(width = w, height = h);
        rows = r;
        cols = c;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i;
        colourSquares(g);
        g.setColor(Color.BLACK);
        width = getSize().width;
        height = getSize().height;
        rowHeight = height / (rows);
        colWidth = width / (cols);

        for (i = 0; i < rows; i++)
            g.drawLine(0, i * rowHeight, width, i * rowHeight);


        for (i = 0; i < cols; i++)
            g.drawLine(i * colWidth, 0, i * colWidth, height);

    }

    public void colourSquares(Graphics g)
    {
        g.setColor(Color.GREEN);
        g.fillRect(food.x * colWidth, food.y * rowHeight, colWidth, rowHeight);

        for(Coords c : snake)
        {
            g.setColor(Color.RED);
            g.fillRect(c.x * colWidth, c.y * rowHeight, colWidth, rowHeight);
        }
    }
}
