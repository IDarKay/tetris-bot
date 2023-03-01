package fr.alice.tetrisbot;

import fr.alice.tetrisbot.bot.Bot;

/**
 * File <b>Main</b> located on fr.alice.tetrisbot
 * Main is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 25/02/2023 at 17:51
 */
public class Main
{
    public static void main(String[] args)
    {
        Window win = new Window();
        Game game = new Game(win.plate, win.preview);
        Bot bot = new Bot(game, win.plate);
        win.addKeyListener(game);
        win.setVisible(true);
        game.start();
    }
}
