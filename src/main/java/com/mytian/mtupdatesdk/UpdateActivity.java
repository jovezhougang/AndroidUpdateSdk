package com.mytian.mtupdatesdk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class UpdateActivity extends AppCompatActivity {
    private UpdateBean bean;
    static Handler H = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            bean = getIntent().getParcelableExtra("UpdateBean");
        } else {
            bean = savedInstanceState.getParcelable("UpdateBean");
        }
        if (null == bean) {
            finish();
        } else {
            H.postDelayed(showUpdateDialogRunnable, 500);
        }
    }

    Runnable showUpdateDialogRunnable = new Runnable() {
        @Override
        public void run() {
            MTUpdateSDK.showUpdateDialog(bean.getTitle(), bean.getMessage()
                    , UpdateActivity.this, bean.getDownloadUrl(), bean.isForceUpdate()
                    , bean.getHeaders());
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("UpdateBean", bean);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        UpdateBean bean = getIntent().getParcelableExtra("UpdateBean");
        if (null != bean) {
            if (!bean.equals(this.bean)) {
                this.bean = bean;
                H.postDelayed(showUpdateDialogRunnable, 500);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        H.removeCallbacks(showUpdateDialogRunnable);
    }


}
