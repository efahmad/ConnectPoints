package com.Firoozeh.ConnectPoints.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.Firoozeh.ConnectPoints.BluetoothActivity;
import com.Firoozeh.ConnectPoints.GameObject.MySoundPlayer;
import com.Firoozeh.ConnectPoints.LocalActivity;
import com.Firoozeh.ConnectPoints.R;

public class TitleView extends View 
{
    private Bitmap titleGraphic;
    private Bitmap singlePlayerButtonUp;
    private Bitmap singlePlayerButtonDown;
    private Bitmap multiPlayerButtonUp;
    private Bitmap multiPlayerButtonDown;
    private int screenW;
    private int screenH;
    private boolean singlePlayerButtonPressed;
    private boolean multiPlayerButtonPressed;
    private Context myContext;
    private Bitmap paperGraphic;

	public TitleView(Context context)
	{
		super(context);
        myContext = context;
        titleGraphic = BitmapFactory.decodeResource(getResources(),
                R.drawable.title);
        // Load Single Player Game button
        singlePlayerButtonUp = BitmapFactory.decodeResource(getResources(),
                R.drawable.single_player_button_up);
        singlePlayerButtonDown = BitmapFactory.decodeResource(getResources(),
                R.drawable.single_player_button_down);
        // Load Multi Player Game button
        multiPlayerButtonUp = BitmapFactory.decodeResource(getResources(),
                R.drawable.multi_player_button_up);
        multiPlayerButtonDown = BitmapFactory.decodeResource(getResources(),
                R.drawable.multi_player_button_down);
        // Load paper graphic
        paperGraphic = BitmapFactory.decodeResource(getResources(),
                R.drawable.paper);
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
        // Draw paper
        canvas.drawBitmap(paperGraphic, (screenW - paperGraphic.getWidth()) / 2,
                (screenH - paperGraphic.getHeight()) / 2, null);
        // Draw title
        canvas.drawBitmap(titleGraphic,
                (screenW - titleGraphic.getWidth()) / 2, (int)(screenH * 0.1), null);
        // Draw Single Player Game button
        if(singlePlayerButtonPressed)
        {
            canvas.drawBitmap(singlePlayerButtonDown,
                    (screenW - singlePlayerButtonDown.getWidth()) / 2,
                    (int)(screenH * 0.4), null);
        }
        else
        {
            canvas.drawBitmap(singlePlayerButtonUp,
                    (screenW - singlePlayerButtonUp.getWidth()) / 2,
                    (int)(screenH * 0.4), null);
        }
        // Draw Multi Player Game button
        if (multiPlayerButtonPressed)
        {
            canvas.drawBitmap(multiPlayerButtonDown,
                    (screenW - multiPlayerButtonDown.getWidth()) / 2,
                    (int)(screenH * 0.55), null);
        }
        else
        {
            canvas.drawBitmap(multiPlayerButtonUp,
                    (screenW - multiPlayerButtonUp.getWidth()) / 2,
                    (int)(screenH * 0.55), null);
        }
	}

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        int eventaction = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch(eventaction)
        {
            case MotionEvent.ACTION_DOWN:
                // Check if Single Player Game button is pressed
                if (x > (screenW - singlePlayerButtonUp.getWidth()) / 2 &&
                    x < ((screenW + singlePlayerButtonUp.getWidth()) / 2) &&
                    y > (int)(screenH * 0.4) &&
                    y < (int)(screenH * 0.4) + singlePlayerButtonUp.getHeight())
                {
                    singlePlayerButtonPressed = true;
                    MySoundPlayer.playSound(myContext, MySoundPlayer.buttonClick);
                }
                // Check if Multi Player Game button is pressed
                if (x > (screenW - multiPlayerButtonUp.getWidth()) / 2 &&
                        x < ((screenW + multiPlayerButtonUp.getWidth()) / 2) &&
                        y > (int)(screenH * 0.55) &&
                        y < (int)(screenH * 0.55) + multiPlayerButtonUp.getHeight())
                {
                    multiPlayerButtonPressed = true;
                    MySoundPlayer.playSound(myContext, MySoundPlayer.buttonClick);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (singlePlayerButtonPressed)
                {
                    Intent localIntent = new Intent(myContext, LocalActivity.class);
                    myContext.startActivity(localIntent);
                }
                if (multiPlayerButtonPressed)
                {
                    Intent bluetoothIntent = new Intent(myContext, BluetoothActivity.class);
                    myContext.startActivity(bluetoothIntent);
                }
                singlePlayerButtonPressed = false;
                multiPlayerButtonPressed = false;
                break;
        }
        invalidate();
        return true;
    }
}
