package com.example.trcanje;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.trcanje.tracks.Track;

import org.w3c.dom.Text;

import java.util.Map;

public class TrackAdapter extends BaseAdapter {

    private  Map<Integer, Track> tracksById;
    Context context;

    public TrackAdapter(Map<Integer, Track> tracksById, Context context) {
        this.tracksById = tracksById;
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
        TextView time_view = (TextView) view.findViewById(R.id.TextView_time1);
        TextView distance_view = (TextView) view.findViewById(R.id.TextView_distance1);
        TextView speed_view = (TextView) view.findViewById(R.id.TextView_speed1);

        id_view.setText(tracksById.get(i).getId());
        time_view.setText(tracksById.get(i).timePassed(tracksById.get(i).endTime));
        distance_view.setText(tracksById.get(i).distance(tracksById.get(i).endTime));
        speed_view.setText(tracksById.get(i).speed(tracksById.get(i).endTime));
        return view;
    }
}
