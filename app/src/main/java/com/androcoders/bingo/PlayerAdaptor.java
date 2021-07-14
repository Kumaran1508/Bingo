package com.androcoders.bingo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayerAdaptor extends RecyclerView.Adapter<PlayerAdaptor.ViewHolder> {
  private  Context context;
  private ArrayList<Player> list;
  private boolean isOwner;

    public PlayerAdaptor(Context context, ArrayList<Player> list,boolean isOwner) {
        this.context = context;
        this.list = list;
        this.isOwner=isOwner;
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

            if (!isOwner)
                kickbutton.setVisibility(View.GONE);

        }
    }
}
