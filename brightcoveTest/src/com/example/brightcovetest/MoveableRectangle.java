package com.example.brightcovetest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dx068-xl on 13-06-21.
 */
public class MoveableRectangle extends View implements BackgroundLayout.MoveableObject{

    public MoveableRectangle(Context context) {
        super(context);
    }

    public MoveableRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveableRectangle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean allowObjectMoveFromPosition(float xcoordinate, float ycoordinate) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
