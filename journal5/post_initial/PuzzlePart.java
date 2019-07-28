// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PuzzlePart.java

import java.awt.Image;
import java.awt.Point;

public class PuzzlePart
{

    public PuzzlePart()
    {
        this(true, null);
    }

    public PuzzlePart(Image image1)
    {
        this(true, image1);
    }

    public PuzzlePart(boolean flag, Image image1)
    {
        goodness = flag;
        lockedIn = false;
        if(image1 != null)
            image = image1;
    }

    public void setImage(Image image1)
    {
        image = image1;
    }

    public Image getImage()
    {
        return image;
    }

    public boolean isPuzzlePart()
    {
        return goodness;
    }

    public boolean partLockedIn()
    {
        return lockedIn;
    }

    public void lockInPart()
    {
        lockedIn = true;
    }

    public void unlockPart()
    {
        lockedIn = false;
    }

    public void setDestination(Point point)
    {
        destination = point;
    }

    public Point getDestination()
    {
        return destination;
    }

    public void setXLoc(int i)
    {
        x = i;
    }

    public int getXLoc()
    {
        return x;
    }

    public void setYLoc(int i)
    {
        y = i;
    }

    public int getYLoc()
    {
        return y;
    }

    public boolean isHot()
    {
        int i = Math.abs(x - destination.x);
        int j = Math.abs(y - destination.y);
        return i < 5 && j < 5;
    }

    protected int x;
    protected int y;
    protected boolean goodness;
    protected boolean lockedIn;
    protected Point destination;
    protected Image image;
}
