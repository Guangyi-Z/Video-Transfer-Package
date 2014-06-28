package com.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by caolijie on 14-6-19.
 */
public class CustomDialog extends Dialog {
    Context context;
    int layoutResId;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialog(Context context, int layoutResId, int theme) {
        super(context, theme);
        this.layoutResId = layoutResId;
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutResId);

    }
}