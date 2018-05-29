package com.example.trcanje;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.trcanje.tracks.Track;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class TrackAdapter extends BaseAdapter {

    private ArrayList<Track> tracksById;
    Context context;

    public TrackAdapter(Map<Integer, Track> tracksById, Context context) {
        this.tracksById = new ArrayList<Track>();
        for(Track t : tracksById.values()) {
            this.tracksById.add(t);
        }
        this.context = context;
    }

    public TrackAdapter(ArrayList<Track> tracks, Context context){
        this.tracksById = tracks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tracksById.size();
    }

    @Override
    public Object getItem(int i) {
        return tracksById.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.tracklist_item,null,false);
        }

        TextView id_view = (TextView) view.findViewById(R.id.TextView_id);
        TextView name_view = (TextView) view.findViewById(R.id.TextView_name);
        TextView time_view = (TextView) view.findViewById(R.id.TextView_time1);
        TextView distance_view = (TextView) view.findViewById(R.id.TextView_distance1);
        TextView speed_view = (TextView) view.findViewById(R.id.TextView_speed1);

        id_view.setText(String.valueOf(tracksById.get(i).getId()));
        name_view.setText(String.valueOf(tracksById.get(i).getName()));
        time_view.setText(tracksById.get(i).timePassed(tracksById.get(i).getEndTime()));
        distance_view.setText(tracksById.get(i).distance(tracksById.get(i).getEndTime()));
        speed_view.setText(tracksById.get(i).speed(tracksById.get(i).getEndTime()));

        return view;
    }
}
