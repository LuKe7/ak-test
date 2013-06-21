package com.example.brightcovetest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by dx068-xl on 13-06-21.
 */
public class BackgroundLayout extends FrameLayout {

    private MoveableObject selectedObject;
    private ObjectMoveListener listener;
    private float offsetX, offsetY;

    public BackgroundLayout(Context context) {
        super(context);
    }

    public BackgroundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackgroundLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectedObject == null) {
                    return setSelectedChildForPosition(ev.getX(), ev.getY());
                }
                break;

//        case MotionEvent.ACTION_UP:
//            Log.d("","MOVE: action up");
//            selectedObject = null;
//            Log.d("","MOVE: child unselected");
//            break;
//
//        case MotionEvent.ACTION_MOVE:
//            Log.d("","MOVE: action move");
//            break;
            default:
                Log.d("", "MOVE: action " + ev.getAction());
        }


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectedObject != null) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                selectedObject = null;
                break;

            case MotionEvent.ACTION_MOVE:

                // moveSelected(event.getX(), event.getY());
                float newX = event.getX() - offsetX;
                float newY = event.getY() - offsetY;

                ((View) selectedObject).setX(newX);
                ((View) selectedObject).setY(newY);

                if (listener != null) {
                    listener.onObjectMoved(selectedObject, newX, newY);
                }
                break;

            default:
                Log.d("", "MOVE: on touch action " + event.getAction());
        }

        return super.onTouchEvent(event);
    }

    protected boolean setSelectedChildForPosition(float x, float y) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child instanceof MoveableObject && isCoordinateInChildBounds(child, x, y)) {
                if (((MoveableObject) child).allowObjectMoveFromPosition(x - child.getX(), y - child.getY())) {
                    selectedObject = (MoveableObject) child;
                    offsetX = x - child.getX();
                    offsetY = y - child.getY();

                    Log.d("", "MOVE: child selected");
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isCoordinateInChildBounds(View child, float x, float y) {
        if (child != null) {
            return (child.getX() <= x
                    && child.getY() <= y
                    && (child.getX() + child.getWidth()) >= x
                    && (child.getY() + child.getHeight()) >= y);
        }

        return false;
    }


    public interface MoveableObject {

        /**
         * @param xcoordinate - the x value of the coordinate to check, represented in the child's coordinate system
         * @param ycoordinate - the y value of the coordinate to, represented in the child's coordinate system
         * @return true if the object is allowed to move when the (x,y) coordinate is the starting point
         */
        public boolean allowObjectMoveFromPosition(float xcoordinate, float ycoordinate);


    }

    public interface ObjectMoveListener {
        public void onObjectMoved(MoveableObject movedObject, float newXcoordinate, float newYCoordinate);

    }

    public void setObjectMoveListener(ObjectMoveListener listener) {
        this.listener = listener;
    }
}
