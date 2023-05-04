package gatech.team4.campusdiscovery.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private List<CampusActivity> activityList;
    private final RecyclerViewInterface recyclerViewInterface;
    private DBHelper db;

    public RecyclerViewAdapter(
            List<CampusActivity> activityList, RecyclerViewInterface recyclerViewInterface) {
        this.activityList = activityList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        db = new DBHelper(parent.getContext());
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CampusActivity campusActivity = activityList.get(position);
        holder.activitySubject.setText(campusActivity.getSubject());
        holder.hostTextView
                .setText("Host: " + db.getUserFromEmail(campusActivity.getHost()).getName());
        holder.dateTimeTextView
                .setText("Date: " + campusActivity.getDate() + " @ " + campusActivity.getTime());
        String[] location = campusActivity.getLocation().split("__,__");
        holder.locationTextView
                .setText("Location: " + location[0].substring(0, 6) + " R#" + location[1]);
        holder.activityImageView.setBackgroundResource(campusActivity.getImage());

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
        private CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            activitySubject = itemView.findViewById(R.id.activitySubjectTextView);
            hostTextView = itemView.findViewById(R.id.hostTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            activityImageView = itemView.findViewById(R.id.activityImageView);
            cardView = itemView.findViewById(R.id.cardView);

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