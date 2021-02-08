package com.easylife.wishy;

import android.app.Dialog;
import android.content.Context;

public class LoadingDialog {

    Context context;
    Dialog dialog;
    public LoadingDialog(Context context) {
        this.context= context;
        dialog= new Dialog(context);
    }

    public void showDialog(){
        dialog.setContentView(R.layout.loading_dialog);
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }




}
