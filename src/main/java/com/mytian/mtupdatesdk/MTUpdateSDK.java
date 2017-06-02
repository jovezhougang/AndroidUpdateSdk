package com.mytian.mtupdatesdk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MTUpdateSDK {
    public static void showUpdateDialog(final String title, final String message
            , final Context context, final String downloadUrl, final boolean isForceUpdate
            , final HashMap<String, String> headers) {
        if (context instanceof Activity) {
            final AlertDialog.Builder builder = new AlertDialog
                    .Builder(context)
                    .setTitle(title)
                    .setMessage(Html.fromHtml(message))
                    .setPositiveButton("立即升级", null)
                    .setCancelable(!isForceUpdate)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (context instanceof UpdateActivity) {
                                ((UpdateActivity) context).finish();
                            }
                        }
                    })
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && isForceUpdate) {
                                return true;
                            }
                            return false;
                        }
                    });
            if (!isForceUpdate) {
                builder.setNegativeButton("暂不升级", null);
            }
            final AlertDialog dialog = builder.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isForceUpdate) {
                        dialog.dismiss();
                    }
                    final Uri uri = getDownloadOKFile(context, downloadUrl);
                    if (null != uri) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        context.getApplicationContext().startActivity(intent);
                    } else {
                        final SharedPreferences sharedPreferences
                                = context.getApplicationContext().getSharedPreferences("download", Context.MODE_PRIVATE);
                        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        if (!sharedPreferences.contains(downloadUrl)) {
                            final Map<String, ?> M = sharedPreferences.getAll();
                            for (final String key : M.keySet()) {
                                final Object downloadId = M.get(key);
                                if (downloadId instanceof Long || downloadId instanceof Integer) {
                                    if (-1 != (Long) downloadId) {
                                        manager.remove((long) downloadId);
                                    }
                                    sharedPreferences.contains(key);
                                }
                            }
                            downloadApk(context.getApplicationContext()
                                    , downloadUrl, headers);
                        }
                    }
                }
            });
        } else {
            final Intent intent = new Intent(context, UpdateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            {
                UpdateBean bean = new UpdateBean();
                bean.setDownloadUrl(downloadUrl);
                bean.setForceUpdate(isForceUpdate);
                bean.setHeaders(headers);
                bean.setMessage(message);
                bean.setTitle(title);
                intent.putExtra("UpdateBean", bean);
            }
            context.startActivity(intent);
        }
    }


    private static void downloadApk(final Context context
            , final String downloadUrl, final Map<String, String> headers) {
        final DownloadManager manager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        final ApplicationInfo mApplicationInfo
                = context.getApplicationInfo();
        final Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription(context.getString(mApplicationInfo.labelRes) + "，新版本下载");
        request.setTitle(context.getString(mApplicationInfo.labelRes));
        request.setDestinationInExternalPublicDir("download", context.getString(mApplicationInfo.labelRes) + ".apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        if (null != headers && !headers.isEmpty()) {
            for (final String key : headers.keySet()) {
                request.addRequestHeader(key, headers.get(key));
            }
        }
        long id = manager.enqueue(request);
        final SharedPreferences sharedPreferences
                = context.getSharedPreferences("download", Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(downloadUrl, id).commit();
    }

    private static Uri getDownloadOKFile(final Context context, final String downloadUrl) {
        final SharedPreferences sharedPreferences
                = context.getSharedPreferences("download", Context.MODE_PRIVATE);
        if (sharedPreferences.contains(downloadUrl)) {
            final long downloadId = sharedPreferences.getLong(downloadUrl, -1);
            if (-1 != downloadId) {
                final DownloadManager manager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = null;
                try {
                    cursor = manager.query(new DownloadManager.Query().setFilterById(downloadId));
                    if (null != cursor && cursor.moveToNext()) {
                        final String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        final int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            if (null != uri) {
                                if ((new File(URI.create(uri)))
                                        .exists()) {
                                    return manager.getUriForDownloadedFile(downloadId);
                                } else {
                                    manager.remove(downloadId);
                                    sharedPreferences.edit().remove(downloadUrl).commit();
                                }
                            }
                        } else if (downloadStatus == DownloadManager.STATUS_FAILED) {
                            manager.remove(downloadId);
                            sharedPreferences.edit().remove(downloadUrl).commit();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }
        return null;
    }
}
