package com.example.brightcovetest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by dx068-xl on 13-06-20.
 */
public class ShapeActivity extends Activity {
    private View shape;
    private TextView xcord;
    private TextView ycord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape);
        shape = findViewById(R.id.square);
        xcord = (TextView) findViewById(R.id.xcoordinate);
        ycord = (TextView) findViewById(R.id.ycoordinate);

        BackgroundLayout.ObjectMoveListener listener = new BackgroundLayout.ObjectMoveListener() {
            @Override
            public void onObjectMoved(BackgroundLayout.MoveableObject movedObject, float newXcoordinate, float newYCoordinate) {
                if (xcord != null && ycord != null) {
                    xcord.setText("" + newXcoordinate);
                    ycord.setText("" + newYCoordinate);
                }
            }
        };

        BackgroundLayout layout = (BackgroundLayout) findViewById(R.id.move_frame);
        layout.setObjectMoveListener(listener);


    }


}
