package com.Firoozeh.ConnectPoints.GameObject;

import android.graphics.Canvas;
import android.graphics.Paint;

public class MyRect
{
    private Paint paint;
    private MyPoint leftTopPoint;
    private MyPoint rightBottomPoint;

    public MyRect(MyPoint leftTopPoint, MyPoint rightBottomPoint, int color)
    {
        this.leftTopPoint = leftTopPoint;
        this.rightBottomPoint = rightBottomPoint;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawRect(leftTopPoint.circleX, leftTopPoint.circleY,
                rightBottomPoint.circleX, rightBottomPoint.circleY, paint);
    }
}
