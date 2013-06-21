package com.example.brightcovetest;

import android.*;
import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dx068-xl on 13-06-21.
 */
public class MoveableCircle extends View implements BackgroundLayout.MoveableObject {
    float centerX, centerY, radius = 0f;
    Paint paint;

    public MoveableCircle(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean allowObjectMoveFromPosition(float xcoordinate, float ycoordinate) {

        return (Math.pow((double) (xcoordinate - centerX), 2) + Math.pow((double) (ycoordinate - centerY), 2) <= Math.pow((double) radius, 2));
    }

    public MoveableCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveableCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(Color.BLUE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        centerX = canvas.getWidth() / 2;
        centerY = canvas.getHeight() / 2;

        if (canvas.getWidth() <= canvas.getHeight()) {
            radius = centerX;
        } else {
            radius = centerY;
        }

        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);

        if (background instanceof ColorDrawable) {
            paint = new Paint();
            paint.setColor(((ColorDrawable) background).getColor());
        } else if (background instanceof PaintDrawable) {
            paint = new Paint();
            paint = ((PaintDrawable) background).getPaint();
        }

        super.setBackgroundDrawable(getContext().getResources().getDrawable(R.color.transparent));
    }


}
