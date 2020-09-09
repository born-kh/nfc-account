package com.example.account.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.account.R;
import com.example.account.models.RdPxList;
import com.example.account.models.RdPxListAdapter;


public class RdFragment extends Fragment {
   private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rd_layout, null);
      recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);




        return  view;
    }


    public void setRdPxList( RdPxList[]  rdPxLists){
        RdPxListAdapter adapter = new RdPxListAdapter(rdPxLists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }


}