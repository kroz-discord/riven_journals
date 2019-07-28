// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Pony.java

import java.applet.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Pony extends Applet
    implements Runnable
{

    public void init()
    {
        ps = myAtoi(getParameter("state"));
        appletSize = size();
    }

    public void start()
    {
        if(me == null)
        {
            me = new Thread(this, "Mover");
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
            solved = false;
        }
    }

    public void dbg(String s)
    {
        if(debug)
            System.err.print(s);
    }

    public void run()
    {
        if(game_over)
            finalAnimation();
        if(solved)
        {
            solved = false;
            animate();
        }
        repaint();
        if(!loaded)
        {
            if(canvas == null)
            {
                canvas = createImage(appletSize.width, appletSize.height);
                gc = canvas.getGraphics();
                back = createImage(appletSize.width, appletSize.height);
                gb = back.getGraphics();
            }
            if(watch == null)
            {
                Image image;
                for(image = loadImage(getURL("images/watch.gif")); !statusImage(image, 300L););
                watch = createImageElements(image, 10);
                gb = back.getGraphics();
            }
            if(background == null)
            {
                for(background = loadImage(getURL("images/back.jpg")); !statusImage(background, 200L); nextWatch());
                gb.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
            }
        }
        if(!loaded)
            loadPuzzleMedia(ps, ps, !loaded);
        if(!game_over)
        {
            drawStatTool(gb);
            drawDynTool(gc);
        }
        if(stampAnims == null)
        {
            stamp = loadImage(getURL("images/action.gif"));
            buttClickNo = getAudioClip(getURL("sounds/stamp_wrong.au"));
            buttClickYes = getAudioClip(getURL("sounds/stamp.au"));
            stampAnims = new Image[4][3];
            for(int i = 0; i <= 3; i++)
            {
                for(int k = 0; k <= 2; k++)
                    stampAnims[i][k] = loadImage(getURL("images/puzzle" + (i + 1) + "/p" + (i + 1) + "-solved" + (k + 1) + ".jpg"));

            }

            if(ps > 0)
            {
                for(int l = 1; l < ps + 1; l++)
                {
                    Applet applet = null;
                    String s = "stamp" + l;
                    applet = getAppletContext().getApplet(s);
                    if(applet != null)
                        if(!(applet instanceof Stamp))
                            dbg("Error:  Can't locate stamp #" + l + ".\n");
                        else
                            ((Stamp)applet).fallBack(false);
                }

            }
            if(ps < 3)
                loadPuzzleMedia(ps + 1, 3, !loaded);
        }
        if(ps == 3 && !there)
        {
            if(finalAnim == null)
            {
                finalAnim = new Image[4];
                for(int j = 0; j < 4; j++)
                    finalAnim[j] = loadImage(getURL("images/final" + (j + 1) + ".jpg"));

            }
            finishClip = getAudioClip(getURL("sounds/stamp_done.au"));
            there = true;
        }
    }

    public void loadPuzzleMedia(int i, int j, boolean flag)
    {
        for(int k = i; k < j + 1; k++)
        {
            parts[k] = new PuzzlePart[numParts[k]];
            for(int l = 0; l <= numGoodParts[k] - 1; l++)
            {
                String s = "images/puzzle" + (k + 1) + "/p" + (k + 1) + "-yest" + (l + 1) + ".gif";
                parts[k][l] = new PuzzlePart(loadImage(getURL(s)));
                if(flag)
                    while(!statusImage(parts[k][l].getImage(), 200L)) 
                        if(flag)
                            nextWatch();
                parts[k][l].setDestination(new Point(pSol[k][l][0], pSol[k][l][1]));
                parts[k][l].setXLoc(pLoc[k][l][0]);
                parts[k][l].setYLoc(pLoc[k][l][1]);
            }

            for(int i1 = 0; i1 <= numParts[k] - numGoodParts[k] - 1; i1++)
            {
                int j1 = i1 + numGoodParts[k];
                String s1 = "images/puzzle" + (k + 1) + "/p" + (k + 1) + "-not" + (i1 + 1) + ".gif";
                parts[k][j1] = new PuzzlePart(false, loadImage(getURL(s1)));
                if(flag)
                    while(!statusImage(parts[k][j1].getImage(), 200L)) 
                        if(flag)
                            nextWatch();
                parts[k][j1].setDestination(new Point(pSol[k][j1][0], pSol[k][j1][1]));
                parts[k][j1].setXLoc(pLoc[k][j1][0]);
                parts[k][j1].setYLoc(pLoc[k][j1][1]);
            }

        }

        if(flag)
            loaded = true;
    }

    public boolean handleEvent(Event event)
    {
        if(!animating && event.x < 246 && event.y < 140 && event.x > 0 && event.y > 0)
        {
            if(event.id == 501)
            {
                if(game_over)
                {
                    if(event.x > 79 && event.x < 169 && event.y > 26 && event.y < 116)
                        getAppletContext().showDocument(getURL("p5_cookie.cgi"));
                    return true;
                }
                if(!solved)
                {
                    for(int i = 0; i <= numParts[ps] - 1 && !holding_image; i++)
                    {
                        luckHeight = parts[ps][i].getImage().getHeight(this);
                        luckWidth = parts[ps][i].getImage().getWidth(this);
                        int j = parts[ps][i].x;
                        int l = parts[ps][i].y;
                        if(j < event.x && event.x < j + luckWidth && l < event.y && event.y < l + luckHeight)
                        {
                            luckyNum = i;
                            holding_image = true;
                            xoff = event.x - j;
                            yoff = event.y - l;
                            drawStatTool(gb);
                            drawDynTool(gc);
                        }
                    }

                }
                if(!holding_image && event.x < 189 && event.y < 129 && event.x > 178 && event.y > 118)
                {
                    stamp_check = true;
                    drawButton(gc);
                }
            }
            if(event.id == 506 && holding_image)
            {
                parts[ps][luckyNum].x = event.x - xoff;
                parts[ps][luckyNum].y = event.y - yoff;
                drawDynTool(gc);
            }
            if(event.id == 502)
            {
                if(holding_image)
                {
                    holding_image = false;
                    if(parts[ps][luckyNum].isHot())
                    {
                        parts[ps][luckyNum].lockInPart();
                        parts[ps][luckyNum].x = parts[ps][luckyNum].destination.x;
                        parts[ps][luckyNum].y = parts[ps][luckyNum].destination.y;
                    } else
                    {
                        parts[ps][luckyNum].unlockPart();
                    }
                    drawDynTool(gc);
                }
                if(stamp_check && !solved)
                {
                    stamp_check = false;
                    boolean flag = true;
                    for(int k = 0; k < numGoodParts[ps]; k++)
                        if(!parts[ps][k].isPuzzlePart() || !parts[ps][k].partLockedIn())
                            flag = false;

                    for(int i1 = numGoodParts[ps]; i1 < numParts[ps]; i1++)
                        if(parts[ps][i1].partLockedIn())
                            flag = false;

                    if(flag)
                    {
                        boolean flag1 = false;
                        solved = true;
                        animating = true;
                        animator = new Thread(this, "animator");
                        animator.start();
                    } else
                    {
                        buttClickNo.play();
                        drawDynTool(gc);
                    }
                }
            }
        }
        return false;
    }

    private void drawStatTool(Graphics g)
    {
        g.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
        for(int i = numParts[ps] - 1; i >= 0; i--)
            if(i != luckyNum)
                g.drawImage(parts[ps][i].getImage(), parts[ps][i].x, parts[ps][i].y, this);

    }

    private void drawButton(Graphics g)
    {
        g.drawImage(stamp, 177, 118, this);
        repaint();
    }

    private void drawDynTool(Graphics g)
    {
        g.drawImage(back, 0, 0, this);
        g.drawImage(parts[ps][luckyNum].getImage(), parts[ps][luckyNum].x, parts[ps][luckyNum].y, this);
        repaint();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        g.drawImage(canvas, 0, 0, this);
    }

    private synchronized void finalAnimation()
    {
        if(!really_over)
        {
            gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
            gc.drawImage(finalAnim[0], 79, 26, this);
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
                gc.drawImage(finalAnim[i], 79, 26, this);
                repaint();
                try
                {
                    Thread.sleep(230L);
                }
                catch(InterruptedException _ex) { }
            }

        }
        really_over = true;
    }

    private synchronized void animate()
    {
        solved = false;
        if(!game_over)
        {
            gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
            gc.drawImage(stampAnims[ps][0], 0, 0, this);
            repaint();
            try
            {
                Thread.sleep(300L);
            }
            catch(InterruptedException _ex) { }
            buttClickYes.play();
            try
            {
                Thread.sleep(50L);
            }
            catch(InterruptedException _ex) { }
            for(int i = 0; i <= 2; i++)
            {
                gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
                gc.drawImage(stampAnims[ps][i], 0, 0, this);
                repaint();
                try
                {
                    Thread.sleep(300L);
                }
                catch(InterruptedException _ex) { }
            }

            for(int j = 2; j >= 0; j--)
            {
                gc.drawImage(background, 0, 0, appletSize.width, appletSize.height, this);
                gc.drawImage(stampAnims[ps][j], 0, 0, this);
                repaint();
                try
                {
                    Thread.sleep(270L);
                }
                catch(InterruptedException _ex) { }
            }

            Applet applet = null;
            String s = "stamp" + (ps + 1);
            applet = getAppletContext().getApplet(s);
            if(applet != null)
                if(!(applet instanceof Stamp))
                    dbg("Error:  Can't locate stamp #" + (ps + 1) + ".\n");
                else
                    ((Stamp)applet).fallBack(true);
            try
            {
                Thread.sleep(901L);
            }
            catch(InterruptedException _ex) { }
            ps++;
            if(ps == 4)
            {
                game_over = true;
                animator = new Thread(this, "animator");
                animator.start();
            } else
            {
                luckyNum = 0;
                drawStatTool(gb);
                drawDynTool(gc);
                repaint();
            }
            animating = false;
            return;
        } else
        {
            return;
        }
    }

    private int myAtoi(String s)
    {
        if(s.equals("jean21"))
            return 0;
        if(s.equals("paul23"))
            return 1;
        if(s.equals("peter27"))
            return 2;
        return !s.equals("timothy29") ? 0 : 3;
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

    public Pony()
    {
        debug = false;
        holding_image = false;
        loaded = false;
        solved = false;
        there = false;
        stamp_check = false;
        really_over = false;
        animating = false;
        game_over = false;
        parts = new PuzzlePart[4][];
    }

    private boolean debug;
    private boolean holding_image;
    private boolean loaded;
    private boolean solved;
    private boolean there;
    private boolean stamp_check;
    private boolean really_over;
    private boolean animating;
    private boolean game_over;
    private int numParts[] = {
        13, 17, 14, 17
    };
    private int numGoodParts[] = {
        7, 9, 8, 9
    };
    private int watchFrame;
    private int luckyNum;
    private int ps;
    private int luckWidth;
    private int luckHeight;
    private int xoff;
    private int yoff;
    private PuzzlePart parts[][];
    private Thread me;
    private Thread animator;
    private Thread button;
    private Graphics gc;
    private Graphics gb;
    private Graphics puzzle;
    private Dimension appletSize;
    private MediaTracker mt;
    private Image canvas;
    private Image stamp;
    private Image back;
    private Image background;
    private Image watch[];
    private Image finalAnim[];
    private Image stampAnims[][];
    private AudioClip buttClickNo;
    private AudioClip buttClickYes;
    private AudioClip finishClip;
    static int pSol[][][] = {
        {
            {
                108, 25
            }, {
                118, 25
            }, {
                128, 25
            }, {
                108, 50
            }, {
                78, 80
            }, {
                108, 70
            }, {
                148, 80
            }, {
                128, 65
            }, {
                108, 65
            }, {
                108, 65
            }, {
                148, 25
            }, {
                108, 40
            }, {
                78, 25
            }
        }, {
            {
                83, 40
            }, {
                118, 35
            }, {
                138, 40
            }, {
                78, 55
            }, {
                83, 60
            }, {
                88, 60
            }, {
                133, 60
            }, {
                138, 60
            }, {
                163, 55
            }, {
                78, 25
            }, {
                83, 40
            }, {
                88, 25
            }, {
                133, 25
            }, {
                138, 40
            }, {
                163, 25
            }, {
                83, 80
            }, {
                138, 80
            }
        }, {
            {
                93, 25
            }, {
                123, 30
            }, {
                143, 25
            }, {
                143, 40
            }, {
                78, 65
            }, {
                78, 75
            }, {
                103, 70
            }, {
                143, 75
            }, {
                78, 25
            }, {
                78, 40
            }, {
                103, 90
            }, {
                123, 70
            }, {
                143, 65
            }, {
                143, 75
            }
        }, {
            {
                78, 25
            }, {
                123, 25
            }, {
                143, 25
            }, {
                163, 25
            }, {
                123, 40
            }, {
                93, 60
            }, {
                153, 60
            }, {
                78, 90
            }, {
                148, 90
            }, {
                78, 30
            }, {
                148, 25
            }, {
                93, 50
            }, {
                153, 50
            }, {
                123, 75
            }, {
                143, 80
            }, {
                78, 105
            }, {
                123, 95
            }
        }
    };
    static int pLoc[][][] = {
        {
            {
                13, 11
            }, {
                23, 85
            }, {
                227, 58
            }, {
                180, 60
            }, {
                45, 53
            }, {
                206, 14
            }, {
                178, 14
            }, {
                24, 14
            }, {
                217, 68
            }, {
                39, 96
            }, {
                36, 13
            }, {
                178, 98
            }, {
                7, 98
            }, {
                24, 14
            }, {
                217, 68
            }, {
                39, 96
            }, {
                36, 13
            }, {
                168, 98
            }, {
                7, 98
            }
        }, {
            {
                47, 100
            }, {
                50, 39
            }, {
                174, 17
            }, {
                3, 83
            }, {
                40, 10
            }, {
                207, 13
            }, {
                12, 13
            }, {
                173, 69
            }, {
                238, 69
            }, {
                237, 3
            }, {
                223, 100
            }, {
                13, 82
            }, {
                200, 81
            }, {
                43, 57
            }, {
                3, 19
            }, {
                175, 43
            }, {
                44, 121
            }
        }, {
            {
                9, 12
            }, {
                194, 94
            }, {
                212, 51
            }, {
                5, 60
            }, {
                38, 60
            }, {
                181, 4
            }, {
                220, 94
            }, {
                2, 12
            }, {
                181, 51
            }, {
                6, 96
            }, {
                49, 12
            }, {
                25, 12
            }, {
                38, 100
            }, {
                212, 4
            }
        }, {
            {
                4, 16
            }, {
                24, 88
            }, {
                222, 105
            }, {
                236, 4
            }, {
                45, 53
            }, {
                182, 13
            }, {
                6, 98
            }, {
                24, 57
            }, {
                200, 74
            }, {
                46, 110
            }, {
                224, 74
            }, {
                189, 110
            }, {
                6, 50
            }, {
                24, 110
            }, {
                179, 71
            }, {
                182, 5
            }, {
                31, 114
            }
        }
    };

}
