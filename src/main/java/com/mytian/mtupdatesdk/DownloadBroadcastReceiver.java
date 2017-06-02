package com.mytian.mtupdatesdk;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context
            , Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.getAction()) {
            final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (-1 != downloadId) {
                final DownloadManager manager =
                        (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (null != manager) {
                    final Uri uri = manager.getUriForDownloadedFile(downloadId);
                    Intent intent1 = new Intent(context
                            , DownloadCompleteActivity.class);
                    intent1.setData(uri);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
            }
        }
    }
}
