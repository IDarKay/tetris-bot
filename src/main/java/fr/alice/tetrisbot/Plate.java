package fr.alice.tetrisbot;

import fr.alice.tetrisbot.interfaces.PlateRender;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * File <b>Plate</b> located on fr.alice.tetrisbot
 * Plate is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 25/02/2023 at 17:59
 */
public class Plate extends JPanel implements PlateRender
{
    public static final int CASE_SIZE = 16;

    private final int width, height, pwidth, pheight;

    private static final Color[] colors = new Color[]{
            new Color(0, 255, 255),
            new Color(255, 255, 0),
            new Color(170, 0, 255),
            new Color(255, 165, 0),
            new Color(0, 0, 255),
            new Color(255, 0, 0),
            new Color(0, 255, 0),
    };

    private static final Color[] overlayColors = new Color[] {
            new Color(255, 0, 0, 100),
            new Color(0, 255, 255, 100),
            new Color(255, 255, 0, 100),
            new Color(170, 0, 255, 100),
            new Color(255, 165, 0, 100),
            new Color(0, 0, 255, 100),
            new Color(123, 210, 132, 100),
            new Color(117, 79, 65, 100)
    };

    private byte [][] b;

    private byte [][] overlay;

    public Plate(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.pwidth = CASE_SIZE * (width + 1);
        this.pheight = CASE_SIZE * (height + 1);
        this.b = new byte[pwidth][pheight];
        this.overlay = new byte[pwidth][pheight];
        this.setSize(pwidth, pheight);
    }


    public int getBoardWidth()
    {
        return width;
    }

    public int getBoardHeight()
    {
        return height;
    }

    public void setOverlay(byte[][] overlay)
    {
        this.overlay = overlay;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (this.b == null) return;;
//        Graphics2D g2 = (Graphics2D) g;

        int dx = CASE_SIZE /2;
        int dy = CASE_SIZE /2;

        // draw bg
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.pheight, this.pheight);
        g.setColor(new Color(20, 20, 70));
        g.fillRect(dx, dy, this.pwidth - CASE_SIZE, this.pheight  - CASE_SIZE);

        //  draw piece

        for (int x = 0 ; x  < this.width ; ++x)
        {
            for (int y = 0; y < this.height; ++y)
            {
                if (b[x][y] != 0)
                {
                    g.setColor(colors[(b[x][y] & 0b111) - 1]);
                    g.fillRect(dx + x * CASE_SIZE, dy + y * CASE_SIZE, CASE_SIZE, CASE_SIZE);
                }
                if (overlay[x][y] != 0)
                {
                    g.setColor(overlayColors[overlay[x][y] - 1]);
                    g.fillRect(dx + x * CASE_SIZE, dy + y * CASE_SIZE, CASE_SIZE, CASE_SIZE);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
    }

    @Override
    public void render(byte[][] board)
    {
        this.b = board;
        repaint();
    }
}
