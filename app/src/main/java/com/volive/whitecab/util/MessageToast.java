package com.volive.whitecab.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by ADMIN on 19-Jan-17.
 */

public class MessageToast {


    public static void showToastMethod(Context context, String message) {

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

}
