// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Dot3DApp.java

import java.applet.Applet;
import java.awt.*;
import java.io.PrintStream;

public class Dot3DApp extends Applet
    implements Runnable
{

    public void init()
    {
        dbg("\n\n\ninitializing w h");
        w = size().width;
        h = size().height;
        tk = Toolkit.getDefaultToolkit();
    }

    public void run()
    {
        Thread thread = Thread.currentThread();
        thread.setPriority(1);
        String s = thread.getName();
        dbg(s + " is running");
        if(thread == UpdateThread)
            update_run();
        if(sintab == null)
            sintab = new float[256];
        initSintab();
        offscrImage1 = createImage(w, h);
        offscrGC1 = offscrImage1.getGraphics();
        offscrGC1.setColor(Color.gray);
        offscrGC1.fillRect(0, 0, w, h);
        theImage = offscrImage1;
        offscrImage2 = createImage(w, h);
        offscrGC2 = offscrImage2.getGraphics();
        offscrGC2.setColor(Color.gray);
        offscrGC2.fillRect(0, 0, w, h);
        dotGridInit();
        dbg(DotGrid.toString());
        randSphereInit();
        dbg(RandSphere.toString());
        do
        {
            Thread.yield();
            waitConsumned();
            Graphics g;
            if((trans & 1) == 0)
                g = offscrGC1;
            else
                g = offscrGC2;
            g.setColor(Color.black);
            g.fillRect(0, 0, w, h);
            double d = trans;
            d = (float)(Math.sin(d * 0.017453292500000002D) * 2D + 6.5999999999999996D);
            double d1 = (double)trans * 0.90000000000000002D;
            d1 = (float)(Math.sin(d1 * 0.017453292500000002D) * 360D);
            double d2 = (double)trans * 0.80000000000000004D;
            d2 = (float)(Math.sin(d2 * 0.017453292500000002D) * 360D);
            RandSphere.mat.unit();
            RandSphere.mat.scale(d, d, d);
            RandSphere.mat.zrot(d1);
            RandSphere.mat.yrot(d2);
            RandSphere.mat.xrot(65D);
            RandSphere.mat.translate(0.0F, 0.0F, 400F);
            RandSphere.Transform();
            RandSphere.Draw(g);
            signalProduced();
        } while(true);
    }

    void dotGridInit()
    {
        DotGrid = new Dot3DModel();
        DotGrid.SetDevAttributes(w / 2, h / 2, 280F);
        DotGrid.InithtFd(8, 40F);
        DotGrid.mat.scale(1.0F);
        DotGrid.TransformStore();
    }

    void randSphereInit()
    {
        RandSphere = new Dot3DModel();
        RandSphere.SetDevAttributes(w / 2, h / 2, 160F);
        RandSphere.genRandomSphere(6, 100F, 0.4F);
    }

    void update_run()
    {
        Thread thread = Thread.currentThread();
        do
        {
            Thread.yield();
            waitProduced();
            if((frame & 1) == 0)
                theImage = offscrImage1;
            else
                theImage = offscrImage2;
            repaint();
            signalConsumned();
        } while(true);
    }

    private synchronized void signalProduced()
    {
        frames2update++;
        trans++;
        notifyAll();
    }

    private synchronized void signalConsumned()
    {
        frames2update--;
        frame++;
        notifyAll();
    }

    private synchronized void waitProduced()
    {
        while(frames2update == 0) 
            try
            {
                wait();
            }
            catch(Exception _ex) { }
    }

    private synchronized void waitConsumned()
    {
        while(frames2update > 1) 
            try
            {
                wait();
            }
            catch(Exception _ex) { }
    }

    private double rad(double d)
    {
        return d * 0.017453292500000002D;
    }

    public void start()
    {
        dbg("starting...");
        if(UpdateThread == null)
        {
            dbg("new UpdateThread");
            UpdateThread = new Thread(this, "UpdateThread");
            UpdateThread.start();
        }
        if(TransThread == null)
        {
            dbg("new TransThread");
            TransThread = new Thread(this, "TransThread");
            TransThread.start();
        }
    }

    public void stop()
    {
        System.out.println("stopping...");
        if(UpdateThread != null)
        {
            try
            {
                UpdateThread.stop();
            }
            catch(IllegalThreadStateException _ex) { }
            UpdateThread = null;
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(theImage == null)
        {
            g.fillRect(0, 0, w, h);
            return;
        } else
        {
            tk.sync();
            g.drawImage(theImage, 0, 0, this);
            return;
        }
    }

    public void dbg(String s)
    {
    }

    private void initSintab()
    {
        double d = 0.0D;
        for(int i = 0; i < 256; i++)
        {
            sintab[i] = (float)Math.sin(d);
            d += 0.017453292500000002D;
        }

    }

    public Dot3DApp()
    {
    }

    int w;
    int h;
    int frame;
    int trans;
    int frames2update;
    Image theImage;
    Image offscrImage1;
    Image offscrImage2;
    Graphics offscrGC1;
    Graphics offscrGC2;
    Thread UpdateThread;
    Thread TransThread;
    final boolean DEBUG = false;
    Dot3DModel DotGrid;
    Dot3DModel RandSphere;
    private float sintab[];
    Toolkit tk;
}
