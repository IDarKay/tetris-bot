package fr.alice.tetrisbot.bot;

import fr.alice.tetrisbot.Game;
import fr.alice.tetrisbot.Plate;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.stream.Stream;

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
public class Bot implements KeyListener
{

    private final Game game;
    private final Plate plate;
    private Game.Piece currentPiece;
    private final boolean[][] simplifiedMap;
    private final byte[][] renderBoard;
    private final int[] heightMap;
    private final boolean[][] holeMap;

    private final int width, height;

    public int lastPositionRender = 0;
    private List<int[]> positions;

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
        this.holeMap = new boolean[width][height];
        this.heightMap = new int[width];
        this.positions = Collections.emptyList();
        this.game.setTickListener(this::onTick);
    }

    private void clearAll() {
//        fill2DArray(this.renderBoard, (byte) 0);
        fill2DArray(this.simplifiedMap, false);
        fill2DArray(this.holeMap, false);
        Arrays.fill(this.heightMap, 0);
    }

    private void updateRender() {
        this.plate.setOverlay(this.renderBoard);
    }

    private void drawNextPosition() {
        fill2DArray(this.renderBoard, (byte) 0);
//        if (positions.size() <= lastPositionRender) lastPositionRender = 0;
//        if (positions.size() > 0) {
//            int[] ints = positions.get(lastPositionRender);
//            this.drawPiece(currentPiece.getType(), ints[1], ints[2], ints[0], (byte) (ints[3] + 1));
//            lastPositionRender++;
//        }
        int[] ints = getBestPosition();
        if (ints.length > 0) {
            this.drawPiece(currentPiece.getType(), ints[1], ints[2], ints[0], (byte) (ints[3] + 1));
        }
        this.updateRender();
        this.plate.repaint();
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
        this.positions = getListOfPosition();

        this.updateRender();
    }

    private void updateHoleMap() {
        for (int x = 0 ; x < width ; ++x) {
            for (int y = 0 ; y < height ; ++y) {
                if (this.simplifiedMap[x][y]) {
                    for (int y2 = y + 1 ; y2 < height ; ++y2) {
                        if (!this.simplifiedMap[x][y2]) {
                            this.holeMap[x][y2] = true;
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


    private boolean checkIsCollide(Game.PIECE_TYPE piece, int bx, int by, int rot) {
        return checkIsCollide(this.simplifiedMap, piece, bx, by, rot);
    }

    private boolean checkIsCollide(boolean[][] tables, Game.PIECE_TYPE piece, int bx, int by, int rot) {
        for (int x = 0; x < 4 ; ++x) {
            for (int y = 0; y < 4; ++y) {
                if ((piece.shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                    if (by + y < 0 || by + y >= height || bx + x < 0 || bx + x >= width) return true;
                    if (tables[bx + x][by + y]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void drawPiece(Game.PIECE_TYPE piece, int bx, int by, int rot, byte c) {
        for (int x = 0; x < 4 ; ++x) {
            for (int y = 0; y < 4; ++y) {
                if ((piece.shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                    if (by + y < 0 || by + y >= height || bx + x < 0 || bx + x >= height) continue;
                    renderBoard[bx + x][by +y] = c;
                }
            }
        }
    }

    private List<int[]> getListOfPosition() {

        List<int[]> positions = new ArrayList<>();

        for (int r = 0 ; r < 4 ; r++) {
            for (int x = - 4; x < 10 ; x++) {
                int y = 0;
                while (!checkIsCollide(currentPiece.getType(), x, y, r)) y++;
                if (y - 1 > 0) {
                    positions.add(new int[] {r, x, y - 1, 0, getScoreOfPosition(currentPiece.getType(), x, y-1, r)});
                }
            }
            // O piece have same shaped for all rotation so useless to turn it
            if (currentPiece.getType() == Game.PIECE_TYPE.O) break;
        }

        for (int r = 0 ; r < 4 ; ++r) {
            for (int x = - 4; x < 10 ; ++x) {
                int y = height + 3;
                boolean last = false;

                while (y >= 0) {
                    if (!checkIsCollide(currentPiece.getType(), x, y, r)) {
                        if (last) {
                            if (checkIsCollide(this.holeMap, currentPiece.getType(), x, y, r)) {
                                positions.add(new int[] {r, x, y, 1, getScoreOfPosition(currentPiece.getType(), x, y, r)});
                            }
                        }
                        last = false;
                    } else {
                        last = true;
                    }
                    y--;
                }
            }
            // O piece have same shaped for all rotation so useless to turn it
            if (currentPiece.getType() == Game.PIECE_TYPE.O) break;
        }

        return positions;
    }

    private boolean isOnSky(Game.PIECE_TYPE pieceType, int x ,int y, int rot) {
        for (int[] position : this.positions) {
            if (position[3] == 0 && position[0] == rot && position[1] == x && position[2] >= y) return true;
        }
        return false;
    }

    /**
     *
     * @param pieceType
     * @param x
     * @param y
     * @param rot
     * @param prevD 1 = left ; 2 = right else ignore
     * @return
     */
    private boolean canGoToSky(Game.PIECE_TYPE pieceType, int x, int y, int rot, int prevD) {
        if (this.isOnSky(pieceType, x, y, rot)) return true;
        for (int r = 0 ; r < 3 ; ++r) {
            int newRot = switch (r) {
                case 0 -> rot;
                case 1 -> (rot - 1);
                case 2 -> (rot + 1) % 4;
                default -> throw new IllegalStateException("Unexpected value: " + r);
            };
            if (newRot < 0) newRot = 3;
            for (int d = 0 ; d < 3 ; ++d) {
                if (prevD == d) continue;
                int newY = d == 0 ? y - 1 : y;
                int newX = d == 1 ? x - 1 : d == 2 ? x + 1 : x;
                if (!checkIsCollide(pieceType, newX, newY, newRot)) {
                    if (canGoToSky(pieceType, newX, newY, newRot, d == 1 ? 2 : d==2 ? 1: -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addToSimplifiedMap(boolean[][] tables, Game.PIECE_TYPE type, int bx, int by, int rot) {
        for (int x = 0; x < 4 ; ++x) {
            for (int y = 0; y < 4; ++y) {
                if ((type.shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                    if (by + y < 0 || by + y >= height || bx + x < 0 || bx + x >= width || tables[bx + x][by + y]) throw new IllegalStateException("invalid pose");
                    tables[bx + x][by + y] = true;
                }
            }
        }
    }

    private void removeToSimplifiedMap(boolean[][] tables, Game.PIECE_TYPE type, int bx, int by, int rot) {
        for (int x = 0; x < 4 ; ++x) {
            for (int y = 0; y < 4; ++y) {
                if ((type.shapes[rot] & (0b1 << (y * 4 + x))) != 0) {
                    if (by + y < 0 || by + y >= height || bx + x < 0 || bx + x >= width) throw new IllegalStateException("invalid pose");
                    tables[bx + x][by + y] = false;
                }
            }
        }
    }

    private int getScoreOfPosition(Game.PIECE_TYPE type, int bx, int by, int rot) {
        if (type != null) addToSimplifiedMap(this.simplifiedMap, type, bx, by, rot);

        int score = 0;
        for (int y = height -1 ; y >= 0 ; --y) {
            for (int x = 0; x < width; ++x) {
                if (this.simplifiedMap[x][y]) {
                    score += height - y;
                }
            }
        }

        if (type != null) removeToSimplifiedMap(this.simplifiedMap, type, bx, by, rot);
        return score;
    }

    private int[] getBestPosition() {
        // filter(ints -> ints[3] == 0 || canGoToSky(currentPiece.getType(), ints[1], ints[2], ints[0], -1))

        List<int[]> sorted = this.positions.stream().sorted(Comparator.comparingInt(ints -> ints[4])).toList();
        sorted.forEach(ints -> System.out.println(ints[4]));
        int index = 0;
        while (sorted.size() > index){
            int[] vals = sorted.get(index++);
            if (canGoToSky(currentPiece.getType(), vals[1], vals[2], vals[0], -1))  {
                return vals;
            }
        }
        return new int[] {};
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 78) {
                this.drawNextPosition();
            }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
