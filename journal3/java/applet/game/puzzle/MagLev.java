// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MagLev.java

package applet.game.puzzle;

import java.applet.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MagLev extends Applet
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
            me = new Thread(this, "MagLev");
            me.start();
        }
        if(lastClip != null)
            lastClip.loop();
    }

    public void stop()
    {
        if(lastClip != null)
            lastClip.stop();
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
            System.err.println("MagLev: " + s);
    }

    public void run()
    {
        if(Thread.currentThread() == animator)
        {
            animate();
            return;
        }
        if(loaded || ready || solved)
            return;
        if(canvas == null)
        {
            canvas = createImage(appletSize.width, appletSize.height);
            gc = canvas.getGraphics();
            gc.clearRect(0, 0, appletSize.width, appletSize.height);
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
            watch = createImageElements(image, 10);
        }
        loaded = true;
        if(background == null)
        {
            String s = getParameter("BACKGROUND_IMG");
            dbg("loading image " + s);
            for(background = loadImage(getURL(s)); !statusImage(background, 300L); nextWatch());
            s = getParameter("RAIL_IMG");
            rail = new Image[2];
            for(rail[0] = loadImage(getURL(s)); !statusImage(rail[0], 300L); nextWatch());
            s = getParameter("RAIL_ON_IMG");
            for(rail[1] = loadImage(getURL(s)); !statusImage(rail[1], 300L); nextWatch());
            thrust = new Image[2];
            s = getParameter("THRUST_IMG");
            for(thrust[0] = loadImage(getURL(s)); !statusImage(thrust[0], 300L); nextWatch());
            s = getParameter("THRUST_ON_IMG");
            for(thrust[1] = loadImage(getURL(s)); !statusImage(thrust[1], 300L); nextWatch());
            s = getParameter("BUTTON_IMG");
            for(whole = loadImage(getURL(s)); !statusImage(whole, 300L); nextWatch());
            button = createImageElements(whole, 2);
            try
            {
                whole.flush();
            }
            catch(Exception _ex) { }
            takeoffClip = getAudioClip(getURL("sounds/takeoff.au"));
            for(submit = loadImage(getURL("p3-img/final-but.jpg")); !statusImage(submit, 300L); nextWatch());
            for(animation = loadImage(getURL("p3-img/final.jpg")); !statusImage(animation, 300L); nextWatch());
            leftRailRect = getRectParam("LEFTRAIL_RECT");
            rightRailRect = getRectParam("RIGHTRAIL_RECT");
            thrustRect = getRectParam("THRUST_RECT");
            solvedRect = getRectParam("SOLVED_RECT");
            grindClip = getAudioClip(getURL("sounds/grind.au"));
            thrust1 = getAudioClip(getURL("sounds/hum1.au"));
            thrust2 = getAudioClip(getURL("sounds/hum2.au"));
            thrust3 = getAudioClip(getURL("sounds/hum3.au"));
            thrust4 = getAudioClip(getURL("sounds/hum4.au"));
            thrust5 = getAudioClip(getURL("sounds/hum5.au"));
        }
        if(backgroundColor == null)
        {
            backgroundColor = getColorParam("BACKGROUND_COLOR");
            dbg("backColor = " + backgroundColor);
            foregroundColor = getColorParam("FOREGROUND_COLOR");
            dbg("foreColor = " + foregroundColor);
        }
        dbg("drawing...");
        leftButtonsRect = new Rectangle(5, 5, button[0].getWidth(this), 80 + button[0].getHeight(this));
        rightButtonsRect = new Rectangle(appletSize.width - 5 - button[0].getWidth(this), 5, button[0].getWidth(this), 80 + button[0].getHeight(this));
        buttonRect = new Rectangle(5, 5, button[0].getWidth(this), button[0].getHeight(this));
        if(!solved)
            draw();
        ready = true;
        requestFocus();
    }

    private void draw()
    {
        synchronized(this)
        {
            drawBackground();
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
            if(!solved)
            {
                g.drawImage(canvas, 0, 0, this);
                return;
            } else
            {
                g.drawImage(animation, 0, 0, this);
                g.drawImage(submit, solvedRect.x, solvedRect.y, this);
                return;
            }
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
            boolean flag = false;
            if(event.id == 501)
            {
                if(solved)
                {
                    if(solvedRect.inside(event.x, event.y))
                        getAppletContext().showDocument(getURL("maglev_cookie.cgi"));
                    return true;
                }
                if(leftButtonsRect.inside(event.x, event.y))
                {
                    buttonRect.move(leftButtonsRect.x, leftButtonsRect.y);
                    for(int i = 0; i < 5; i++)
                    {
                        if(buttonRect.inside(event.x, event.y))
                        {
                            leftButtonState ^= 1 << i;
                            flag = true;
                            break;
                        }
                        buttonRect.translate(0, 20);
                    }

                }
                if(rightButtonsRect.inside(event.x, event.y))
                {
                    buttonRect.move(rightButtonsRect.x, rightButtonsRect.y);
                    for(int j = 0; j < 5; j++)
                    {
                        if(buttonRect.inside(event.x, event.y))
                        {
                            rightButtonState ^= 1 << j;
                            flag = true;
                            break;
                        }
                        buttonRect.translate(0, 20);
                    }

                }
                if(leftRailRect.inside(event.x, event.y))
                {
                    dragging = 0;
                    flag = true;
                }
                if(rightRailRect.inside(event.x, event.y))
                {
                    dragging = 1;
                    flag = true;
                }
                if(thrustRect.inside(event.x, event.y))
                {
                    dragging = 2;
                    flag = true;
                }
            }
            if(event.id == 502)
            {
                dragging = -1;
                draw();
                return true;
            }
            if(event.id == 506)
            {
                int k = event.x;
                if(dragging == 0)
                {
                    if(k < 65)
                        k = 65;
                    if(k > 115)
                        k = 115;
                    int l = Math.round((float)((k - 65) * 6) / 60F);
                    leftRailRect.move((65 - leftRailRect.width / 2) + l * 10, leftRailRect.y);
                    rightRailRect.move(315 - rightRailRect.width / 2 - l * 10, rightRailRect.y);
                    if(railPosition != l)
                    {
                        railPosition = l;
                        flag = true;
                    }
                }
                if(dragging == 1)
                {
                    if(k < 265)
                        k = 265;
                    if(k > 315)
                        k = 315;
                    int i1 = Math.round((float)((315 - k) * 6) / 60F);
                    leftRailRect.move((65 - leftRailRect.width / 2) + i1 * 10, leftRailRect.y);
                    rightRailRect.move(315 - rightRailRect.width / 2 - i1 * 10, rightRailRect.y);
                    if(railPosition != i1)
                    {
                        railPosition = i1;
                        flag = true;
                    }
                }
                if(dragging == 2)
                {
                    int j1 = event.y;
                    if(j1 < 66)
                        j1 = 66;
                    if(j1 > 106)
                        j1 = 106;
                    int k1 = Math.round((float)((106 - j1) * 5) / 50F);
                    thrustRect.move(thrustRect.x, 106 - thrustRect.height / 2 - k1 * 10);
                    if(thrustPosition != k1)
                    {
                        thrustPosition = k1;
                        flag = true;
                    }
                }
            }
            if(flag)
            {
                leftValue = getNumVal(leftButtonState, leftNumSet) * thrustNumSet[thrustPosition];
                rightValue = getNumVal(rightButtonState, rightNumSet) * thrustNumSet[thrustPosition];
                draw();
                if(leftValue == rightValue)
                {
                    if(lastClip != null)
                        lastClip.stop();
                    if(leftValue == 0)
                        return true;
                    if(leftValue == 48)
                    {
                        ready = false;
                        animator = new Thread(this, "animator");
                        animator.start();
                        return true;
                    }
                    switch(thrustPosition)
                    {
                    case 0: // '\0'
                        thrust1.loop();
                        lastClip = thrust1;
                        break;

                    case 1: // '\001'
                        thrust2.loop();
                        lastClip = thrust2;
                        break;

                    case 2: // '\002'
                        thrust3.loop();
                        lastClip = thrust3;
                        break;

                    case 3: // '\003'
                        thrust4.loop();
                        lastClip = thrust4;
                        break;

                    case 4: // '\004'
                        thrust5.loop();
                        lastClip = thrust5;
                        break;
                    }
                } else
                {
                    if(lastClip != null)
                        lastClip.stop();
                    grindClip.loop();
                    lastClip = grindClip;
                }
                return true;
            }
        }
        return false;
    }

    private void drawBackground()
    {
        gc.drawImage(background, 0, 0, this);
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

    private void drawElements()
    {
        gc.setColor(backgroundColor);
        gc.setColor(backgroundColor.darker());
        int i = 5;
        byte byte0 = 5;
        int j = appletSize.width - 5 - button[0].getWidth(this);
        int k = leftButtonState;
        int l = rightButtonState;
        for(int i1 = 0; i1 < 5; i1++)
        {
            if((k & 1) == 1)
                gc.drawImage(button[0], byte0, i, this);
            else
                gc.drawImage(button[1], byte0, i, this);
            if((l & 1) == 1)
                gc.drawImage(button[0], j, i, this);
            else
                gc.drawImage(button[1], j, i, this);
            k >>= 1;
            l >>= 1;
            i += 20;
        }

        String s = new String(leftValue + "");
        String s1 = new String(rightValue + "");
        gc.drawString(s, 5, appletSize.height - 50);
        gc.drawString(s1, appletSize.width - 5 - fm.stringWidth(s1), appletSize.height - 50);
        if(dragging >= 0)
        {
            if(dragging == 0 || dragging == 1)
            {
                gc.drawImage(rail[1], leftRailRect.x - 52, leftRailRect.y - 54, this);
                gc.drawImage(rail[1], rightRailRect.x - 52, rightRailRect.y - 54, this);
                gc.drawImage(thrust[0], thrustRect.x - 63, thrustRect.y - 60, this);
            } else
            {
                gc.drawImage(thrust[1], thrustRect.x - 63, thrustRect.y - 60, this);
                gc.drawImage(rail[0], leftRailRect.x - 52, leftRailRect.y - 54, this);
                gc.drawImage(rail[0], rightRailRect.x - 52, rightRailRect.y - 54, this);
            }
        } else
        {
            gc.drawImage(rail[0], leftRailRect.x - 52, leftRailRect.y - 54, this);
            gc.drawImage(rail[0], rightRailRect.x - 52, rightRailRect.y - 54, this);
            gc.drawImage(thrust[0], thrustRect.x - 63, thrustRect.y - 60, this);
        }
        repaint();
    }

    private int getNumVal(int i, int ai[][])
    {
        int j = 0;
        for(int k = 0; k < 5; k++)
            if((i & 1 << k) > 0)
                j += ai[railPosition][k];

        return j;
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

    private void animate()
    {
        takeoffClip.play();
        try
        {
            Thread.sleep(900L);
        }
        catch(InterruptedException _ex) { }
        int i = appletSize.width;
        int j = appletSize.height;
        int k = i / 10;
        int l = i / 2 - k;
        i = k * 2;
        for(int i1 = 0; i1 < 5; i1++)
        {
            Graphics g = canvas.getGraphics();
            g.clipRect(l, 0, i, j);
            g.drawImage(animation, 0, 0, this);
            repaint();
            try
            {
                Thread.sleep(900L);
            }
            catch(InterruptedException _ex) { }
            g.dispose();
            l -= k;
            i += k * 2;
        }

        gc.drawImage(submit, solvedRect.x, solvedRect.y, this);
        repaint();
        ready = true;
        solved = true;
    }

    private void loadRemainder()
    {
    }

    public MagLev()
    {
        dragging = -1;
        loaded = false;
        ready = false;
        solved = false;
        debug = false;
    }

    private Thread me;
    private Thread animator;
    private MediaTracker mt;
    private Color backgroundColor;
    private Color foregroundColor;
    private Graphics gc;
    private Image canvas;
    private Image whole;
    private Image animation;
    private Image submit;
    private Image watch[];
    private Image rail[];
    private Image thrust[];
    private Image button[];
    private Image background;
    private AudioClip thrust1;
    private AudioClip thrust2;
    private AudioClip thrust3;
    private AudioClip thrust4;
    private AudioClip thrust5;
    private AudioClip grindClip;
    private AudioClip takeoffClip;
    private AudioClip lastClip;
    private Rectangle leftRailRect;
    private Rectangle rightRailRect;
    private Rectangle thrustRect;
    private Rectangle solvedRect;
    private Rectangle leftButtonsRect;
    private Rectangle rightButtonsRect;
    private Rectangle buttonRect;
    private Rectangle railRect;
    private int leftButtonState;
    private int rightButtonState;
    private int leftValue;
    private int rightValue;
    private int railPosition;
    private int thrustPosition;
    private int dragging;
    private Font font;
    private FontMetrics fm;
    private Toolkit tk;
    private Dimension appletSize;
    private boolean loaded;
    private boolean ready;
    private boolean solved;
    private boolean debug;
    private int watchFrame;
    private int mouseClicks;
    private static final int leftNumSet[][] = {
        {
            1, 3, 2, 2, 1
        }, {
            7, 40, 3, 6, 9
        }, {
            6, 3, 18, 4, 30
        }, {
            38, 3, 5, 1, 22
        }, {
            3, 21, 6, 3, 4
        }, {
            12, 8, 10, 2, 2
        }
    };
    private static final int rightNumSet[][] = {
        {
            2, 3, 7, 20, 7
        }, {
            8, 2, 11, 3, 5
        }, {
            7, 1, 4, 3, 4
        }, {
            24, 9, 3, 1, 11
        }, {
            2, 7, 1, 31, 2
        }, {
            13, 4, 21, 14, 2
        }
    };
    private static final int thrustNumSet[] = {
        1, 2, 4, 6, 8
    };

}
