package com.Firoozeh.ConnectPoints.GameObject;

import android.graphics.Canvas;
import android.graphics.Paint;

public class MyLine
{
    public MyPoint startPoint;
    public MyPoint stopPoint;
    private Paint paint;

    public MyLine(MyPoint startPoint, MyPoint stopPoint, int color, int screenH)
    {
        this.startPoint = startPoint;
        this.stopPoint = stopPoint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(0.01f * screenH);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawLine(startPoint.circleX, startPoint.circleY,
                stopPoint.circleX, stopPoint.circleY, paint);
    }

    public int getColor()
    {
        return paint.getColor();
    }

    public boolean equals(MyLine line)
    {
        if (startPoint.isOnLine(line) && stopPoint.isOnLine(line))
            return true;
        else
            return false;
    }
}
