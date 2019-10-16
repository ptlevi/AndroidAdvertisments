package com.ptlevi.sapientia.ms.project;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Drako on 13-Nov-17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Advertisment> advertismentList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<Advertisment> advertismentList ) {
        this.mInflater = LayoutInflater.from(context);
        this.advertismentList = advertismentList;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.advertisment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = advertismentList.get(position).getTitle();
        String description = advertismentList.get(position).getDescription();
        String photo = advertismentList.get(position).getImage();
        holder.TVtitle.setText(title);
        holder.TVdetail.setText(description);
        Glide.with(mInflater.getContext())
                .load(photo)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .into(holder.IVimage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return advertismentList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView TVtitle;
        private TextView TVdetail;
        private ImageView IVimage;

        private ViewHolder(View itemView) {
            super(itemView);
            TVtitle = (TextView) itemView.findViewById(R.id.TVtitle);
            TVdetail = (TextView) itemView.findViewById(R.id.TVdetail);
            IVimage = (ImageView) itemView.findViewById(R.id.IVimage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getTitle(int id) {
        return advertismentList.get(id).getTitle();
    }
    public String getDescription(int id){
        return advertismentList.get(id).getDescription();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}