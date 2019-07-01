// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextBox.java

package applet.game.puzzle;

import java.applet.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TextBox extends Applet
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
        if(me == null)
        {
            me = new Thread(this, "TextBox");
            me.start();
        }
    }

    public void stop()
    {
        try
        {
            me.stop();
        }
        catch(IllegalThreadStateException _ex) { }
        me = null;
    }

    public void dbg(String s)
    {
        if(debug)
            System.err.println("TextBox: " + s);
    }

    public void run()
    {
        if(Thread.currentThread() == animator)
        {
            showFinalAnim();
            return;
        }
        if(Thread.currentThread() == blinker)
        {
            blinkCursor();
            return;
        }
        repaint();
        if(text == null)
            text = new String();
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
            Image image;
            for(image = loadImage(getURL(getParameter("WATCH_IMG"))); !statusImage(image, 300L););
            watch = createImageElements(image, 28);
        }
        loaded = true;
        if(background == null)
        {
            String s = getParameter("BACKGROUND_IMG");
            dbg("loading image " + s);
            for(background = loadImage(getURL(s)); !statusImage(background, 300L); nextWatch());
            s = getParameter("WHOLE_IMG");
            for(whole = loadImage(getURL(s)); !statusImage(whole, 300L); nextWatch());
            gridRect = getRectParam("GRID_RECT");
            dbg("gridRect = " + gridRect);
            createGridElements();
            whole.flush();
            s = getParameter("NOMATCH_IMG");
            for(whole = loadImage(getURL(s)); !statusImage(whole, 300L); nextWatch());
            nomatch = createImageElements(whole, 60);
            whole.flush();
            click = getAudioClip(getURL(getParameter("CLICK_SOUND")));
            finish = getAudioClip(getURL(getParameter("FINISH_SOUND")));
        }
        if(textRect == null)
            textRect = getRectParam("TEXT_RECT");
        if(submitRect == null)
            submitRect = getRectParam("SUBMIT_RECT");
        dbg("textRect = " + textRect);
        if(backgroundColor == null)
        {
            backgroundColor = getColorParam("BACKGROUND_COLOR");
            dbg("backColor = " + backgroundColor);
            foregroundColor = getColorParam("FOREGROUND_COLOR");
            dbg("foreColor = " + foregroundColor);
        }
        dbg("drawing...");
        if(!solved)
            draw();
        ready = true;
        requestFocus();
        if(blinker == null)
        {
            blinker = new Thread(this, "blinkerThread");
            blinker.start();
        }
    }

    private void blinkCursor()
    {
        Color color = new Color(0, 0, 0);
        Color color1 = new Color(235, 202, 124);
        int i = 0;
        while(!solved) 
        {
            try
            {
                Thread.sleep(333L);
            }
            catch(InterruptedException _ex) { }
            if(text.length() < pass.length())
            {
                Graphics g;
                synchronized(this)
                {
                    g = getGraphics();
                    g.setFont(font);
                    if((i++ & 1) == 0)
                        g.setColor(color);
                    else
                        g.setColor(color1);
                    g.drawString("-", cursorX, cursorY);
                }
                g.dispose();
            }
        }
    }

    private void draw()
    {
        synchronized(this)
        {
            drawBackground();
            drawText();
            drawElements();
            repaint();
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(loaded)
        {
            g.drawImage(canvas, 0, 0, this);
            return;
        } else
        {
            g.clearRect(0, 0, size().width, size().height);
            return;
        }
    }

    public boolean handleEvent(Event event)
    {
        if(ready)
        {
            if(event.id == 401 && !solved)
            {
                dbg("key = " + Integer.toString(event.key & 0xff, 16));
                int i = text.length();
                switch(event.key & 0x7f)
                {
                case 8: // '\b'
                case 127: // '\177'
                    if(i > 0)
                        text = text.substring(0, i - 1);
                    break;

                default:
                    if(i < pass.length())
                        text += (char)(event.key & 0x7f);
                    break;

                case 32: // ' '
                    break;
                }
                if(i != text.length())
                {
                    keystrokes++;
                    click.play();
                    if(keystrokes == 20)
                    {
                        wholeAnim = loadImage(getURL(getParameter("ANIM_IMG")));
                        statusImage(wholeAnim, 32L);
                    }
                    draw();
                    if(text.equals(pass))
                    {
                        dbg("solved puzzle!");
                        solved = true;
                        animator = new Thread(this, "animator");
                        animator.start();
                        dbg("starting animator");
                    }
                    return true;
                }
            }
            if(event.id == 501 && solved && submitRect.inside(event.x, event.y))
            {
                getAppletContext().showDocument(getURL("cookie.cgi"));
                return true;
            }
        }
        return false;
    }

    private void drawBackground()
    {
        gc.drawImage(background, 0, 0, this);
    }

    private void drawText()
    {
        int i = fm.getHeight();
        int j = (textRect.y + textRect.height / 2 + i / 2) - 3;
        int k = textRect.x + textRect.width / 2;
        String s = new String(text);
        int l = s.length();
        int i1 = pass.length() - l;
        for(int j1 = 0; j1 < i1; j1++)
            s += 45;

        s = s.substring(0, 4) + ' ' + s.substring(4, 9) + ' ' + s.substring(9);
        if(l >= 9)
            l += 2;
        else
        if(l >= 4)
            l++;
        int k1 = k - fm.stringWidth(s) / 2;
        int l1 = j;
        cursorY = l1;
        cursorX = k1 + fm.stringWidth(s.substring(0, l));
        gc.setColor(foregroundColor);
        gc.drawString(s, k1, l1);
    }

    private void createGridElements()
    {
        Image image = createImage(gridRect.width, gridRect.height);
        Graphics g = image.getGraphics();
        g.drawImage(whole, 0, 0, this);
        gridElements = new Image[ROWS][COLS];
        int i = gridRect.width / COLS;
        int j = gridRect.height / ROWS;
        int k = 0;
        for(int l = 0; l < ROWS; l++)
        {
            int i1 = 0;
            for(int j1 = 0; j1 < COLS; j1++)
            {
                Image image1 = createImage(i, j);
                Graphics g1 = image1.getGraphics();
                g1.drawImage(image, -i1, -k, this);
                gridElements[l][j1] = image1;
                i1 += i;
            }

            k += j;
        }

    }

    private Image[] createImageElements(Image image, int i)
    {
        int j = image.getHeight(this);
        int k = image.getWidth(this);
        int l = k / i;
        Image aimage[] = new Image[l];
        dbg("created " + l + " elements");
        for(int i1 = 0; i1 < l; i1++)
        {
            aimage[i1] = createImage(i, j);
            Graphics g = aimage[i1].getGraphics();
            g.drawImage(image, i1 * -i, 0, this);
            g.dispose();
        }

        return aimage;
    }

    private void nextWatch()
    {
        gc.drawImage(watch[watchFrame++ % watch.length], appletSize.width / 2 - 14, appletSize.height / 2 - 16, this);
        repaint();
    }

    private void drawElements()
    {
        gc.setColor(backgroundColor);
        gc.setColor(backgroundColor.darker());
        int i = gridRect.width / COLS;
        int j = gridRect.height / ROWS;
        int k = 0;
        int l = text.length();
        int i1 = gridRect.y;
        for(int j1 = 0; j1 < ROWS; j1++)
        {
            int k1 = gridRect.x;
            for(int l1 = 0; l1 < COLS; l1++)
            {
                if(k < l)
                {
                    int i2 = pass.indexOf(text.charAt(k));
                    if(i2 >= 0)
                    {
                        Image image = gridElements[i2 >> 2][i2 & 3];
                        gc.drawImage(image, k1, i1, this);
                    } else
                    {
                        gc.drawImage(nomatch[text.charAt(k) % 8], k1, i1, this);
                    }
                }
                k++;
                k1 += i;
            }

            i1 += j;
        }

    }

    private void showFinalAnim()
    {
        dbg("animator started");
        try
        {
            Thread.sleep(1000L);
        }
        catch(InterruptedException _ex) { }
        if(wholeAnim == null)
            wholeAnim = loadImage(getURL(getParameter("ANIM_IMG")));
        while(!statusImage(wholeAnim, 300L)) ;
        dbg("animator got image");
        anim = createImageElements(wholeAnim, 255);
        finish.play();
        for(int i = 0; i < 4; i++)
        {
            gc.drawImage(anim[i], 125, 145, this);
            dbg("drawing frame " + i);
            repaint();
            try
            {
                Thread.sleep(1000L);
            }
            catch(InterruptedException _ex) { }
        }

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

    public TextBox()
    {
        loaded = false;
        ready = false;
        solved = false;
        debug = false;
    }

    private Thread me;
    private Thread animator;
    private Thread blinker;
    private Image background;
    private Image whole;
    private AudioClip click;
    private AudioClip finish;
    private MediaTracker mt;
    private Color backgroundColor;
    private Color foregroundColor;
    private Graphics gc;
    private Image canvas;
    private Image wholeAnim;
    private Image watch[];
    private Image anim[];
    private Image nomatch[];
    private Image gridElements[][];
    private Rectangle gridRect;
    private Rectangle textRect;
    private Rectangle submitRect;
    private Font font;
    private FontMetrics fm;
    private Toolkit tk;
    private Dimension appletSize;
    private boolean loaded;
    private boolean ready;
    private boolean solved;
    private String text;
    private static String pass = "eachflowsink";
    private static final int KEY_BKSPC = 127;
    private static final int KEY_DEL = 8;
    private static int ROWS = 3;
    private static int COLS = 4;
    private int keystrokes;
    private int cursorX;
    private int cursorY;
    private boolean debug;
    private int watchFrame;

}
