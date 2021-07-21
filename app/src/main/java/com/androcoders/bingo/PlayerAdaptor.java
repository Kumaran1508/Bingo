package com.androcoders.bingo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlayerAdaptor extends RecyclerView.Adapter<PlayerAdaptor.ViewHolder> {
  private  Context context;
  private ArrayList<Player> list;
  private boolean isOwner;
  private String roomkey;
  private String ownerId;

    public PlayerAdaptor(Context context, ArrayList<Player> list,boolean isOwner,String roomkey) {
        this.context = context;
        this.list = list;
        this.isOwner=isOwner;
        this.roomkey=roomkey;

        FirebaseFirestore.getInstance().collection("rooms").document(roomkey)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ownerId = documentSnapshot.getString("owner");
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View itemview =inflater.inflate(R.layout.player_room_item,parent,false);
        ViewHolder viewHolder= new ViewHolder(itemview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdaptor.ViewHolder holder, int position) {
        holder.playername.setText(list.get(position).getPlayername());

        if (list.get(position).getPlayerid().contentEquals(ownerId))
            holder.kickbutton.setVisibility(View.INVISIBLE);

        holder.kickbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore =FirebaseFirestore.getInstance();

                firestore.collection("rooms")
                        .document(roomkey)
                        .collection("players")
                        .document(list.get(position).getPlayerid())
                        .delete();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView playername;
        private Button kickbutton;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            playername=itemView.findViewById(R.id.player_name);
            kickbutton =itemView.findViewById(R.id.click_btn);

            String currentUser = GoogleSignIn.getLastSignedInAccount(context).getId();

            if (!ownerId.contentEquals(currentUser))
                kickbutton.setVisibility(View.GONE);



        }
    }
}
