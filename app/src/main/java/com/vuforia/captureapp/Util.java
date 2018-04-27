package com.vuforia.captureapp;

import android.os.*;
import com.vuforia.captureapp.model.*;
import java.util.*;
import java.io.*;

public class Util
{
    private static void copy(final File file, final File file2) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final FileOutputStream fileOutputStream = new FileOutputStream(file2);
        final byte[] array = new byte[1024];
        while (true) {
            final int read = fileInputStream.read(array);
            if (read <= 0) {
                break;
            }
            fileOutputStream.write(array, 0, read);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public static void deleteFileRecursive(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            for (int length = listFiles.length, i = 0; i < length; ++i) {
                deleteFileRecursive(listFiles[i]);
            }
        }
        file.delete();
    }

    public static String getAppDataCaptureDirectory() {
        return getAppDataRootDirectory() + "/ObjectReco";
    }

    public static String getAppDataMetadataDirectory() {
        return getAppDataRootDirectory() + "/metadata";
    }

    public static String getAppDataRootDirectory() {
        return Environment.getExternalStorageDirectory().getPath() + "/VuforiaObjectScanner";
    }

    public static CaptureInformation getCaptureInformation(final String s) {
        final File captureMetadataDirectory = getCaptureMetadataDirectory(s);
        final Properties properties = new Properties();
        final File file = new File(captureMetadataDirectory, "info.properties");
        if (!file.exists()) {
            return null;
        }
        try {
            properties.load(new FileInputStream(file));
            return new CaptureInformation(new File(captureMetadataDirectory, "capture.jpg").getAbsolutePath(), captureMetadataDirectory.getName(), new Date(file.lastModified()), properties);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static File getCaptureMetadataDirectory(final String s) {
        return new File(getAppDataMetadataDirectory() + "/" + s);
    }

    public static File getCaptureShareDirectory() {
        return new File(getAppDataRootDirectory() + "/share");
    }

    public static String getMarkerCatureName() {
        final String s = null;
        try {
            final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/VuforiaObjectScanner/current.txt");
            String property = s;
            if (file.exists()) {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final Properties properties = new Properties();
                properties.load(fileInputStream);
                fileInputStream.close();
                file.delete();
                property = properties.getProperty("captureName");
            }
            return property;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return s;
        }
    }

    public static File getOdFile(final String s) {
        return new File(getAppDataCaptureDirectory() + "/" + s + ".od");
    }

    public static Date prepareMetadataDirectory(final String s, int i, final float n, final int[] array) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        final File file = new File(path + "/VuforiaObjectScanner/capture.jpg");
        final File file2 = new File(path + "/VuforiaObjectScanner/current.txt");
        final File file3 = new File(path + "/VuforiaObjectScanner/metadata/" + s);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        final File file4 = new File(file3, "info.properties");
        try {
            final File file5 = new File(file3, "capture.jpg");
            if (!file5.exists()) {
                copy(file, file5);
            }
            final Properties properties = new Properties();
            if (file4.exists()) {
                properties.load(new FileInputStream(file4));
            }
            if (i > 0) {
                properties.setProperty("nbPoints", "" + i);
            }
            if (n > 0.0f) {
                properties.setProperty("mFileSize", "" + n);
            }
            if (array.length > 0) {
                i = 0;
                int n2;
                for (int j = 0; j < array.length; ++j, i = n2) {
                    n2 = i;
                    if (array[j] > 0) {
                        n2 = i + 1;
                    }
                }
                if (i > 0) {
                    for (i = 0; i < array.length; ++i) {
                        properties.setProperty("facet" + i, "" + array[i]);
                    }
                }
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(file4);
            properties.store(fileOutputStream, "ObjectReco");
            fileOutputStream.flush();
            fileOutputStream.close();
            final FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
            final Properties properties2 = new Properties();
            properties2.setProperty("captureName", s);
            properties2.store(fileOutputStream2, "marker");
            fileOutputStream2.flush();
            fileOutputStream2.close();
            return new Date(file4.lastModified());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new Date(file4.lastModified());
        }
    }
}
