package com.volive.whitecab.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.RecyclerAdapters.ComplaintAdapter;
import com.volive.whitecab.R;

public class ComplaintActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_complaint;
    RecyclerView complaint_recycler;
    ComplaintAdapter adapter;
    String[] complaint_texts=new String[]{"I lost an item","Bad driver behaviour","I would like a refund","Different driver / vehicle","Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        initUI();
        initViews();

    }

    private void initUI() {
        back_complaint=findViewById(R.id.back_complaint);
        complaint_recycler=findViewById(R.id.complaint_recycler);
    }

    private void initViews() {
        back_complaint.setOnClickListener(this);

        adapter=new ComplaintAdapter(ComplaintActivity.this,complaint_texts);
        complaint_recycler.setHasFixedSize(true);
        complaint_recycler.setAdapter(adapter);
    }

    public void onItemSelect(int position) {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_complaint:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
