/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.apricot.cleanmaster.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.ApkFile;


import java.io.File;
import java.io.IOException;


/**
 * 常用操作工具类 如传送APK 导出APK等操作
 * Created by GuDong on 12/7/15 17:47.
 * Contact with 1252768410@qq.com.
 */
public class ActionUtil {
    /**
     * 传送安装包
     * @param entity
     */
    public static void shareApk(Activity activity, ApkFile entity) {
        final File srcFile = new File(entity.getFilePath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_share_app),entity.getApkName()), Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(entity.getFilePath())));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent,entity.getApkName()));
    }

    /**
     * 安装APK
     * @param entity
     */
    public static void installApp(Activity activity, ApkFile entity) {
        final File srcFile = new File(entity.getFilePath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_install_app), entity.getApkName()), Snackbar.LENGTH_LONG).show();
            return;
        }

        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setAction(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(srcFile),
                "application/vnd.android.package-archive");
        activity.startActivity(mIntent);
    }
    /**
     * export apk file
     * @param entity
     */
    public static void exportApk(final Activity activity, final ApkFile entity) {
        //判断sd卡是否挂载


        final File srcFile = new File(entity.getFilePath());
        if(!srcFile.exists()){
            Snackbar.make(activity.getWindow().getDecorView(),String.format(activity.getString(R.string.fail_export_app),entity.getApkName()), Snackbar.LENGTH_LONG).show();
            return;
        }
        File exportParentFile = FileUtil.createDir(FileUtil.getSDPath(),FileUtil.KEY_EXPORT_DIR);

        String exportFileName = entity.getApkName().concat("_").concat(entity.getVersionName()).concat(".apk");
        final File exportFile = new File(exportParentFile, exportFileName);
        String contentInfo = String.format(activity.getString(R.string.dialog_message_file_exist), exportFileName, exportFile.getParentFile().getAbsolutePath());
        if (exportFile.exists()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_point)
                    .setMessage(contentInfo)
                    .setPositiveButton(R.string.dialog_action_exist_not_override, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(R.string.dialog_action_exist_override, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            copyFile(activity,srcFile, exportFile);
                        }
                    })
                    .show();
        } else {
            String pointInfo = String.format(activity.getString(R.string.dialog_message_export),entity.getApkName(),exportFile.getParentFile().getAbsolutePath());
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_point)
                    .setMessage(pointInfo)
                    .setPositiveButton(R.string.dialog_confirm_export, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copyFile(activity,srcFile, exportFile);
                            T.showShort(activity,"应用"+entity.getApkName()+"已成功导出");
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel,null)
                    .show();

        }
    }


    private static void copyFile(final Activity activity,File srcFile, final File exportFile) {
        try {
            FileUtil.copyFileUsingFileChannels(srcFile, exportFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
