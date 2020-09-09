package com.example.account.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.account.R;

public class RdPxListAdapter extends RecyclerView.Adapter<RdPxListAdapter.ViewHolder>{
    private RdPxList[] rdPxLists;

    // RecyclerView recyclerView;
    public RdPxListAdapter(RdPxList[] rdPxLists) {
        this.rdPxLists = rdPxLists;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RdPxList rdPxList = rdPxLists[position];
        holder.textDatetime.setText(rdPxLists[position].getDatetime());
        holder.textPrice.setText(rdPxLists[position].getPrice());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+rdPxList.getPrice(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return  rdPxLists.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textDatetime;
        public TextView textPrice;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textDatetime = (TextView) itemView.findViewById(R.id.textDatetime);
            this.textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}


