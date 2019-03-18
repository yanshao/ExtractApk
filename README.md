一、概述

        现在绝大多数手机apk在安装完成后，会主动删除对应的apk安装包，大家在安装完成后想把这个apk分享给其他人
        还需要在去找到对应的下载链接才可以分享给亲戚朋友。
        此项目的主要功能就是将安装过后的文件导出成apk 另存在手机目录，这样大家就可以分享apk给任何人了。


![Alt text](/7a45ddf779cd2576fda8997d50c3789.jpg)
![Alt text](/4b29023f124b7f023320f9b24960ebf.jpg)
![Alt text](/04addc33d3eedcf142c014774db5641.jpg)

二、实现思路

      大家都知道apk安装后会生成几个目录

       1. /data/data/ 包名      目录

        2./data/app/包名    目录

目录1为 对应app的缓存及数据库存储路径，目录2下有资源及名称为base.apk的文件

上面提到的base.apk是可以安装的该应用的备份apk包  ，所以我们需要做的就是根据包名找到/data/app  目录  
并将其目录下的base.apk复制一份到我们指定的目录即可

（1）获取手机中所有安装的app


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
（2） 根据包名复制data/app 下的base.apk到指定目录


                File sd = new File(Environment.getExternalStorageDirectory().getPath()+"/yan");
                ApplicationInfo applicationInfo= null;
                try {
                    applicationInfo = getPackageManager().getApplicationInfo(appList.get(position).getPackageName(), 0);
                    Log.e("yy","applicationInfo="+applicationInfo.sourceDir);
                 copyFile(applicationInfo.sourceDir,sd.getAbsolutePath(),appList.get(position).getAppName()+".apk");

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
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
                
