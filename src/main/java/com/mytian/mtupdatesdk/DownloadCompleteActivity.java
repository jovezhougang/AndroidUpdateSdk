package com.mytian.mtupdatesdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class DownloadCompleteActivity extends AppCompatActivity {
    static Handler H = new Handler(Looper.getMainLooper());
    AlertDialog dialog;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            uri = getIntent().getData();
        } else {
            uri = savedInstanceState.getParcelable("uri");
        }
        if (null == uri) {
            finish();
        } else {
            H.postDelayed(showAlertDialogRunnable, 500);
        }
    }

    Runnable showAlertDialogRunnable = new Runnable() {
        @Override
        public void run() {
            showAlertDialog();
        }
    };

    void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("下载完成").setMessage("应用安装文件，已经下载完成" +
                        "。是否进行安装。").setPositiveButton("安装", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        getApplicationContext().startActivity(intent);
                    }
                }).setNegativeButton("取消", null);
        dialog = builder.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("uri", uri);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        uri = getIntent().getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        H.removeCallbacks(showAlertDialogRunnable);
    }
}
