package com.volive.whitecab.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volive.whitecab.Adapters.RecyclerAdapters.WalletAdapter;
import com.volive.whitecab.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment {

    RecyclerView mRecyclerView;
    public AllFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_all, container, false);
        initUI(view);
        initViews();
        return view;
    }

    private void initUI(View view) {
        mRecyclerView =view.findViewById(R.id.wallet_recycler);
    }

    private void initViews() {
        WalletAdapter walletAdapter = new WalletAdapter(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(walletAdapter);
    }

}
