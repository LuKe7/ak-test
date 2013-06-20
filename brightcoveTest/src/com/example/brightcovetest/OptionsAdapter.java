package com.example.brightcovetest;

import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import android.util.Log;


public class OptionsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Option> objects;
    private int menuItemLayout = R.layout.side_menu_item;
//    private int volumeSliderid = R.id.volume_slider;
    private int volumeSliderLayout = R.layout.volume_slider;
    private int fieldId;


    public OptionsAdapter(Context context, Option[] list) {

        this.context = context;
        objects = Arrays.asList(list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Option getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void newOptionsList(Option[] list) {
        objects = Arrays.asList(list);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        switch (getItemViewType(position)) {
            case 0:
                view = createViewFromResource(position, convertView, parent, menuItemLayout);
                updateTextView(view, getItem(position));

                break;
            case 1:
                view = createViewFromResource(position, convertView, parent, volumeSliderLayout);
                updateVolumeSliderView(view);
                break;
            default:
                view = createViewFromResource(position, convertView, parent, View.NO_ID);
                break;
        }

        return view;
    }


    private void updateTextView(View view, Option item) {
        TextView text = (TextView) view;
        text.setText(item.getName());
    }

    private void updateVolumeSliderView(View view) {
        final AudioManager audioManager = (AudioManager) TestApplicaton.getAppContext().getSystemService(Context.AUDIO_SERVICE);


        VolumeManager.getInstance().registerObserver(new VolumeManager.VolumeObserver() {
            @Override
            public void onVolumeUp() {

            }

            @Override
            public void onVolumeDown() {

            }

            @Override
            public void onVolumeMute(int volumeStream, boolean muteOn) {

            }
        });



    }




    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;

        if (convertView == null) {
            if (resource != View.NO_ID) {
                view = inflater.inflate(resource, parent, false);
            } else {
                view = new View(parent.getContext());
            }
        } else {
            view = convertView;
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


}
