package gatech.team4.campusdiscovery.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.R;

public class RecyclerViewIndicatorAdapter
        extends RecyclerView.Adapter<RecyclerViewIndicatorAdapter.MyViewHolder> {
    private List<CampusActivity> activityList;
    private List<CampusActivity> totalActivityList;
    private final RecyclerViewInterface recyclerViewInterface;
    private DBHelper db;
    private MyViewHolder viewHolder;

    public RecyclerViewIndicatorAdapter(List<CampusActivity> activityList,
                                        RecyclerViewInterface recyclerViewInterface,
                                        List<CampusActivity> totalActivityList) {
        this.activityList = activityList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.totalActivityList = totalActivityList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        db = new DBHelper(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_indicator_adapter_layout,
                parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        viewHolder = holder;
        final CampusActivity campusActivity = activityList.get(position);
        holder.activitySubject.setText(campusActivity.getSubject());
        holder.hostTextView.setText(
                "Host: " + db.getUserFromEmail(campusActivity.getHost()).getName());
        holder.dateTimeTextView.setText(
                "Date: " + campusActivity.getDate() + " @ " + campusActivity.getTime());
        String[] location = campusActivity.getLocation().split("__,__");
        holder.locationTextView.setText(
                "Location: " + location[0].substring(0, 6) + " R#" + location[1]);
        holder.activityImageView.setBackgroundResource(campusActivity.getImage());

        if (isActivityTimeConflict(activityList)[position]) {
            holder.errorIndicator.setVisibility(View.VISIBLE);
        }
    }

    private boolean[] isActivityTimeConflict(List<CampusActivity> currentActivityList) {
        ArrayList<String> dateList = new ArrayList<>();
        for (int i = 0; i < currentActivityList.size(); i++) {
            Log.i("INDICATOR current", "" + currentActivityList.get(i).getDate());
            dateList.add(currentActivityList.get(i).getDate());
        }
        ArrayList<String> dateActivityList = new ArrayList<>();
        for (int i = 0; i < totalActivityList.size(); i++) {
            Log.i("INDICATOR total", "" + totalActivityList.get(i).getDate());
            dateActivityList.add(totalActivityList.get(i).getDate());
        }
        boolean[] indicators = new boolean[dateList.size()];
        for (int i = 0; i < indicators.length; i++) {
            if (Collections.frequency(dateActivityList, dateList.get(i)) >= 2) {
                indicators[i] = true;
            } else {
                indicators[i] = false;
            }
            Log.i("INDICATOR", "" + indicators[i]);
        }
        return indicators;
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView activitySubject;
        private TextView hostTextView;
        private TextView dateTimeTextView;
        private TextView locationTextView;
        private ImageView activityImageView;
        private ImageView errorIndicator;
        private CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            activitySubject = itemView.findViewById(R.id.activitySubjectTextView);
            hostTextView = itemView.findViewById(R.id.hostTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            activityImageView = itemView.findViewById(R.id.activityImageView);
            cardView = itemView.findViewById(R.id.cardView);
            errorIndicator = itemView.findViewById(R.id.errorIndicator);

            // set GONE
            errorIndicator.setVisibility(View.GONE);
            errorIndicator.bringToFront();

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();
                    System.out.println(pos);
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });

        }
    }
}