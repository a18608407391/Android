package com.elder.zcommonmodule.Utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FileUtils {

    public static File writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        File f = makeFilePath(filePath, fileName);
        if (f == null) {
            return new File(filePath + fileName);
        }
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        File file = null;
        try {
            file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
        return file;
    }

    public static String getStorageFile(String id) {
        return FileUtils.getFileContent(new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski" + "/UpLoad_location_File" + id + ".txt"));
    }


    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line = null;
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    fr.close();
                    return sb.toString();
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }


//    private void dumpExceptionToSDCard(Throwable e, File file, String time) throws IOException {
//        //如果SD卡不存在或无法使用，则无法将异常信息写入SD卡
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            if (DEBUG) {
//                Log.w(TAG, "sdcard unmounted,skip dump exception");
//                return;
//            }
//        }
//        File dir = new File(PATH);
//        //如果目录下没有文件夹，就创建文件夹
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        try {
//            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
//            StringWriter sw = new StringWriter();
//            PrintWriter ps = new PrintWriter(sw);
//            //写入时间
//            pw.println(time);
//            //写入手机信息
//            pw.println();//换行
//            e.printStackTrace(pw);
//            e.printStackTrace(ps);
//            String msg = sw.toString();
//            pw.close();//关闭输入流
//        } catch (Exception e1) {
//
//        }
//    }

}
