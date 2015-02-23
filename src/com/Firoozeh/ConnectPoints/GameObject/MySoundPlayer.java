package com.Firoozeh.ConnectPoints.GameObject;

import android.content.Context;
import android.media.MediaPlayer;

import com.Firoozeh.ConnectPoints.R;

public class MySoundPlayer
{
    public static final int drawLine = R.raw.draw;
    public static final int drawRect = R.raw.rect;
    public static final int buttonClick = R.raw.click;

    public static void playSound(Context context, int soundID)
    {
        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.setVolume(0.1f, 0.2f);
        mp.start();
    }
}
