package com.Examples.Snake;

import javax.swing.*;

public class Display extends JFrame
{
    public Display(Grid g)
    {
        add(g);
        this.setSize(398, 399);
    }
}
