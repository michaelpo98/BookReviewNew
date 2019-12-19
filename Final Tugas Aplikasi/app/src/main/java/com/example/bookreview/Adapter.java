package com.example.bookreview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MViewHolder> {

    ArrayList<PojoBuku> values;

    public Adapter( ArrayList<PojoBuku> pj){
        values = pj;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tampilan_buku, parent, false);

        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

        PojoBuku pb = values.get(position);

        holder.tv_nama.setText(pb.getNama());
        holder.tv_rating.setText(pb.getRating());
        holder.tv_komen.setText(pb.getUlasan());

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nama, tv_komen, tv_rating;
        public MViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nama = itemView.findViewById(R.id.tv_nama_user);
            tv_rating = itemView.findViewById(R.id.tv_rating_buku);
            tv_komen = itemView.findViewById(R.id.tv_komentar_buku);
        }
    }
}


