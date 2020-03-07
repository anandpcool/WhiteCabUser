package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.volive.whitecab.Adapters.ViewPagerAdapters.AboutPagerAdapter;
import com.volive.whitecab.DataModels.AboutPojo;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_about;
    ViewPager about_viewPager;
    AboutPagerAdapter adapter;
    PageIndicatorView circlePageIndicator;
    int[] about_images=new int[]{R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image};
    private ProgressDialog myDialog;
    Boolean netConnection = false;
    Boolean nodata = false;
    ArrayList<AboutPojo> arrAboutUs;
    NetworkConnection nw;
    SessionManager sm;
    String strLanguage="";
    WebView about_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initUI();
        initViews();

    }

    private void initUI() {
        back_about=findViewById(R.id.back_about);
        about_viewPager=findViewById(R.id.about_viewPager);
        circlePageIndicator = findViewById(R.id.pager_indicator);
        circlePageIndicator.setViewPager(about_viewPager);
        about_webView=findViewById(R.id.about_webView);
        nw=new NetworkConnection(AboutActivity.this);
        sm=new SessionManager(AboutActivity.this);

    }

    private void initViews() {
        back_about.setOnClickListener(this);


        new aboutUs().execute();
      /*  //View Pager
        final float density = getResources().getDisplayMetrics().density;
        circlePageIndicator.setRadius(5 * density);
        about_viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//        adapter=new AboutPagerAdapter(AboutActivity.this, about_images);
//        about_viewPager.setAdapter(adapter);
        circlePageIndicator.setCount(about_images.length);
*/
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.back_about:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }

    }

    private class aboutUs extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String base_url,message, message_ar,heading_text_en;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(AboutActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"about_us?" + "&API-KEY=1514209135";
                    Log.e("AboutFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    base_url=js.getString("base_url");
                    Log.e("Fare", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject jsonObject = js.getJSONObject("about");

                        heading_text_en=jsonObject.getString("heading_text_en");

                        JSONArray jsonArray=jsonObject.getJSONArray("images");

                        if(jsonArray != null && jsonArray.length() > 0){
                            arrAboutUs=new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){

                                JSONObject object=jsonArray.getJSONObject(i);

                                String id= object.getString("id");
                                String image=object.getString("image");
                                AboutPojo aboutPojo= new AboutPojo(id, image);
                                arrAboutUs.add(aboutPojo);
                            }

                        }

                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
//                        message_ar = js.getString("message_ar");

                    }

                    nodata = false;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    nodata = true;
                }

                netConnection = true;

            } else {

                netConnection = false;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (myDialog.isShowing())
                myDialog.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(AboutActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            about_webView.loadDataWithBaseURL(null, heading_text_en, "text/html", "UTF-8", null);
                        }

                        if(!arrAboutUs.isEmpty()){
                            circlePageIndicator.setVisibility(View.VISIBLE);
                            adapter=new AboutPagerAdapter(AboutActivity.this,arrAboutUs,base_url);
                            about_viewPager.setAdapter(adapter);
                            final float density = getResources().getDisplayMetrics().density;
                            circlePageIndicator.setRadius(5 * density);
                            about_viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                            circlePageIndicator.setCount(arrAboutUs.size());
                        }

                    } else {
                        MessageToast.showToastMethod(AboutActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(AboutActivity.this, getString(R.string.check_net_connection));

            }

        }


    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
