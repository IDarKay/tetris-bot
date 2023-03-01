package fr.alice.tetrisbot.bot;

import fr.alice.tetrisbot.Game;
import fr.alice.tetrisbot.Plate;

import java.util.Arrays;

/**
 * File <b>Bot</b> located on fr.alice.tetrisbot.bot
 * Bot is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 27/02/2023 at 22:13
 */
public class Bot
{

    private final Game game;
    private final Plate plate;
    private Game.Piece currentPiece;
    private final boolean[][] simplifiedMap;
    private final byte[][] renderBoard;
    private final int[] heightMap;

    private final int width, height;


    private static void fill2DArray(boolean[][] array, boolean val) {
        for (boolean[] subArray : array) {
            Arrays.fill(subArray, val);
        }
    }

    private static void fill2DArray(byte[][] array, byte val) {
        for (byte[] subArray : array) {
            Arrays.fill(subArray, val);
        }
    }

    public Bot(Game game, Plate plate) {
        this.game = game;
        this.plate = plate;
        this.width = plate.getBoardWidth();
        this.height = plate.getBoardHeight();
        this.renderBoard = new byte[width][height];
        this.simplifiedMap = new boolean[width][height];
        this.heightMap = new int[width];
        this.game.setTickListener(this::onTick);
    }

    private void clearAll() {
        fill2DArray(this.renderBoard, (byte) 0);
        fill2DArray(this.simplifiedMap, false);
        Arrays.fill(this.heightMap, 0);
    }

    private void updateRender() {
        this.plate.setOverlay(this.renderBoard);
    }

    private void onTick(byte[][] board, Game.Piece piece) {
        this.clearAll();
        this.currentPiece = piece;
        for (int x = 0 ; x < width ; ++x) {
            for (int y = 0 ; y < height ; ++y) {
                this.simplifiedMap[x][y] = board[x][y] != 0 && (board[x][y] & 0b1000) == 0;
            }
        }



        this.updateHeightMap();
        this.updateHoleMap();
        for (int x = 0 ; x < width ; ++x) {
            for (int y = 0 ; y < heightMap[x] ; ++y) {
                this.renderBoard[x][y] = 1;
            }
        }
        this.updateRender();
    }

    private void updateHoleMap() {
        for (int x = 0 ; x < width ; ++x) {
            for (int y = 0 ; y < height ; ++y) {
                if (this.simplifiedMap[x][y]) {
                    for (int y2 = y + 1 ; y2 < height ; ++y2) {
                        if (!this.simplifiedMap[x][y2]) {
                            this.renderBoard[x][y2] = 2;
                        }
                    }
                }
            }
        }
    }

    private void updateHeightMap() {
        for (int x = 0 ; x < width ; ++x) {
            this.heightMap[x] = height;
            for (int y = 0; y < height ; ++y) {
                if (this.simplifiedMap[x][y]) {
                    this.heightMap[x] = y;
                    break;
                }
            }
        }
    }

}
