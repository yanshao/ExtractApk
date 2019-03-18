package com.yanshao.extractapk.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 这儿是类说明
 *
 * @author 新浪微博@Wang丶Yan
 * @github:https://github.com/yanshao
 * @createtime 2018/1/15
 */

public class Utils {


    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    context.getResources().getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @TargetApi(19)
    public static void transparencyBar(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window =activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void copyFilesAssets(Context context, String oldPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                String path = Environment.getExternalStorageDirectory().toString()
                        + "/YanDemo";
                File path1 = new File(path);
                if (!path1.exists()) {
                    path1.mkdirs();
                }
                File picfile = new File(path1, oldPath);
                for (String fileName : fileNames) {
                    copyFilesAssets(context, oldPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);

                String path = Environment.getExternalStorageDirectory().toString()
                        + "/YanDemo";
                File path1 = new File(path);
                if (!path1.exists()) {
                    path1.mkdirs();
                }

                File picfile = new File(path1, oldPath);

                FileOutputStream fos = new FileOutputStream(picfile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   static void  hideKeyboard(Context context, EditText view){
        InputMethodManager imm = (InputMethodManager)context. getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

}
