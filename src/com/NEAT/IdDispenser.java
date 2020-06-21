package com.NEAT;

import java.util.Stack;

public class IdDispenser
{
    private static long lastNewID = 0;
    private static final Stack<Long> stack = new Stack<>();

    public static synchronized long getID()
    {
        if(stack.size() > 0)
            return stack.pop();
        return lastNewID += 1;
    }

    public static synchronized void recycleID(long id, int maxSize)
    {
        if(stack.size() < maxSize)
        {
            stack.push(id);
        }
    }
}
