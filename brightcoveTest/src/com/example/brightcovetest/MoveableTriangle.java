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
public class MoveableTriangle extends View implements BackgroundLayout.MoveableObject {
    Point point1, point2, point3;
    Paint paint;

    public MoveableTriangle(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean allowObjectMoveFromPosition(float xcoordinate, float ycoordinate) {
        if (point1 == null || point2 == null || point3 == null) {
            return false;
        }

        // Checking the barycentric coordinates of a point relative to the 3 points of a triangle gives an efficient way of determining if that point is in the triangle.
        // alpha, beta & gamma (calculated below) are the barycentric coordinates. The point is in the triangle if the barycentric coordinates are all > 0.
        float alpha = ((point2.y - point3.y) * (xcoordinate - point3.x) + (point3.x - point2.x)* (ycoordinate - point3.y)) /
                ((point2.y - point3.y) * (point1.x - point3.x) + (point3.x - point2.x) * (point1.y - point3.y));

        float beta = ((point3.y - point1.y) * (xcoordinate - point3.x) + (point1.x - point3.x) * (ycoordinate - point3.y)) /
                ((point2.y - point3.y) * (point1.x - point3.x) + (point3.x - point2.x) * (point1.y - point3.y));

        float gamma = 1.0f - alpha - beta;

        return (alpha>0 && beta >0 && gamma>0);
    }

    public MoveableTriangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveableTriangle(Context context, AttributeSet attrs, int defStyle) {
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

        point1 = new Point(0, getHeight());
        point2 = new Point(getWidth(), getHeight());
        point3 = new Point(getWidth()/2, 0);

        Path path = new Path();
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
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
