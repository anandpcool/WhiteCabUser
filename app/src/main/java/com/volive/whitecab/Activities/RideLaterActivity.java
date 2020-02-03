package com.volive.whitecab.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.volive.whitecab.R;
import com.volive.whitecab.util.PreferenceUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RideLaterActivity extends AppCompatActivity implements View.OnClickListener {

    Typeface typeface, typefaceLight;
    TextView tittle, selectlocationtxt;

    CalendarView calendar;
    TimePicker timePicker;
    String strDate, strTime;
    SimpleDateFormat foramteDate, formatTime;

    String strAddress, strVehicleType;


    ImageView back_ride_later;
    Button btn_select_location;
    PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_later);

        initUI();
        initViews();

    }

    private void initUI() {
        back_ride_later=findViewById(R.id.back_ride_later);
        btn_select_location=findViewById(R.id.btn_select_location);
        calendar = (CalendarView) findViewById(R.id.calendarview);
        timePicker = (TimePicker) findViewById(R.id.simpleTimePicker);

        preferenceUtils=new PreferenceUtils(RideLaterActivity.this);
    }

    private void initViews() {
        back_ride_later.setOnClickListener(this);
        btn_select_location.setOnClickListener(this);

        Intent intent = getIntent();
        strAddress = intent.getStringExtra("address");
        strVehicleType = intent.getStringExtra("vehicleType");

        Date date = new Date();
        calendar.setMinDate(date.getTime());
        foramteDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date dateNew = new Date();
        strDate = foramteDate.format(dateNew);

        formatTime = new SimpleDateFormat("HH:mm:ss",Locale.ENGLISH);
        Date dateNewTime = new Date();
        strTime = formatTime.format(dateNewTime);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub
                int ys = year;
                String strDate1 = String.valueOf(dayOfMonth + "/" + (month + 1) + "/" + year);

                try {
                    SimpleDateFormat spf = new SimpleDateFormat("dd/mm/yyyy");
                    Date newDate = spf.parse(strDate1);
                    spf = new SimpleDateFormat("dd-MM-yyyy");
                    strDate = spf.format(newDate);
                    Log.e("Date is : ", +dayOfMonth + " / " + (month + 1) + " / " + year + " : " + strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                                @Override
                                                public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

                                                    String time = String.valueOf(hourOfDay).toString() + ":" + String.valueOf(minute).toString();
                                                    try {
                                                        SimpleDateFormat spf = new SimpleDateFormat("hh:mm");
                                                        Date newDate = spf.parse(time);
                                                        spf = new SimpleDateFormat("HH:mm:ss");
                                                        strTime = spf.format(newDate);
                                                        Log.e("Time ", String.valueOf(hourOfDay).toString() + ":" + String.valueOf(minute).toString() + " : " + strTime);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                }


                                            }

                                         );

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_ride_later:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_select_location:
                preferenceUtils.setRideType("Ride Later");

                Intent intent = new Intent(RideLaterActivity.this, LaterBookingActivity.class);
                intent.putExtra("Address", strAddress);
                intent.putExtra("vehicleType", strVehicleType);
                intent.putExtra("Date", strDate);
                intent.putExtra("Time", strTime);

                intent.putExtra("key", "");
                Log.e("fsdafndsaklfdsaf",strDate+" "+strTime);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                /*Intent intent=new Intent(RideLaterActivity.this, PickLocationActivity.class);
                startActivity(intent);*/

                break;

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
