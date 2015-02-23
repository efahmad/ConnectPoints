package com.Firoozeh.ConnectPoints.view;

import com.Firoozeh.ConnectPoints.BluetoothActivity;
import com.Firoozeh.ConnectPoints.BluetoothService;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BluetoothGameView extends View
{
    private MyPoint[][] points;
    private List<MyPoint> attentionPoints;
    private List<MyLine> lines;
    private List<MyRect> rects;
    private MyPoint selectedPoint;
    private int screenW;
    private int screenH;
    private BluetoothService bluetoothService;
    private Context context;
    private boolean myTurn;
    private int myColor;
    private int remoteDeviceColor;
    private int myScore = 0;
    private int remoteDeviceScore = 0;
    private Paint paint;
    private Bitmap turnIcon;
    private Bitmap youWinGraphic;
    private Bitmap youLoseGraphic;
    private Bitmap paperGraphic;

    public BluetoothGameView(Context context, BluetoothService bluetoothService)
    {
        super(context);
        //
        this.bluetoothService = bluetoothService;
        myTurn = bluetoothService.isThisDeviceServer;
        if (bluetoothService.isThisDeviceServer)
        {
            myColor = Color.BLUE;
            remoteDeviceColor = Color.RED;
        }
        else
        {
            myColor = Color.RED;
            remoteDeviceColor = Color.BLUE;
        }
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
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Read messages
        ReadMessage();
        // Draw paper
        canvas.drawBitmap(paperGraphic, (screenW - paperGraphic.getWidth()) / 2,
                (screenH - paperGraphic.getHeight()) / 2, null);
        // Draw text
        paint.setColor(myColor);
        canvas.drawText("Me: " + myScore, 0.2222222222f * screenW, 0.1f * screenH, paint);
        paint.setColor(remoteDeviceColor);
        canvas.drawText(BluetoothActivity.mConnectedDeviceName + ": " + remoteDeviceScore,
                0.5555555555f * screenW, 0.1f * screenH, paint);
        // Draw turn icon
        if (myTurn)
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
            if (myScore > remoteDeviceScore)
                canvas.drawBitmap(youWinGraphic, screenW / 2 - youWinGraphic.getWidth() / 2,
                        screenH / 2 - youWinGraphic.getHeight() / 2, null);
            else if (myScore < remoteDeviceScore)
                canvas.drawBitmap(youLoseGraphic, screenW / 2 - youLoseGraphic.getWidth() / 2,
                        screenH / 2 - youLoseGraphic.getHeight() / 2, null);
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        // If it's not my turn, return
        if (!myTurn)
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
            MyLine tmpLine = new MyLine(selectedPoint, touchedPoint, myColor, screenH);
            lines.add(tmpLine);
            // Play draw sound
            MySoundPlayer.playSound(context, MySoundPlayer.drawLine);
            // Tell remote device to add line too
            sendMessage("ADD_LINE\n" + selectedPoint.indexI + "," + selectedPoint.indexJ +
                    "\n" + touchedPoint.indexI + "," + touchedPoint.indexJ);
            // Change turn
            myTurn = false;
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

    private void sendMessage(String message)
    {
        // Check that we're actually connected before trying anything
        if (bluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0)
        {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send);
        }
    }

    private void ReadMessage()
    {
        if (BluetoothActivity.message != "")
        {
            String str = BluetoothActivity.message.split("\n")[0];
            if (str.equals("ADD_LINE"))
            {
                int startI = Integer.parseInt(BluetoothActivity.message.split("\n")[1].split(",")[0]);
                int startJ = Integer.parseInt(BluetoothActivity.message.split("\n")[1].split(",")[1]);
                int stopI = Integer.parseInt(BluetoothActivity.message.split("\n")[2].split(",")[0]);
                int stopJ = Integer.parseInt(BluetoothActivity.message.split("\n")[2].split(",")[1]);
                MyLine tmpLine = new MyLine(points[startI][startJ], points[stopI][stopJ], remoteDeviceColor, screenH);
                lines.add(tmpLine);
                // Add attention points
                attentionPoints.add(new MyPoint(-1, -1, points[startI][startJ].circleX,
                        points[startI][startJ].circleY, 0.05f * screenH, Color.YELLOW));
                attentionPoints.add(new MyPoint(-1, -1, points[stopI][stopJ].circleX,
                        points[stopI][stopJ].circleY, 0.05f * screenH, Color.YELLOW));
                // Play draw sound
                MySoundPlayer.playSound(context, MySoundPlayer.drawLine);
                myTurn = true;
                checkRectCreation(tmpLine, true);
            }
            BluetoothActivity.message = "";
        }
    }

    private void checkRectCreation(MyLine newLine, boolean isRemotePlayer)
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
                                        if (isRemotePlayer)
                                        {
                                            myTurn = false;
                                            remoteDeviceScore++;
                                        }
                                        else
                                        {
                                            myTurn = true;
                                            myScore++;
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
                                        if (isRemotePlayer)
                                        {
                                            myTurn = false;
                                            remoteDeviceScore++;
                                        }
                                        else
                                        {
                                            myTurn = true;
                                            myScore++;
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
                                        if (isRemotePlayer)
                                        {
                                            myTurn = false;
                                            remoteDeviceScore++;
                                        }
                                        else
                                        {
                                            myTurn = true;
                                            myScore++;
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
                                        if (isRemotePlayer)
                                        {
                                            myTurn = false;
                                            remoteDeviceScore++;
                                        }
                                        else
                                        {
                                            myTurn = true;
                                            myScore++;
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
