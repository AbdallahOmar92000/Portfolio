package com.sarrawi.mymaps.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.sarrawi.mymaps.R;

public class LoadingDialogBar {
    Context context;
    Dialog dialog;

    public LoadingDialogBar(Context context) {
        this.context = context;
    }

    public void ShowDialog(String title) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleTExtView = dialog.findViewById(R.id.textView);
        titleTExtView.setText(title);
        dialog.create();
        dialog.show();
    }

    public void HideDialog() {
        dialog.dismiss();
    }
}
