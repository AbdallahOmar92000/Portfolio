package com.sarrawi.mymaps.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarrawi.mymaps.R;
import com.sarrawi.mymaps.entities.Address;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<Address> searches;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter(ArrayList<Address> searches, RecyclerViewClickListener listener){
        this.searches = searches;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTxt;

        MyViewHolder(final View view) {
            super(view);
            nameTxt = view.findViewById(R.id.name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = searches.get(position).getFormatted_address();
        holder.nameTxt.setText(name);
    }

    @Override
    public int getItemCount() {
        return searches.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}
