package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles = new ArrayList<>();
    private  int steps =0;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        int numTile = 0;
        Bitmap sqBitmap = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        for (int i=0;i<NUM_TILES;i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                tiles.add(new PuzzleTile(bitmap.createBitmap(sqBitmap, j*parentWidth / NUM_TILES,i*parentWidth/NUM_TILES,parentWidth/NUM_TILES,parentWidth/NUM_TILES),numTile));
                 numTile++;
            }
        }
        tiles.set((NUM_TILES* NUM_TILES)-1,null);
        steps=0;

    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();

        steps = otherBoard.steps+1;
        previousBoard= otherBoard;

    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> vaildBoard = new ArrayList<>();
        int index =0;
        for (int i=0; i<tiles.size();i++) {
            if (tiles.get(i) == null) {
                index = i;
                break;

            }
        }
        for (int i=0;i<4;i++){
            int valid = XYtoIndex(NEIGHBOUR_COORDS[i][0],NEIGHBOUR_COORDS[i][1])+index;
            if (valid>=0 && valid<=8){
                PuzzleBoard copy = new PuzzleBoard(this);
                copy.swapTiles(index,valid);
                vaildBoard.add(copy);
            }
        }
        return vaildBoard;
    }

    public int priority() {
        int manhattanValue = 0;

        int originalX;
        int originalY;

        int presentX;
        int presentY;

        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {

            presentX = i / NUM_TILES;
            presentY = i % NUM_TILES;

            if (tiles.get(i) != null) {
                originalX = tiles.get(i).getNumber() / NUM_TILES;
                originalY = tiles.get(i).getNumber() % NUM_TILES;
            } else {
                originalX = 8 / NUM_TILES;
                originalY = 8 % NUM_TILES;
            }
            manhattanValue += Math.abs(originalX - presentX);
            manhattanValue += Math.abs(originalY - presentY);
        }



        manhattanValue += steps;

        return manhattanValue;

    }

    public void setSteps (int x) {
        steps = x;
    }

    public void setPreviousToNull () {
        previousBoard = null;
    }

    public PuzzleBoard getPreviousBoard () {
        return previousBoard;
    }

}
