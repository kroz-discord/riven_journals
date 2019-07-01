// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MinnieMap.java

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MinnieMap extends Applet
    implements Runnable
{

    public void init()
    {
        String s = getParameter("DEBUG");
        if(s != null && s.equalsIgnoreCase("true"))
            debug = true;
        topDir = getParameter("TOP_DIR");
        finalImagePrefix = getParameter("FINAL_IMG_PREFIX");
        finalImageSuffix = getParameter("FINAL_IMG_SUFFIX");
        finalImageCount = Integer.parseInt(getParameter("FINAL_IMG_COUNT"));
        finalImageX = Integer.parseInt(getParameter("FINAL_IMG_X"));
        finalImageY = Integer.parseInt(getParameter("FINAL_IMG_Y"));
        appletSize = size();
        initRegionGlobals();
    }

    public void start()
    {
        if(me == null)
        {
            me = new Thread(this, "MinnieMap");
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
        if(solved)
            animate();
        if(canvas == null)
        {
            canvas = createImage(appletSize.width, appletSize.height);
            gc = canvas.getGraphics();
        }
        if(backgroundColor == null)
        {
            loading = true;
            backgroundColor = getColorParam("BACKGROUND_COLOR");
            dbg("backColor = " + backgroundColor);
            foregroundColor = getColorParam("FOREGROUND_COLOR");
            dbg("foreColor = " + foregroundColor);
            repaint();
        }
        if(watch == null)
        {
            Image image;
            for(image = loadImage(getURL(topDir + getParameter("WATCH_IMG"))); !statusImage(image, 300L););
            watch = createImageElements(image, 10);
        }
        if(mapoff == null)
        {
            loadMedia();
            loading = false;
            loaded = true;
        }
        draw();
        requestFocus();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(loading || loaded)
        {
            g.drawImage(canvas, 0, 0, this);
            return;
        } else
        {
            g.setColor(getBackground());
            g.fillRect(0, 0, size().width, size().height);
            return;
        }
    }

    private void draw()
    {
        if(!solved)
        {
            gc.drawImage(mapoff, 0, 0, this);
            for(int i = 0; i < totalRegions; i++)
                if(regionStatus.get(i))
                {
                    Point point = regionToTopLeftPoint(i);
                    gc.drawImage(activeRegions[i], point.x, point.y, this);
                }

        }
        repaint();
    }

    protected void loadMedia()
    {
        dbg("loading map background");
        for(mapoff = loadImage(getURL(topDir + getParameter("MAPOFF_IMG"))); !statusImage(mapoff, 300L); nextWatch());
        getRegionGeometry(mapoff);
        dbg("loading activated map background");
        Image image;
        for(image = loadImage(getURL(topDir + getParameter("MAPON_IMG"))); !statusImage(image, 300L); nextWatch());
        activeRegions = createMapElements(image);
        image.flush();
        dbg("loading audio clips");
        defaultClickSnd = getAudioClip(getURL(topDir + getParameter("DEFCLICK_SND")));
        specialClickSnd = getAudioClip(getURL(topDir + getParameter("HINTCLICK_SND")));
        incorrectFinalSnd = getAudioClip(getURL(topDir + getParameter("FINAL_SND_WRONG")));
        correctFinalSnd = getAudioClip(getURL(topDir + getParameter("FINAL_SND_RIGHT")));
        nextWatch();
        dbg("loading region chord data");
        loadRegionData();
        dbg("loading final success animation");
        if(finalImages == null)
        {
            finalImages = new Image[finalImageCount];
            for(int i = 1; i <= finalImageCount; i++)
                finalImages[i - 1] = loadImage(getURL(topDir + finalImagePrefix + i + finalImageSuffix));

        }
    }

    private synchronized void animate()
    {
        if(!done)
        {
            draw();
            try
            {
                Thread.sleep(750L);
            }
            catch(InterruptedException _ex) { }
            correctFinalSnd.play();
            try
            {
                Thread.sleep(50L);
            }
            catch(InterruptedException _ex) { }
            for(int i = 0; i <= finalImageCount - 1; i++)
            {
                gc.drawImage(finalImages[i], finalImageX, finalImageY, this);
                repaint();
                try
                {
                    Thread.sleep(400L);
                }
                catch(InterruptedException _ex) { }
            }

        }
        done = true;
    }

    public boolean handleEvent(Event event)
    {
        byte byte0 = -1;
        if(!loaded || solved && !done)
            return true;
        if(event.id == 501)
        {
            if(done)
            {
                if(event.x >= finalImageX && event.x <= finalImageX + finalImages[finalImageCount - 1].getWidth(this) && event.y >= finalImageY && event.y <= finalImageY + finalImages[finalImageCount - 1].getHeight(this))
                    getAppletContext().showDocument(getURL(topDir + finalDestinationURL));
                return true;
            }
            int i = pointToRegion(event.x, event.y);
            if(i == submitRegion && clickCounter == solutionLength)
            {
                specialClickSnd.play();
                if(arrayCompare(Solution, clickSequence))
                {
                    solved = true;
                    animator = new Thread(this, "animator");
                    animator.start();
                } else
                {
                    incorrectFinalSnd.play();
                    regionStatus.and(new BitSet());
                    while(clickCounter > 0) 
                        clickSequence[--clickCounter] = -1;
                    draw();
                }
                return true;
            }
            if(clickCounter < solutionLength && i != submitRegion)
            {
                clickSequence[clickCounter] = i;
                if(i == Solution[clickCounter])
                    specialClickSnd.play();
                else
                    defaultClickSnd.play();
                clickCounter++;
                if(clickCounter == solutionLength)
                    regionStatus.set(submitRegion);
                try
                {
                    Vector vector = (Vector)regionActives.elementAt(i);
                    for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
                    {
                        Integer integer = (Integer)enumeration.nextElement();
                        if(integer.intValue() >= 0)
                            regionStatus.set(integer.intValue());
                    }

                }
                catch(ArrayIndexOutOfBoundsException _ex)
                {
                    dbg("WARNING: No activates defined for this region");
                }
                try
                {
                    Vector vector1 = (Vector)regionInactives.elementAt(i);
                    for(Enumeration enumeration1 = vector1.elements(); enumeration1.hasMoreElements();)
                    {
                        Integer integer1 = (Integer)enumeration1.nextElement();
                        if(integer1.intValue() >= 0)
                            regionStatus.clear(integer1.intValue());
                    }

                }
                catch(ArrayIndexOutOfBoundsException _ex)
                {
                    dbg("WARNING: No deactivates defined for this region");
                }
                draw();
            }
            return true;
        } else
        {
            return false;
        }
    }

    private boolean arrayCompare(int ai[], int ai1[])
    {
        boolean flag = false;
        try
        {
            for(int i = 0; ai[i] == ai1[i]; i++);
        }
        catch(ArrayIndexOutOfBoundsException _ex)
        {
            flag = true;
        }
        return flag;
    }

    private int pointToRegion(int i, int j)
    {
        return (j / regionSize.height) * xRegions + i / regionSize.width;
    }

    private Point regionToTopLeftPoint(int i)
    {
        Point point = new Point((i % xRegions) * regionSize.width, (i / xRegions) * regionSize.height);
        return point;
    }

    private void initRegionGlobals()
    {
        xRegions = Integer.parseInt(getParameter("X_REGIONS"));
        yRegions = Integer.parseInt(getParameter("Y_REGIONS"));
        totalRegions = xRegions * yRegions;
        regionActives = new Vector();
        regionInactives = new Vector();
        regionStatus = new BitSet(totalRegions);
    }

    private void getRegionGeometry(Image image)
    {
        if(regionSize == null)
            regionSize = new Dimension();
        regionSize.width = image.getWidth(this) / xRegions;
        regionSize.height = image.getHeight(this) / yRegions;
    }

    private Image[] createMapElements(Image image)
    {
        Image aimage[] = new Image[totalRegions];
        dbg("created (" + xRegions + " * " + yRegions + ") elements");
        for(int i = 0; i < yRegions; i++)
        {
            for(int j = 0; j < xRegions; j++)
            {
                aimage[j + i * xRegions] = createImage(regionSize.width, regionSize.height);
                Graphics g = aimage[j + i * xRegions].getGraphics();
                g.drawImage(image, j * -regionSize.width, i * -regionSize.height, this);
                g.dispose();
            }

        }

        return aimage;
    }

    private void loadRegionData()
    {
        URL url = getURL(topDir + regionDataFile);
        java.io.InputStream inputstream = null;
        try
        {
            inputstream = url.openStream();
        }
        catch(IOException _ex)
        {
            dbg("FATAL - Can't open region chord data.");
            System.exit(0);
        }
        DataInputStream datainputstream = new DataInputStream(inputstream);
        try
        {
            do
            {
                String s = datainputstream.readLine();
                if(s == null)
                    break;
                if(s.startsWith("#"))
                    continue;
                StringTokenizer stringtokenizer = new StringTokenizer(s, ":", false);
                String s1 = stringtokenizer.nextToken();
                String s2 = stringtokenizer.nextToken();
                StringTokenizer stringtokenizer1 = new StringTokenizer(s2, ",", false);
                if(s1.equals("S"))
                {
                    solutionLength = stringtokenizer1.countTokens();
                    Solution = new int[solutionLength];
                    clickSequence = new int[solutionLength];
                    for(int i = 0; i < solutionLength; i++)
                        Solution[i] = Integer.parseInt(stringtokenizer1.nextToken().trim());

                    dbg("Loaded solution sequence (" + solutionLength + " regions)");
                    try
                    {
                        submitRegion = Integer.parseInt(stringtokenizer.nextToken().trim());
                    }
                    catch(NoSuchElementException _ex)
                    {
                        dbg("FATAL: Submit region not specified in region file");
                        System.exit(255);
                    }
                    continue;
                }
                int j = Integer.parseInt(s1);
                for(; stringtokenizer1.hasMoreTokens(); addRegionVector(regionActives, j, Integer.parseInt(stringtokenizer1.nextToken().trim())));
                String s3 = null;
                try
                {
                    s3 = stringtokenizer.nextToken();
                }
                catch(NoSuchElementException _ex)
                {
                    dbg("Caught NoSuchElementException");
                    addRegionVector(regionInactives, j, -1);
                    continue;
                }
                for(StringTokenizer stringtokenizer2 = new StringTokenizer(s3, ",", false); stringtokenizer2.hasMoreTokens();)
                    try
                    {
                        addRegionVector(regionInactives, j, Integer.parseInt(stringtokenizer2.nextToken().trim()));
                    }
                    catch(NumberFormatException _ex)
                    {
                        addRegionVector(regionInactives, j, -1);
                    }

            } while(true);
        }
        catch(IOException _ex)
        {
            dbg("Caught IOException");
        }
        try
        {
            datainputstream.close();
            return;
        }
        catch(IOException _ex)
        {
            dbg("Caught IOException closing region chord file");
        }
    }

    private void addRegionVector(Vector vector, int i, int j)
    {
        Vector vector1 = null;
        try
        {
            vector1 = (Vector)vector.elementAt(i);
        }
        catch(ArrayIndexOutOfBoundsException _ex)
        {
            vector.insertElementAt(new Vector(), i);
            vector1 = (Vector)vector.elementAt(i);
        }
        vector1.addElement(new Integer(j));
    }

    public void dbg(String s)
    {
        if(debug)
            System.err.println("MinnieMap: " + s);
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

    private void nextWatch()
    {
        gc.drawImage(watch[watchFrame++ % watch.length], appletSize.width / 2 - 14, appletSize.height / 2 - 16, this);
        repaint();
    }

    private Color getColorParam(String s)
    {
        String s1 = getParameter(s);
        int i = 0xff000000;
        if(s1 != null)
            i = 0xff000000 | Integer.parseInt(s1, 16);
        return new Color(i);
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

    public MinnieMap()
    {
        regionDataFile = "mnocodes";
        finalDestinationURL = "chron_cookie.cgi";
        debug = false;
        loading = false;
        loaded = false;
        solved = false;
        done = false;
    }

    private Thread me;
    private Thread animator;
    private Dimension appletSize;
    private String topDir;
    private Color backgroundColor;
    private Color foregroundColor;
    private Image canvas;
    private Graphics gc;
    private Image watch[];
    private int watchFrame;
    private Image mapoff;
    private Vector regionActives;
    private Vector regionInactives;
    private int xRegions;
    private int yRegions;
    private int totalRegions;
    private Dimension regionSize;
    private BitSet regionStatus;
    private int submitRegion;
    private String regionDataFile;
    private int Solution[];
    private int solutionLength;
    private int clickCounter;
    private int clickSequence[];
    private int finalImageCount;
    private String finalImagePrefix;
    private String finalImageSuffix;
    private int finalImageX;
    private int finalImageY;
    private Image finalImages[];
    private String finalDestinationURL;
    private Image activeRegions[];
    private AudioClip defaultClickSnd;
    private AudioClip specialClickSnd;
    private AudioClip incorrectFinalSnd;
    private AudioClip correctFinalSnd;
    private MediaTracker mt;
    protected boolean debug;
    protected boolean loading;
    protected boolean loaded;
    protected boolean solved;
    protected boolean done;
}
