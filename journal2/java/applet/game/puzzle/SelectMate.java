// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SelectMate.java

package applet.game.puzzle;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

// Referenced classes of package applet.game.puzzle:
//            MatePattern

public class SelectMate extends Applet
    implements Runnable
{

    public void init()
    {
        String s = getParameter("DEBUG");
        appletSize = size();
        if(s != null && s.equalsIgnoreCase("true"))
            debug = true;
    }

    public void start()
    {
        if(solved)
        {
            animator = new Thread(this, "animator");
            animator.start();
            return;
        }
        if(me == null)
        {
            me = new Thread(this, "SelectMate");
            me.start();
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

    public void dbg(String s)
    {
        if(debug)
            System.err.println("SelectMate: " + s);
    }

    public void run()
    {
        if(solved)
            animate();
        repaint();
        if(canvas == null)
        {
            canvas = createImage(appletSize.width, appletSize.height);
            gc = canvas.getGraphics();
        }
        if(font == null)
        {
            font = new Font("Courier", 0, 14);
            gc.setFont(font);
            tk = Toolkit.getDefaultToolkit();
            fm = tk.getFontMetrics(font);
        }
        if(watch == null)
        {
            topDir = getParameter("TOP_DIR");
            Image image;
            for(image = loadImage(getURL(topDir + getParameter("WATCH_IMG"))); !statusImage(image, 300L););
            watch = createImageElements(image, 10);
        }
        loading = true;
        if(background == null)
        {
            selectedHistory = new MatePattern[6];
            for(int i = 0; i < 5; i++)
                selectedHistory[i] = null;

            leftRect = getRectParam("LEFT_RECT");
            rightRect = getRectParam("RIGHT_RECT");
            mixRect = getRectParam("MIX_RECT");
            submitRect = getRectParam("SUBMIT_RECT");
            thumbRect = getRectParam("THUMB_RECT");
            dbg("loading graphics");
            for(background = loadImage(getURL(topDir + getParameter("BACKGROUND_IMG"))); !statusImage(background, 300L); nextWatch());
            for(submit = loadImage(getURL(topDir + getParameter("SUBMIT_IMG"))); !statusImage(submit, 300L); nextWatch());
            genImage = new Image[5][];
            thmImage = new Image[5][];
            Image image1;
            for(image1 = loadImage(getURL(topDir + "p2-img/p2-0gen.jpg")); !statusImage(image1, 300L); nextWatch());
            genImage[0] = createImageElements(image1, 5);
            image1.flush();
            for(int j = 1; j < 5; j++)
            {
                for(image1 = loadImage(getURL(topDir + "p2-img/p2-" + j + "gen.jpg")); !statusImage(image1, 300L); nextWatch());
                genImage[j] = createImageElements(image1, 9);
                image1.flush();
                for(image1 = loadImage(getURL(topDir + "p2-img/p2-" + j + "thumb.jpg")); !statusImage(image1, 300L); nextWatch());
                thmImage[j - 1] = createImageElements(image1, 9);
                image1.flush();
            }

            for(image1 = loadImage(getURL(topDir + "p2-img/p2-5gen.jpg")); !statusImage(image1, 300L); nextWatch());
            finalAnim = createImageElements(image1, 5);
            image1.flush();
            for(image1 = loadImage(getURL(topDir + "p2-img/p2-5thumb.jpg")); !statusImage(image1, 300L); nextWatch());
            thmImage[4] = createImageElements(image1, 1);
            image1.flush();
            for(image1 = loadImage(getURL(topDir + "p2-img/p2-dead.jpg")); !statusImage(image1, 300L); nextWatch());
            deadImage = createImageElements(image1, 16);
            image1.flush();
            currentGen = 0;
            soundClip = new AudioClip[5];
            for(int k = 0; k < 5; k++)
                soundClip[k] = getAudioClip(getURL(topDir + "sounds/" + animalClipFile[k]));

            finalClip = getAudioClip(getURL(topDir + "sounds/final4.au"));
            loadGenData();
        }
        if(backgroundColor == null)
        {
            backgroundColor = getColorParam("BACKGROUND_COLOR");
            dbg("backColor = " + backgroundColor);
            foregroundColor = getColorParam("FOREGROUND_COLOR");
            dbg("foreColor = " + foregroundColor);
        }
        loading = false;
        loaded = true;
        dbg("drawing...");
        draw();
        requestFocus();
    }

    private void animate()
    {
        int i = 0;
        draw();
        try
        {
            Thread.sleep(750L);
        }
        catch(InterruptedException _ex) { }
        finalClip.play();
        for(int j = 0; j < finalAnim.length; j++)
        {
            if(i++ > 12)
            {
                done = true;
                gc.drawImage(submit, submitRect.x, submitRect.y, this);
            }
            gc.drawImage(finalAnim[j], mixRect.x, mixRect.y, this);
            repaint();
            try
            {
                Thread.sleep(400L);
            }
            catch(InterruptedException _ex) { }
        }

    }

    private void draw()
    {
        gc.drawImage(background, 0, 0, this);
        gc.drawImage(genImage[currentGen][leftSelection], leftRect.x, leftRect.y, this);
        gc.drawImage(genImage[currentGen][rightSelection], rightRect.x, rightRect.y, this);
        int i = thumbRect.x;
        int j = thumbRect.y;
        byte byte0 = 26;
        for(int k = 0; k < 5; k++)
        {
            if(selectedHistory[k] == null)
                break;
            gc.drawImage(thmImage[k][selectedHistory[k].child], i, j, this);
            j += byte0;
        }

        if(currentMatePattern != null)
        {
            MatePattern matepattern = currentMatePattern;
            if(matepattern.living)
            {
                if(currentGen < 4)
                    gc.drawImage(genImage[currentGen + 1][matepattern.child], mixRect.x, mixRect.y, this);
            } else
            {
                gc.drawImage(deadImage[matepattern.child], mixRect.x, mixRect.y, this);
            }
        }
        repaint();
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

    public boolean handleEvent(Event event)
    {
        if(!loaded)
            return true;
        if(done)
        {
            if(event.id == 501 && (submitRect.inside(event.x, event.y) || mixRect.inside(event.x, event.y)))
                getAppletContext().showDocument(getURL("cookie.cgi"));
            return true;
        }
        if(event.id == 503)
        {
            if(submitRect.inside(event.x, event.y))
            {
                if(!submitState)
                {
                    gc.drawImage(submit, submitRect.x, submitRect.y, this);
                    submitState = true;
                    repaint();
                }
            } else
            if(submitState)
            {
                submitState = false;
                draw();
            }
            return false;
        }
        if(event.id == 501)
        {
            if(leftRect.inside(event.x, event.y) && currentGen == 0)
            {
                leftSelection++;
                if(leftSelection == genImage[currentGen].length)
                    leftSelection = 0;
                gc.drawImage(genImage[currentGen][leftSelection], leftRect.x, leftRect.y, this);
                repaint();
            }
            if(rightRect.inside(event.x, event.y))
            {
                rightSelection++;
                if(rightSelection == genImage[currentGen].length)
                    rightSelection = 0;
                gc.drawImage(genImage[currentGen][rightSelection], rightRect.x, rightRect.y, this);
                repaint();
            }
            if(mixRect.inside(event.x, event.y) && currentMatePattern != null && currentMatePattern.living)
            {
                selectedHistory[currentGen] = currentMatePattern;
                leftSelection = currentMatePattern.child;
                rightSelection = 0;
                currentMatePattern = null;
                currentGen++;
                draw();
            }
            if(submitRect.inside(event.x, event.y))
            {
                MatePattern matepattern = getMatePattern(leftSelection, rightSelection);
                matepattern.sound.play();
                currentMatePattern = matepattern;
                if(matepattern.living)
                {
                    if(currentGen == 4)
                    {
                        selectedHistory[currentGen] = matepattern;
                        solved = true;
                        animator = new Thread(this, "animator");
                        animator.start();
                        return true;
                    }
                    gc.drawImage(genImage[currentGen + 1][matepattern.child], mixRect.x, mixRect.y, this);
                } else
                {
                    gc.drawImage(deadImage[matepattern.child], mixRect.x, mixRect.y, this);
                }
                repaint();
            }
            if(thumbRect.inside(event.x, event.y))
            {
                int i = thumbRect.x;
                int j = thumbRect.y;
                Rectangle rectangle = new Rectangle(i + 7, j + 1, 36, 17);
                for(int k = 0; k < currentGen; k++)
                {
                    if(rectangle.inside(event.x, event.y))
                    {
                        currentGen = k;
                        MatePattern matepattern1 = selectedHistory[k];
                        currentMatePattern = matepattern1;
                        leftSelection = matepattern1.parent1;
                        rightSelection = matepattern1.parent2;
                        for(; k < 5; k++)
                            selectedHistory[k] = null;

                        draw();
                        break;
                    }
                    rectangle.translate(0, 26);
                }

            }
            return true;
        } else
        {
            return false;
        }
    }

    private MatePattern getMatePattern(int i, int j)
    {
        for(Enumeration enumeration = genData[currentGen].elements(); enumeration.hasMoreElements();)
        {
            MatePattern matepattern = (MatePattern)enumeration.nextElement();
            if(matepattern.parent1 == leftSelection && matepattern.parent2 == rightSelection || matepattern.parent2 == leftSelection && matepattern.parent1 == rightSelection)
                return matepattern;
        }

        return null;
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

    private Rectangle getRectParam(String s)
    {
        String s1 = getParameter(s);
        Rectangle rectangle = new Rectangle();
        int i = 1;
        boolean flag = false;
        if(s1.charAt(0) == '+')
        {
            int j = s1.indexOf('+', i);
            rectangle.x = Integer.parseInt(s1.substring(i, j));
            i = j + 1;
            j = s1.indexOf('x', i);
            rectangle.y = Integer.parseInt(s1.substring(i, j));
            i = j + 1;
            j = s1.indexOf('x', i);
            rectangle.width = Integer.parseInt(s1.substring(i, j));
            i = j + 1;
            rectangle.height = Integer.parseInt(s1.substring(i));
        }
        return rectangle;
    }

    private Color getColorParam(String s)
    {
        String s1 = getParameter(s);
        int i = 0xff000000 | Integer.parseInt(s1, 16);
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

    private void loadGenData()
    {
        URL url = getURL(topDir + "gen.3");
        java.io.InputStream inputstream = null;
        DataInputStream datainputstream = null;
        try
        {
            inputstream = url.openStream();
        }
        catch(IOException _ex)
        {
            dbg("FATAL - no gen data file");
            System.exit(0);
        }
        datainputstream = new DataInputStream(inputstream);
        int i = 5;
        genData = new Vector[i];
        while(i-- > 0) 
            genData[i] = new Vector();
        try
        {
            do
            {
                String s = datainputstream.readLine();
                if(s == null || s.trim().equals(""))
                    break;
                StringTokenizer stringtokenizer = new StringTokenizer(s);
                int j = Integer.parseInt((String)stringtokenizer.nextElement()) - 1;
                int k = Integer.parseInt((String)stringtokenizer.nextElement());
                int l = Integer.parseInt((String)stringtokenizer.nextElement());
                boolean flag = false;
                String s1 = (String)stringtokenizer.nextElement();
                if(s1.charAt(0) == 'L')
                    flag = true;
                int i1 = Integer.parseInt(s1.substring(1));
                int j1 = Integer.parseInt((String)stringtokenizer.nextElement());
                genData[j].addElement(new MatePattern(k, l, i1, flag, soundClip[j1]));
                String s2 = "alive";
                if(!flag)
                    s2 = "dead";
                dbg("gen " + j + " p1 " + k + " p2 " + l + " c " + i1 + " " + s2);
            } while(true);
        }
        catch(IOException _ex)
        {
            dbg("caught IOException on read from " + datainputstream);
        }
        try
        {
            datainputstream.close();
            return;
        }
        catch(IOException _ex)
        {
            dbg("error closing input stream... continuing");
        }
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

    public SelectMate()
    {
        submitState = false;
        loading = false;
        loaded = false;
        solved = false;
        done = false;
        debug = false;
    }

    private Thread me;
    private Thread animator;
    private Image background;
    private MediaTracker mt;
    private Color backgroundColor;
    private Color foregroundColor;
    private Image canvas;
    private Graphics gc;
    private Image submit;
    private boolean submitState;
    private Image watch[];
    private Image finalAnim[];
    private Vector genData[];
    private Image genImage[][];
    private Image thmImage[][];
    private Image deadImage[];
    private AudioClip soundClip[];
    private AudioClip finalClip;
    private String topDir;
    private static String animalClipFile[] = {
        "live.au", "livehalf.au", "livenone.au", "deadhalf.au", "dead.au"
    };
    private int currentGen;
    private MatePattern currentMatePattern;
    private Font font;
    private FontMetrics fm;
    private Toolkit tk;
    private Dimension appletSize;
    private Rectangle leftRect;
    private Rectangle rightRect;
    private Rectangle mixRect;
    private Rectangle submitRect;
    private Rectangle thumbRect;
    private int leftSelection;
    private int rightSelection;
    private MatePattern selectedHistory[];
    private boolean loading;
    private boolean loaded;
    private boolean solved;
    private boolean done;
    private boolean debug;
    private int watchFrame;

}
