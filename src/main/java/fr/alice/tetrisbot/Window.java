package fr.alice.tetrisbot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;

/**
 * File <b>Window</b> located on fr.alice.tetrisbot
 * Window is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 25/02/2023 at 17:52
 */
public class Window extends JFrame
{

    private final JPanel content = new JPanel();
    public final Plate plate = new Plate(10, 22);
    public final Plate preview = new Plate(4, 4);

    /**
     * init windows
     */
    public Window()
    {
        this.setupWindow();
    }


    private void setupWindow()
    {
        this.setTitle("Tetris-bot");
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        content.setLayout(null);
        content.add(plate);
        content.add(preview);
        plate.setBounds(0, 0, plate.getWidth(), plate.getHeight());
        preview.setBounds(176, 0, preview.getWidth(), preview.getHeight());
        this.setContentPane(content);
    }
}
