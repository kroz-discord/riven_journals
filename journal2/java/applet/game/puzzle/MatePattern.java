// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SelectMate.java

package applet.game.puzzle;

import java.applet.AudioClip;

// Referenced classes of package applet.game.puzzle:
//            SelectMate

class MatePattern
{

    public MatePattern(int i, int j, int k, boolean flag, AudioClip audioclip)
    {
        parent1 = i;
        parent2 = j;
        child = k;
        living = flag;
        sound = audioclip;
    }

    int parent1;
    int parent2;
    int child;
    boolean living;
    AudioClip sound;
}
