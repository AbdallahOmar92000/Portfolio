package com.sarrawi.mymaps.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarrawi.mymaps.R;
import com.sarrawi.mymaps.entities.FavouriteLocation;
import com.sarrawi.mymaps.utils.LdgoHelpers;

import java.util.ArrayList;

public class FavouriteRecyclerAdapter extends RecyclerView.Adapter<FavouriteRecyclerAdapter.MyViewHolder> {
    private ArrayList<FavouriteLocation> savedLocation;
    private LdgoHelpers ldgoHelpers;

    public FavouriteRecyclerAdapter (ArrayList<FavouriteLocation> savedLocation) {
        this.savedLocation = savedLocation;
        this.ldgoHelpers = ldgoHelpers;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView formatted_addressTxt, nameTxt;
        private ImageView photo_referenceTxt;

        public  MyViewHolder(final View view){
            super(view);
            formatted_addressTxt = view.findViewById(R.id.formatted_address);
            nameTxt = view.findViewById(R.id.name);
            photo_referenceTxt = view.findViewById(R.id.photo_reference);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = savedLocation.get(position).getName();
        String formatted_address = savedLocation.get(position).getFormatted_address();
//        String image = savedLocation.get(position).getPhoto_reference();

        holder.nameTxt.setText(name);
        holder.formatted_addressTxt.setText(formatted_address);
//        holder.photo_referenceTxt.setIm
//        Picasso.get().load(ldgoHelpers.googpeMapsImage(image)).into(locationImage);

    }

    @Override
    public int getItemCount() {
        return savedLocation.size();
    }
}
