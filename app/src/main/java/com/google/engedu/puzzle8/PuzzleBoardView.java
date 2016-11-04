package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            for (int i=0;i<NUM_SHUFFLE_STEPS;i++){
                ArrayList<PuzzleBoard> shuffle = puzzleBoard.neighbours();
                puzzleBoard = shuffle.get(random.nextInt(shuffle.size()));

            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }



    public void solve() {


        Comparator<PuzzleBoard> comparator = new BoardComparator();

        PriorityQueue<PuzzleBoard> queue = new PriorityQueue<>(10, comparator);

        puzzleBoard.setPreviousToNull();

        puzzleBoard.setSteps(0);

        queue.add(puzzleBoard);

        int i = 0;
        while (!queue.isEmpty()) {
            PuzzleBoard lowestPriority = queue.poll();

            if (!lowestPriority.resolved()) {
               for (PuzzleBoard pb : lowestPriority.neighbours()) {
                   if (!pb.equals(lowestPriority.getPreviousBoard()))
                        queue.add(pb);
                }
            }
            else {

                ArrayList<PuzzleBoard> sequence = new ArrayList<>();
                sequence.add(lowestPriority);
                while (lowestPriority.getPreviousBoard() != null) {
                    lowestPriority = lowestPriority.getPreviousBoard();
                    sequence.add(lowestPriority);
                }


                sequence.remove(sequence.size() - 1);
                Collections.reverse(sequence);
                animation = sequence;
                invalidate();

                return;
            }
        }
    }


    class BoardComparator implements Comparator<PuzzleBoard> {

        @Override
        public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {

            return lhs.priority() - rhs.priority();

        }
    }

}

