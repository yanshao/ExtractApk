package com.yanshao.extractapk;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.yanshao.extractapk.adapter.AppListRecyclerAdapter;
import com.yanshao.extractapk.bean.AppInfo;
import com.yanshao.extractapk.util.PermissionsUtils;
import com.yanshao.extractapk.util.Utils;
import com.yanshao.extractapk.view.ClearEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = {

            //文件
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    RecyclerView listrecycler;
    ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
    AppListRecyclerAdapter appListRecyclerAdapter;
    ClearEditText key_edit;
    ArrayList<AppInfo> adapterappList = new ArrayList<AppInfo>();

    String apkpath, apkname;
    TextView number_text;
    File sd = new File(Environment.getExternalStorageDirectory().getPath() + "/yan");

    ProgressDialog progressDialog;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            copyFile(apkpath, sd.getAbsolutePath(), apkname + ".apk");
            //需要处理操作
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listrecycler = findViewById(R.id.listrecycler);
        key_edit = findViewById(R.id.key_edit);
        number_text = findViewById(R.id.number_text);

        PermissionsUtils.getInstance().checkPermissions(this, permissions, new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {


                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                        AppInfo tmpInfo = new AppInfo();
                        tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                        tmpInfo.setPackageName(packageInfo.packageName);
                        tmpInfo.setVersionName(packageInfo.versionName);
                        tmpInfo.setVersionCode(packageInfo.versionCode);
                        tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                        appList.add(tmpInfo);
                    }
                }
                adapterappList.addAll(appList);
                number_text.setText("共有" + appList.size() + "个应用");
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listrecycler.setLayoutManager(layoutManager);

                // searchRecycler.addItemDecoration(new RecycleViewDivider(SearchActivity.this, DividerItemDecoration.VERTICAL,10, getResources().getColor(R.color.txt_unslct)));
                appListRecyclerAdapter = new AppListRecyclerAdapter(MainActivity.this, adapterappList);
                listrecycler.setAdapter(appListRecyclerAdapter);
            }

            @Override
            public void forbidPermissions() {
                finish();
            }
        });


        appListRecyclerAdapter.setOnItemCallClickListener(new AppListRecyclerAdapter.OnItemCallClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = getPackageManager().getApplicationInfo(appList.get(position).getPackageName(), 0);
                    Log.e("yy", "applicationInfo=" + applicationInfo.sourceDir);
                    apkpath = applicationInfo.sourceDir;
                    apkname = appList.get(position).getAppName();
                    if (!TextUtils.isEmpty(apkpath) && !TextUtils.isEmpty(apkname)) {
                        showWaiting();
                        new Thread(runnable).start();
                    }


                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        key_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Utils.hideKeyboard(MainActivity.this, key_edit);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                    String key = key_edit.getText().toString();
                    if (!TextUtils.isEmpty(key)) {

                        getkeyList(key);

                    }

                    return true;
                }
                return false;
            }
        });
        key_edit.setOnDeleteClickListener(new ClearEditText.OnDeleteClickListener() {
            @Override
            public void onClick() {

                adapterappList.clear();
                adapterappList.addAll(appList);
                number_text.setText("共有" + appList.size() + "个应用");
                appListRecyclerAdapter.notifyDataSetChanged();
                Utils.hideKeyboard(MainActivity.this, key_edit);
            }
        });
    }

    private void getkeyList(String key) {
        adapterappList.clear();

        for (int i = 0; i < appList.size(); i++) {
            if (appList.get(i).getAppName().indexOf(key) != -1) {
                adapterappList.add(appList.get(i));
            }
        }
        number_text.setText("搜索出" + adapterappList.size() + "个应用");
        appListRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    public void copyFile(String oldPath, String newPath, String name) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件

                if (!newfile.exists()) {
                    newfile.mkdirs();
                    newfile.createNewFile();
                }
                File path = new File(newfile.getAbsoluteFile() + "/" + name);
                FileOutputStream fs = new FileOutputStream(path);
                byte[] buffer = new byte[1444];
                Log.e("yy", "fs" + ((FileInputStream) inStream).getChannel().size());
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                    Log.e("yy", "bytesum==" + bytesum);
                }
                inStream.close();
                Looper.prepare();
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, "导出成功！导出目录为：" + path, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

 
    private void showWaiting() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("导出中...");
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();

    }

}
