package com.volive.whitecab.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ADMIN on 10-Feb-17.
 */

public class DialogsUtils {

    public static ProgressDialog showProgressDialog(Context context, String text){
         String message=text;
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }
}
