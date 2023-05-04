package gatech.team4.campusdiscovery.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.MyViewHolder> {
    private ArrayList<User> userList;
    private final RecyclerViewInterface recyclerViewInterface;

    public UserViewAdapter(ArrayList<User> userList,
                           RecyclerViewInterface recyclerViewInterface) {
        this.userList = userList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.userlist_recyclerview_adapter_layout,
                parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final User user = userList.get(position);
        holder.countTextView.setText((position + 1) + ".");
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userEmail;
        private ImageButton deleteButton;
        private TextView countTextView;

        private CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            countTextView = itemView.findViewById(R.id.countTextView);
            userName = itemView.findViewById(R.id.nameTextView);
            userEmail = itemView.findViewById(R.id.emailTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cardView = itemView.findViewById(R.id.cardView);

            deleteButton.setOnClickListener(view -> {
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