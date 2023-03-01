package fr.alice.tetrisbot;

import fr.alice.tetrisbot.interfaces.PlateRender;
import fr.alice.tetrisbot.utils.RandomArray;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * File <b>Game</b> located on fr.alice.tetrisbot
 * Game is a part of tetris-bot.
 * <p>
 * Copyright (c) 2023 tetris-bot.
 * <p>
 *
 * @author Alois. B. (IDarKay),
 * Created the 26/02/2023 at 00:48
 */
public class Game implements KeyListener
{
    private static final int TICK_RATE = 60;
    private static final float DEFAULT_DOWN_GRAVITY = 0.1f;
    private final RandomArray<PIECE_TYPE> pieces;
    private PlateRender plate;
    private PlateRender preview;
    private Timer tickTimer;
    private Piece currentPiece;

    private BiConsumer<byte[][], Piece> tickListener;

    private boolean end = false;

    private byte[][] board = new byte[10][22];

    private boolean pause = false;

    public Game(PlateRender plate, PlateRender preview) {
        this.plate = plate;
        this.preview = preview;
        this.tickTimer = new javax.swing.Timer((int) (1000/TICK_RATE / DEFAULT_DOWN_GRAVITY), this::tick);
        this.pieces = new RandomArray<>(PIECE_TYPE.values());
    }

    public void setTickListener(BiConsumer<byte[][], Piece> tickListener)
    {
        this.tickListener = tickListener;
    }

    private void clearFullLines() {
        for (int y = 0 ; y < 22 ; ++y) {
            if (checkLastLine(y)) {
                clearLine(y);
            }
        }
    }
    private void clearLine(int lineY) {
        for (int x = 0 ; x < 10 ; ++x) {
            for (int y = lineY ; y >= 0 ; --y) {
                if (y == 0) this.board[x][y] = 0;
                else this.board[x][y] = this.board[x][y - 1];
            }
        }
    }

    private boolean checkLastLine(int lineY) {
        for (int x = 0 ; x < 10 ; ++x) {
            if (this.board[x][lineY] == 0 || (this.board[x][lineY] & 0b1000) != 0){
                return false;
            }
        }
        return true;
    }

    private void clearCurrentPiecesOfGrid() {
        for (int x = 0 ; x < 10 ; ++x) {
            for (int y = 0 ; y < 22 ; ++y) {
                if ((this.board[x][y] & 0b1000) != 0) {
                    this.board[x][y] = 0;
                }
            }
        }
    }

    private void updatePreview() {
        byte[][] b = new byte[4][4];
        pieces.get().addToGrid(0, 0, 0, b);
        preview.render(b);
    }

    private void newPiece() {
        this.currentPiece = new Piece(3, 0, pieces.pop(), 0);
        if (this.currentPiece.checkCollision(this.board)) {
            this.end = true;
            this.currentPiece = null;
            System.out.println("end");
            return;
        }
        this.clearFullLines();
        this.updatePreview();
    }

    private void fixPiece() {
        if (this.currentPiece != null) {
            this.currentPiece.type.fixToGrid(this.currentPiece.x, this.currentPiece.y, this.currentPiece.rot, this.board);
            this.currentPiece = null;
        }
    }

    private void drawPiece() {
        if (this.currentPiece != null) {
            this.clearCurrentPiecesOfGrid();
            this.currentPiece.draw(this.board);
        }
    }

    private void tick(ActionEvent e)
    {
        if (end || pause) return;
        if (this.currentPiece != null) {
            if (this.currentPiece.checkCollision(this.board, DIRECTION.DOWN)) {
                this.fixPiece();
                this.newPiece();
            } else {
                this.currentPiece.move(DIRECTION.DOWN);
            }
        } else  {
            this.newPiece();
        }

        this.drawPiece();
        tickListener.accept(this.board, this.currentPiece);
        this.plate.render(this.board);
    }

    public void start() {
        this.tickTimer.start();
    }


    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    private void tryRotate(boolean right) {
        if (currentPiece == null) return;
        int newRot = (this.currentPiece.rot + (right? -1 : 1));
        if (newRot > 3) newRot = 0;
        else if (newRot < 0) newRot = 3;
        if (!this.currentPiece.checkCollision(this.board, newRot)) {
            this.currentPiece.rot = newRot;
            this.drawPiece();
        }
    }

    private void tryMove(DIRECTION direction) {
        if (currentPiece == null) return;
        if (!this.currentPiece.checkCollision(this.board, direction)) {
            this.currentPiece.move(direction);
            this.drawPiece();
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == 37) {
            this.tryMove(DIRECTION.LEFT);
        } else if (e.getKeyCode() == 39) {
            this.tryMove(DIRECTION.RIGHT);
        } else if (e.getKeyCode() == 32) {
            this.tryMove(DIRECTION.DOWN);
        } else if (e.getKeyCode() == 38) {
            this.tryRotate(false);
        } else if (e.getKeyCode() == 40) {
            this.tryRotate(true);
        } else if (e.getKeyCode() == 27) {
            this.pause = !this.pause;
        }
        this.drawPiece();
        this.plate.render(this.board);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    private enum DIRECTION  {
        UP,
        RIGHT,
        DOWN,
        LEFT,

        NONE,

    }

    public enum PIECE_TYPE {
        I((byte) 0b001, new char[] {
                0b0000_1111_0000_0000,
                0b0010_0010_0010_0010,
                0b0000_0000_1111_0000,
                0b0100_0100_0100_0100
        }),
        O((byte) 0b010, new char[] {
                0b0000_0110_0110_0000,
                0b0000_0110_0110_0000,
                0b0000_0110_0110_0000,
                0b0000_0110_0110_0000
        }),
        T((byte) 0b011, new char[] {
                0b0100_1110_0000_0000,
                0b0100_0110_0100_0000,
                0b0000_1110_0100_0000,
                0b0100_1100_0100_0000,
        }),
        S((byte) 0b100, new char[] {
                0b0110_1100_0000_0000,
                0b0100_0110_0010_0000,
                0b0000_0110_1100_0000,
                0b1000_1100_0100_0000
        }),
        Z((byte) 0b101, new char[] {
                0b1100_0110_0000_0000,
                0b0010_0110_0100_0000,
                0b0000_1100_0110_0000,
                0b0100_1100_1000_0000
        }),
        J((byte) 0b110, new char[] {
                0b1000_1110_0000_0000,
                0b0110_0100_0100_0000,
                0b0000_1110_0010_0000,
                0b0100_0100_1100_0000
        }),
        L((byte) 0b111, new char[] {
                0b0010_1110_0000_0000,
                0b0100_0100_0110_0000,
                0b0000_1110_1000_0000,
                0b1100_0100_0100_0000
        });

        public byte id;
        private char[] shapes;

        PIECE_TYPE(byte id, char[] shapes) {
            this.shapes = shapes;
            this.id = id;
        }

        public boolean checkCollision(int bx, int by, int rot, byte[][] grid) {
            for (int x = 0 ; x < 4 ; ++x) {
                for (int y = 0 ; y < 4 ; y++) {
                    if ((shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                        if (by + y < 0 || by + y > 21 || bx + x < 0 || bx + x > 9) return true;
                        if (grid[bx + x][by + y] != 0 && (grid[bx + x][by + y] & 0b1000) == 0) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public void addToGrid(int bx, int by, int rot, byte[][] grid) {
            if (rot < 0 || 3 < rot) rot %= 4;
            for (int x = 0 ; x < 4 ; ++x) {
                for (int y = 0 ; y < 4 ; y++) {
                    if ((shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                        grid[bx + x][by + y] = (byte) (this.id | 0b1000);
                    }
                }
            }
        }

        public void fixToGrid(int bx, int by, int rot, byte[][] grid) {
            if (rot < 0 || rot > 3) return;
            for (int x = 0 ; x < 4 ; ++x) {
                for (int y = 0 ; y < 4 ; y++) {
                    if ((shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                        grid[bx + x][by + y] = this.id;
                    }
                }
            }
        }
    }

    public static class Piece {

        private int x;
        private int y;
        private PIECE_TYPE type;
        private int rot;

        public Piece(int x, int y, PIECE_TYPE type, int rot) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.rot = rot;
        }

        public boolean checkCollision(byte[][] grid) {
            return this.checkCollision(grid, DIRECTION.NONE, this.rot);
        }

        public boolean checkCollision(byte[][] grid, int rot) {
            return this.checkCollision(grid, DIRECTION.NONE, rot);
        }

        public boolean checkCollision(byte[][] grid, DIRECTION direction) {
            return this.checkCollision(grid, direction, this.rot);
        }

        public boolean checkCollision(byte[][] grid, DIRECTION direction, int rot) {
            return switch (direction) {
                case UP -> this.type.checkCollision(this.x, this.y - 1, rot, grid);
                case DOWN -> this.type.checkCollision(this.x, this.y + 1, rot, grid);
                case RIGHT -> this.type.checkCollision(this.x + 1, this.y, rot, grid);
                case LEFT -> this.type.checkCollision(this.x - 1, this.y, rot, grid);
                case NONE -> this.type.checkCollision(this.x, this.y, rot, grid);
            };
        }

        public void draw(byte[][] grid) {
            this.type.addToGrid(this.x, this.y, this.rot, grid);
        }

        public void move(DIRECTION direction) {
            switch (direction) {
                case UP -> this.y -= 1;
                case DOWN -> this.y += 1;
                case RIGHT -> this.x += 1;
                case LEFT -> this.x -= 1;
            }
        }
    }
}
