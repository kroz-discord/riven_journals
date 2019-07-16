// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Grid.java

import java.applet.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Grid extends Applet
    implements Runnable
{

    public void init()
    {
        appletSize = size();
    }

    public void start()
    {
        if(me == null)
        {
            me = new Thread(this, "Grid");
            me.start();
        }
        if(stumbled_upon_solution)
        {
            animator = new Thread(this, "animator");
            animator.start();
            return;
        } else
        {
            return;
        }
    }

    public void stop()
    {
        if(me != null)
        {
            try
            {
                me.stop();
            }
            catch(IllegalThreadStateException _ex) { }
            me = null;
        }
        if(animator != null)
        {
            try
            {
                animator.stop();
            }
            catch(IllegalThreadStateException _ex) { }
            animator = null;
            stumbled_upon_solution = false;
        }
    }

    public void dbg(String s)
    {
        if(debug)
            System.err.println("Grid: " + s);
    }

    public void run()
    {
        if(stumbled_upon_solution)
            animate();
        if(!loaded)
        {
            repaint();
            loadMedia();
        }
        if(!done)
            drawSquare(gc);
    }

    public void loadMedia()
    {
        if(canvas == null)
        {
            canvas = createImage(appletSize.width, appletSize.height);
            gc = canvas.getGraphics();
        }
        if(watch == null)
        {
            Image image;
            for(image = loadImage(getURL("images/watch.gif")); !statusImage(image, 300L););
            watch = createImageElements(image, 10);
        }
        if(background == null)
        {
            for(background = loadImage(getURL("images/back.gif")); !statusImage(background, 200L); nextWatch());
            gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
        }
        on = loadImage(getURL("images/on.gif"));
        for(; !statusImage(background, 200L); nextWatch());
        tween = loadImage(getURL("images/tween.gif"));
        for(; !statusImage(background, 200L); nextWatch());
        if(finalAnim == null)
        {
            finalAnim = new Image[4];
            for(int i = 0; i < 4; i++)
                for(finalAnim[i] = loadImage(getURL("images/final" + (i + 1) + ".jpg")); !statusImage(finalAnim[i], 200L); nextWatch());

        }
        loaded = true;
        buttonClick = getAudioClip(getURL("sounds/grid.wav"));
        finishClip = getAudioClip(getURL("sounds/grid_done.wav"));
    }

    public boolean handleEvent(Event event)
    {
        if(event.x > 9 && event.y > 9 && event.id == 501)
        {
            ex = (event.x - 10) / 20;
            why = (event.y - 10) / 20;
            if(done)
            {
                getAppletContext().showDocument(getURL("grid_cookie.cgi"));
                return true;
            }
            if(loaded && !stumbled_upon_solution)
            {
                buttonClick.play();
                if(gridStatus[why][ex] == 0)
                    gridStatus[why][ex] = 1;
                else
                    gridStatus[why][ex] = 0;
                drawSquare(gc);
            }
        }
        return false;
    }

    private void drawSquare(Graphics g)
    {
        int i = 0;
        int j = 0;
        g.drawImage(background, 0, 0, this);
        for(int k = 0; k < wd; k++)
        {
            for(int l = 0; l < hi; l++)
                if(gridStatus[l][k] == 1)
                {
                    g.drawImage(on, k * 20 + 10, l * 20 + 10, this);
                    if(gridSolution[l][k] == 1)
                        j++;
                    else
                        i++;
                }

        }

        repaint();
        if(j == 23 && i == 0)
        {
            stumbled_upon_solution = true;
            animator = new Thread(this, "animator");
            animator.start();
        }
    }

    private void animate()
    {
        if(!done)
        {
            gc.drawImage(finalAnim[0], 0, 0, this);
            repaint();
            try
            {
                Thread.sleep(300L);
            }
            catch(InterruptedException _ex) { }
            finishClip.play();
            try
            {
                Thread.sleep(50L);
            }
            catch(InterruptedException _ex) { }
            for(int i = 0; i < 4; i++)
            {
                gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
                gc.drawImage(finalAnim[i], 0, 0, this);
                repaint();
                try
                {
                    Thread.sleep(230L);
                }
                catch(InterruptedException _ex) { }
            }

        }
        done = true;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        g.drawImage(canvas, 0, 0, this);
    }

    private URL getURL(String s)
    {
        String s1 = new String(s);
        if(!s1.startsWith("http://"))
        {
            String s2 = getDocumentBase().toString();
            if(!s2.endsWith("/"))
                s2 = s2.substring(0, s2.lastIndexOf('/') + 1);
            s1 = s2 + s1;
        }
        URL url = null;
        try
        {
            url = new URL(s1);
        }
        catch(MalformedURLException _ex) { }
        return url;
    }

    private Image loadImage(URL url)
    {
        dbg("loading... " + url);
        if(mt == null)
            mt = new MediaTracker(this);
        Image image = getImage(url);
        int i = image.hashCode();
        mt.addImage(image, i);
        mt.statusID(i, true);
        return image;
    }

    private boolean statusImage(Image image, long l)
    {
        boolean flag = false;
        try
        {
            flag = mt.waitForID(image.hashCode(), l);
        }
        catch(InterruptedException _ex) { }
        return flag;
    }

    private void nextWatch()
    {
        gc.drawImage(watch[watchFrame++ % watch.length], appletSize.width / 2 - 14, appletSize.height / 2 - 16, this);
        repaint();
    }

    private Image[] createImageElements(Image image, int i)
    {
        int j = image.getHeight(this);
        int k = image.getWidth(this);
        int l = k / i;
        Image aimage[] = new Image[i];
        dbg("created " + i + " elements");
        for(int i1 = 0; i1 < i; i1++)
        {
            aimage[i1] = createImage(l, j);
            Graphics g = aimage[i1].getGraphics();
            g.drawImage(image, i1 * -l, 0, this);
            g.dispose();
        }

        return aimage;
    }

    public Grid()
    {
        debug = true;
        loaded = false;
        stumbled_upon_solution = false;
        animation_shown = false;
        done = false;
        solved = false;
        ready = false;
        wd = 8;
        hi = 6;
    }

    boolean debug;
    boolean loaded;
    boolean stumbled_upon_solution;
    boolean animation_shown;
    boolean done;
    boolean solved;
    boolean ready;
    Dimension appletSize;
    Thread me;
    Thread animator;
    MediaTracker mt;
    Graphics gc;
    Image canvas;
    Image background;
    Image on;
    Image tween;
    Image watch[];
    Image finalAnim[];
    AudioClip buttonClick;
    AudioClip finishClip;
    Rectangle buttonRect;
    int ex;
    int why;
    int wd;
    int hi;
    int buttonState;
    int buttonClicked;
    int watchFrame;
    private static final int gridSolution[][] = {
        {
            0, 0, 0, 1, 0, 0, 1, 0
        }, {
            1, 0, 0, 1, 0, 1, 0, 0
        }, {
            0, 0, 0, 1, 1, 0, 1, 1
        }, {
            1, 1, 1, 0, 1, 0, 0, 1
        }, {
            1, 1, 1, 0, 0, 0, 1, 1
        }, {
            0, 0, 1, 1, 1, 0, 1, 0
        }
    };
    private static final int gridStatus[][] = {
        {
            0, 0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0, 0, 0, 0, 0, 0, 0
        }, {
            0, 0, 0, 0, 0, 0, 0, 0
        }
    };
    private static final int startX = 10;
    private static final int startY = 10;

}
