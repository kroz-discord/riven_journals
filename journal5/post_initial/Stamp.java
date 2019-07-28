// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Stamp.java

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Stamp extends Applet
    implements Runnable
{

    public void init()
    {
        appletSize = size();
        appletName = getParameter("name");
        appletNumb = appletName.substring(5);
    }

    public void start()
    {
        if(me == null)
        {
            me = new Thread(this, "stamp");
            me.start();
        }
        if(solved)
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
        }
    }

    public void run()
    {
        if(!loaded)
        {
            canvas = createImage(appletSize.width, appletSize.height);
            gc = canvas.getGraphics();
            if(animImages[2] == null)
            {
                for(animImages[2] = loadImage(getURL("images/puzzle" + appletNumb + "/p" + appletNumb + "-fliped3.jpg")); !statusImage(animImages[2], 200L); repaint())
                {
                    gc.setColor(Color.black);
                    gc.fillRect(0, 0, appletSize.width, appletSize.height);
                }

                gc.drawImage(animImages[2], 0, 0, appletSize.width, appletSize.height, this);
                repaint();
                for(int i = 0; i < 2; i++)
                    for(animImages[i] = loadImage(getURL("images/puzzle" + appletNumb + "/p" + appletNumb + "-fliped" + (i + 1) + ".jpg")); !statusImage(animImages[i], 200L); repaint())
                        gc.drawImage(animImages[2], 0, 0, appletSize.width, appletSize.height, this);


            }
            loaded = true;
        }
        if(solved)
            animate();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        g.drawImage(canvas, 0, 0, this);
    }

    public void fallBack(boolean flag)
    {
        first = flag;
        solved = true;
        dbg("\n    *** In stamp1 ***  Woo-hoo!!!\n");
        animator = new Thread(this, "animator");
        animator.start();
    }

    private synchronized void animate()
    {
        for(int i = 2; i >= 0; i--)
        {
            gc.drawImage(animImages[i], 0, 0, this);
            repaint();
            try
            {
                Thread.sleep(300L);
            }
            catch(InterruptedException _ex) { }
        }

        if(first)
        {
            if(appletNumb.equals("1"))
                getAppletContext().showDocument(getURL("set_state.cgi?paul23"), "hidden");
            if(appletNumb.equals("2"))
                getAppletContext().showDocument(getURL("set_state.cgi?peter27"), "hidden");
            if(appletNumb.equals("3"))
                getAppletContext().showDocument(getURL("set_state.cgi?timothy29"), "hidden");
            if(appletNumb.equals("4"))
                getAppletContext().showDocument(getURL("set_state.cgi?lance31"), "hidden");
        }
    }

    public String getAppletInfo()
    {
        return "Stamp (" + getParameter("name") + ") by tm";
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

    public Image loadImage(URL url)
    {
        dbg("loading... " + url + "\n");
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

    public void dbg(String s)
    {
        if(debug)
            System.err.print(s);
    }

    public Stamp()
    {
        solved = false;
        loaded = false;
        first = false;
        debug = true;
        animImages = new Image[3];
    }

    private boolean solved;
    private boolean loaded;
    private boolean first;
    private boolean debug;
    private Thread me;
    private Thread animator;
    private Dimension appletSize;
    private MediaTracker mt;
    private Graphics gc;
    private Image canvas;
    private Image animImages[];
    private String appletName;
    private String appletNumb;
}
