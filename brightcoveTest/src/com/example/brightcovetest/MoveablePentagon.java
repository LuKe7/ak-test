package com.example.brightcovetest;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dx068-xl on 13-06-21.
 */
public class MoveablePentagon extends View implements BackgroundLayout.MoveableObject {
    Point point1, point2, point3, point4, point5;
    Paint paint;

    public MoveablePentagon(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean allowObjectMoveFromPosition(float xcoordinate, float ycoordinate) {
        if (point1 == null || point2 == null || point3 == null || point4 == null || point5 == null) {
            return false;
        }

        //TODO: map pentagon outline generically (generic to orientation of shape on screen)
//
//        return getCoordinatePositionRelativeToLine(xcoordinate, ycoordinate, point1, point2) != 1
//                && getCoordinatePositionRelativeToLine(xcoordinate, ycoordinate, point);
        
        return true;
    }

    /**
     * @return 0 = on line, -1 = below line, 1 = above line
     */
    protected int getCoordinatePositionRelativeToLine(float x, float y, Point l1, Point l2) {
        float crossProduct = ((l2.x - l1.x) * (y - l1.y) - (l2.y - l1.y) * (x - l1.x));

        if (crossProduct > 0) {
            return -1;
        } else if (crossProduct < 0) {
            return 1;
        } else {
            return 0;
        }

    }


    public MoveablePentagon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveablePentagon(Context context, AttributeSet attrs, int defStyle) {
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


        point1 = new Point(getWidth() / 2, 0);
        point2 = new Point(getWidth(), 5 * getHeight() / 12);
        point3 = new Point(13 * getWidth() / 16, getHeight());
        point4 = new Point(3 * getWidth() / 16, getHeight());
        point5 = new Point(0, 5*getHeight() / 12);

        Path path = new Path();
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point4.x, point4.y);
        path.lineTo(point5.x, point5.y);
        path.lineTo(point1.x, point1.y);
        path.close();

        paint.setStyle(Paint.Style.FILL);

        canvas.drawPath(path, paint);
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
