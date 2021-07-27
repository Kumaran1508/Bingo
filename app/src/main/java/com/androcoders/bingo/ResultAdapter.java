package com.androcoders.bingo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Player> list=new ArrayList<>();


    public ResultAdapter(Context context, ArrayList<Player> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View itemview=inflater.inflate(R.layout.resultitem,parent,false);
        return new ResultAdapter.ViewHolder(itemview);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

     holder.name1.setText(list.get(position).getPlayername());
     holder.score1.setText(Long.toString(list.get(position).getScore()));
     holder.bingo1.setText(Long.toString(list.get(position).getBingo()));


    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name1,bingo1,score1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1=itemView.findViewById(R.id.resname);
            bingo1=itemView.findViewById(R.id.resbingo);
            score1=itemView.findViewById(R.id.score);

        }
    }
}
