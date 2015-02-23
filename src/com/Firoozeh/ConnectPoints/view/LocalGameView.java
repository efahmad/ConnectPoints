package com.Firoozeh.ConnectPoints.view;

import com.Firoozeh.ConnectPoints.GameObject.AndroidPlayer;
import com.Firoozeh.ConnectPoints.GameObject.MyLine;
import com.Firoozeh.ConnectPoints.GameObject.MyRect;
import com.Firoozeh.ConnectPoints.GameObject.MySoundPlayer;
import com.Firoozeh.ConnectPoints.R;
import com.Firoozeh.ConnectPoints.GameObject.MyPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class LocalGameView extends View
{
    private MyPoint[][] points;
    private List<MyPoint> attentionPoints;
    private List<MyLine> lines;
    private List<MyRect> rects;
    private MyPoint selectedPoint;
    private int screenW;
    private int screenH;
    private Context context;
    private boolean humanTurn;
    private int humanColor;
    private int humanScore = 0;
    private Paint paint;
    private Bitmap turnIcon;
    private Bitmap youWinGraphic;
    private Bitmap youLoseGraphic;
    private Bitmap paperGraphic;
    private AndroidPlayer android;
    private boolean isGameOver = false;

    public LocalGameView(Context context)
    {
        super(context);
        //
        humanTurn = true;
        humanColor = Color.BLUE;
        this.context = context;
        this.points = new MyPoint[6][8];
        attentionPoints = new ArrayList<MyPoint>();
        this.lines = new ArrayList<MyLine>();
        this.rects = new ArrayList<MyRect>();
        paint = new Paint();
        paint.setAntiAlias(true);
        turnIcon = BitmapFactory.decodeResource(getResources(), R.drawable.turn);
        youWinGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.you_win);
        youLoseGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.you_lose);
        paperGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.paper);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
        paint.setTextSize(0.0625f * screenH);
        // Add points
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                points[i][j] = new MyPoint(i, j, (j + 1) * 0.1111111111f * screenW,
                        (i + 2) * 0.125f * screenH, 0.03f * screenH, Color.BLACK);
            }
        }
        // Instantiate android player
        android = new AndroidPlayer(lines, points, screenH, Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Draw paper
        canvas.drawBitmap(paperGraphic, (screenW - paperGraphic.getWidth()) / 2,
                (screenH - paperGraphic.getHeight()) / 2, null);
        // Draw text
        paint.setColor(humanColor);
        canvas.drawText("Me: " + humanScore, 0.2222222222f * screenW, 0.1f * screenH, paint);
        paint.setColor(android.color);
        canvas.drawText("Android" + ": " + android.score,
                0.5555555555f * screenW, 0.1f * screenH, paint);
        // Draw turn icon
        if (humanTurn)
        {
            canvas.drawBitmap(turnIcon, (0.2f * screenW) - turnIcon.getWidth(),
                    0.04f * screenH, null);
        }
        else
        {
            canvas.drawBitmap(turnIcon, (0.5333333333f * screenW) - turnIcon.getWidth(),
                    0.04f * screenH, null);
        }
        // Draw rectangles
        for (MyRect rect : rects)
        {
            rect.draw(canvas);
        }
        // Draw attention points
        for (MyPoint point : attentionPoints)
        {
            point.draw(canvas);
        }
        // Draw lines
        for(MyLine line : lines)
        {
            line.draw(canvas);
        }
        // Draw points
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                points[i][j].draw(canvas);
            }
        }
        // Draw Winner showing graphics
        if (rects.size() == 35)
        {
            // the game has been finished
            isGameOver = true;
            if (humanScore > android.score)
                canvas.drawBitmap(youWinGraphic, screenW / 2 - youWinGraphic.getWidth() / 2,
                        screenH / 2 - youWinGraphic.getHeight() / 2, null);
            else if (humanScore < android.score)
                canvas.drawBitmap(youLoseGraphic, screenW / 2 - youLoseGraphic.getWidth() / 2,
                        screenH / 2 - youLoseGraphic.getHeight() / 2, null);
        }

        if (!humanTurn && !isGameOver)
        {
            if (System.currentTimeMillis() - android.thinkingTime > 1000)
            {
                MyLine tmpLine = android.move();
                // Play draw sound
                MySoundPlayer.playSound(context, MySoundPlayer.drawLine);
                // Add attention points
                attentionPoints.add(new MyPoint(-1, -1,
                        tmpLine.startPoint.circleX, tmpLine.startPoint.circleY,
                        0.05f * screenH, Color.YELLOW));
                attentionPoints.add(new MyPoint(-1, -1,
                        tmpLine.stopPoint.circleX, tmpLine.stopPoint.circleY,
                        0.05f * screenH, Color.YELLOW));
                humanTurn = true;
                checkRectCreation(tmpLine, true);
                android.thinkingTime = System.currentTimeMillis();
            }
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        // If it's not my turn, return
        if (!humanTurn)
        {
            return false;
        }
        int eventaction = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch(eventaction)
        {
            case MotionEvent.ACTION_DOWN:
                MyPoint touchedPoint = getTouchedPoint(x, y);
                if (touchedPoint != null)
                {
                    touchPoint(touchedPoint);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    private void touchPoint(MyPoint touchedPoint)
    {
        if (touchedPoint.getColor() == Color.YELLOW)
        {
            // Add the Line
            MyLine tmpLine = new MyLine(selectedPoint, touchedPoint, humanColor, screenH);
            lines.add(tmpLine);
            // Remove this line from possible lines
            android.removeFromPossibleLines(tmpLine);
            // Play draw sound
            MySoundPlayer.playSound(context, MySoundPlayer.drawLine);
            // Change turn
            humanTurn = false;
            android.thinkingTime = System.currentTimeMillis();
            checkRectCreation(tmpLine, false);
            // Paint all points with black
            for (int i = 0; i < 6; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    points[i][j].setColor(Color.BLACK);
                }
            }
            selectedPoint = null;
            return;
        }
        // Paint all points with black
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                points[i][j].setColor(Color.BLACK);
            }
        }
        // Clear attention points
        attentionPoints.clear();
        // Paint touched point with Green
        touchedPoint.setColor(Color.GREEN);
        // Paint choices points with Yellow
        for(MyPoint adj : getChoices(touchedPoint))
        {
            adj.setColor(Color.YELLOW);
        }
        selectedPoint = touchedPoint;
    }

    private MyPoint getTouchedPoint(int x, int y)
    {
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if(points[i][j].isTouched(x, y, screenH))
                {
                    return points[i][j];
                }
            }
        }
        return null;
    }

    private List<MyPoint> getChoices(MyPoint curPoint)
    {
        List<MyPoint> adjPoints = curPoint.getAdjPoints(points);
        List<MyPoint> choicePoints = new ArrayList<MyPoint>();
        for (MyPoint adj : adjPoints)
        {
            // Check if there is a line between adjacent point and current point
            if (!isLineBetween(adj, curPoint))
            {
                // There is no line between adjacent point and current point
                choicePoints.add(adj);
            }
        }
        return choicePoints;
    }

    private boolean isLineBetween(MyPoint point1, MyPoint point2)
    {
        for (MyLine line : lines)
        {
            if (point1.isOnLine(line) && point2.isOnLine(line))
            {
                return true;
            }
        }
        return false;
    }

    private void checkRectCreation(MyLine newLine, boolean isAndroid)
    {
        // newLine is vertical or horizontal ?
        if (newLine.startPoint.indexI != newLine.stopPoint.indexI)
        {
            // newLine is vertical
            // Get indices of top point
            int i = Math.min(newLine.startPoint.indexI, newLine.stopPoint.indexI);
            int j = newLine.startPoint.indexJ;

            for (MyLine line1 : lines)
            {
                // Check right
                if (j < 7)
                {
                    if (points[i][j].isOnLine(line1) && points[i][j+1].isOnLine(line1))
                    {
                        for (MyLine line2 : lines)
                        {
                            if (points[i][j+1].isOnLine(line2) && points[i+1][j+1].isOnLine(line2))
                            {
                                for (MyLine line3 : lines)
                                {
                                    if (points[i+1][j+1].isOnLine(line3) && points[i+1][j].isOnLine(line3))
                                    {
                                        rects.add(new MyRect(points[i][j], points[i+1][j+1], newLine.getColor()));
                                        // Play drawRect sound
                                        MySoundPlayer.playSound(context, MySoundPlayer.drawRect);
                                        if (isAndroid)
                                        {
                                            humanTurn = false;
                                            android.score++;
                                        }
                                        else
                                        {
                                            humanTurn = true;
                                            humanScore++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Check left
                if (j > 0)
                {
                    if (points[i][j].isOnLine(line1) && points[i][j-1].isOnLine(line1))
                    {
                        for (MyLine line2 : lines)
                        {
                            if (points[i][j-1].isOnLine(line2) && points[i+1][j-1].isOnLine(line2))
                            {
                                for (MyLine line3 : lines)
                                {
                                    if (points[i+1][j-1].isOnLine(line3) && points[i+1][j].isOnLine(line3))
                                    {
                                        rects.add(new MyRect(points[i][j-1], points[i+1][j], newLine.getColor()));
                                        // Play drawRect sound
                                        MySoundPlayer.playSound(context, MySoundPlayer.drawRect);
                                        if (isAndroid)
                                        {
                                            humanTurn = false;
                                            android.score++;
                                        }
                                        else
                                        {
                                            humanTurn = true;
                                            humanScore++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            // newLine is horizontal
            // Get indices of left point
            int i = newLine.startPoint.indexI;
            int j = Math.min(newLine.startPoint.indexJ, newLine.stopPoint.indexJ);

            for (MyLine line1 : lines)
            {
                // Check top
                if (i > 0)
                {
                    if (points[i][j].isOnLine(line1) && points[i-1][j].isOnLine(line1))
                    {
                        for (MyLine line2 : lines)
                        {
                            if (points[i-1][j].isOnLine(line2) && points[i-1][j+1].isOnLine(line2))
                            {
                                for (MyLine line3 : lines)
                                {
                                    if (points[i-1][j+1].isOnLine(line3) && points[i][j+1].isOnLine(line3))
                                    {
                                        rects.add(new MyRect(points[i-1][j], points[i][j+1], newLine.getColor()));
                                        // Play drawRect sound
                                        MySoundPlayer.playSound(context, MySoundPlayer.drawRect);
                                        if (isAndroid)
                                        {
                                            humanTurn = false;
                                            android.score++;
                                        }
                                        else
                                        {
                                            humanTurn = true;
                                            humanScore++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Check bottom
                if (i < 5)
                {
                    if (points[i][j].isOnLine(line1) && points[i+1][j].isOnLine(line1))
                    {
                        for (MyLine line2 : lines)
                        {
                            if (points[i+1][j].isOnLine(line2) && points[i+1][j+1].isOnLine(line2))
                            {
                                for (MyLine line3 : lines)
                                {
                                    if (points[i+1][j+1].isOnLine(line3) && points[i][j+1].isOnLine(line3))
                                    {
                                        rects.add(new MyRect(points[i][j], points[i+1][j+1], newLine.getColor()));
                                        // Play drawRect sound
                                        MySoundPlayer.playSound(context, MySoundPlayer.drawRect);
                                        if (isAndroid)
                                        {
                                            humanTurn = false;
                                            android.score++;
                                        }
                                        else
                                        {
                                            humanTurn = true;
                                            humanScore++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
