package com.yanshao.extractapk;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yanshao.extractapk.adapter.AppListRecyclerAdapter;
import com.yanshao.extractapk.bean.AppInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = {

            //文件
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
RecyclerView listrecycler;
    ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
    AppListRecyclerAdapter appListRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listrecycler=findViewById(R.id.listrecycler);
        String path2 = getApplicationContext().getPackageResourcePath();
        Log.e("yy","path2="+path2);


        PermissionsUtils.getInstance().checkPermissions(this, permissions, new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {


                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0) { //非系统应用
                        AppInfo tmpInfo = new AppInfo();
                        tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                        tmpInfo.setPackageName(packageInfo.packageName);
                        tmpInfo.setVersionName(packageInfo.versionName);
                        tmpInfo.setVersionCode(packageInfo.versionCode);
                        tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                        appList.add(tmpInfo);
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listrecycler.setLayoutManager(layoutManager);

                // searchRecycler.addItemDecoration(new RecycleViewDivider(SearchActivity.this, DividerItemDecoration.VERTICAL,10, getResources().getColor(R.color.txt_unslct)));
               appListRecyclerAdapter = new AppListRecyclerAdapter(MainActivity.this, appList);
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
                File sd = new File(Environment.getExternalStorageDirectory().getPath()+"/yan");
                ApplicationInfo applicationInfo= null;
                try {
                    applicationInfo = getPackageManager().getApplicationInfo(appList.get(position).getPackageName(), 0);
                    Log.e("yy","applicationInfo="+applicationInfo.sourceDir);
                 copyFile(applicationInfo.sourceDir,sd.getAbsolutePath(),appList.get(position).getAppName()+".apk");

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }




    public void copyFile(String oldPath, String newPath,String name) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件

                if (!newfile.exists()){
                    newfile.mkdirs();
                    newfile.createNewFile();
                }
                File path=new File(newfile.getAbsoluteFile()+"/"+name);
                FileOutputStream fs = new FileOutputStream(path);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                Toast.makeText(MainActivity.this,"导出成功！导出目录为："+path,Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
}
