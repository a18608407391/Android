package com.elder.zcommonmodule.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;


import com.elder.zcommonmodule.ConfigKt;
import com.zk.library.Utils.PreferenceUtils;

import org.cs.tec.library.Base.Utils.UtilsKt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.elder.zcommonmodule.ConfigKt.Point_Save_Path;

public abstract class FileSystem {


    public static boolean writeAsString(String fileName, String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
//        FileWriter fw;
        FileOutputStream fos;
        RandomAccessFile randomFile = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
                return true;
            } else {
                randomFile = new RandomAccessFile(Environment.getExternalStorageDirectory().getPath() + "/Amoski" + Point_Save_Path, "rw");
                // 文件长度，字节数
                long fileLength = randomFile.length();
                // 将写文件指针移到文件尾。
                randomFile.seek(fileLength);
                randomFile.writeBytes(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static String getPhoneBase64(String phone) {
        return new String(Base64.encodeToString(phone.getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));
    }

    public static void unzipFile(String zipFileString, String outPathString) throws IOException {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                Log.e("result", outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    Log.e("result", "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                if (file.getName().endsWith(".jpg")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    MediaStore.Images.Media.insertImage(UtilsKt.getContext().getContentResolver(), bitmap, file.getName(), null);
                }
            }
        }
        inZip.close();

    }

    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d("result", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("result", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d("result", "2ret = " + ret);
            return ret;
        }
        return ret;
    }

    public static boolean writeSingString(String fileName, String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        FileOutputStream fos;
        try {
            if (!file.exists())
                file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readAsString(String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        if (!file.exists())
            return "";
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

        } catch (IOException e) {

        }

        return "";
    }

    public static boolean exists(String fileName) {
        File directory = Environment.getExternalStorageDirectory();
        File file = new File(directory, fileName);
        return file.exists();
    }

    public static boolean writeAsObject(String fileName, Object obj) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        FileOutputStream fos = null;
        ObjectOutputStream ops = null;

        boolean success = false;
        try {
            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            ops = new ObjectOutputStream(fos);
            ops.writeObject(obj);
            ops.flush();

            success = true;

        } catch (IOException e) {
            Log.e("result", e.toString());
        } finally {
            try {
                if (ops != null)
                    ops.close();
                if (ops != null)
                    fos.close();
            } catch (Exception e) {
                Log.e("result", e.toString());
            }
        }

        return success;
    }

    public static Object readAsObject(String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Amoski");
        File file = new File(dir, fileName);
        Log.e("result", file.getPath() + file.length());
        if (!file.exists())
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object result = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            result = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("result", e.toString() + "错误");
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
                Log.e("result", e.getMessage());
            }
        }
        if (result == null) {
            Log.e("result", "是Null");
        }
        return result;
    }


}
