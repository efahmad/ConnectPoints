package com.Firoozeh.ConnectPoints.GameObject;

import android.graphics.Paint;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class MyPoint 
{
	// Fields 
	private Paint paint;
	public Float circleX;
	public Float circleY;
	public float radius;
    public int indexI;
    public int indexJ;
    
	// Methods
	public MyPoint(int indexI, int indexJ, float circleX, float circleY, float radius, int color)
	{
        this.indexI = indexI;
        this.indexJ = indexJ;
		this.circleX = circleX;
		this.circleY = circleY;
		this.radius = radius;
		paint = new Paint();
		paint.setAntiAlias(true);
        paint.setColor(color);
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawCircle(circleX, circleY, radius, paint);
	}
	
	public boolean isTouched(int x, int y, int screenH)
	{
        float error = 0.01f * screenH;

		if(x > circleX - radius - error && x < circleX + radius + error &&
				y > circleY - radius - error && y < circleY + radius + error)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

    public void setColor(int color)
    {
        paint.setColor(color);
    }

    public int getColor()
    {
        return paint.getColor();
    }

    public List<MyPoint> getAdjPoints(MyPoint[][] points)
    {
        List<MyPoint> adjPoints = new ArrayList<MyPoint>();
        try { adjPoints.add(points[indexI + 1][indexJ]); }catch (ArrayIndexOutOfBoundsException e){}
        try { adjPoints.add(points[indexI - 1][indexJ]); }catch (ArrayIndexOutOfBoundsException e){}
        try { adjPoints.add(points[indexI][indexJ + 1]); }catch (ArrayIndexOutOfBoundsException e){}
        try { adjPoints.add(points[indexI][indexJ - 1]); }catch (ArrayIndexOutOfBoundsException e){}
        return adjPoints;
    }

    public boolean isOnLine(MyLine line)
    {
        if ((circleX == line.startPoint.circleX && circleY == line.startPoint.circleY) ||
            (circleX == line.stopPoint.circleX && circleY == line.stopPoint.circleY))
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
}
