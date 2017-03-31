package com.example.amit.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestTickle;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.VolleyTickle;
import com.vcs.ecp.constant.Constant;
import com.vcs.ecp.dao.DBAdapter;
import com.vcs.ecp.others.DbExportImport;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Utils {

    public static Map<String, String> parameterPost = new HashMap<>();

    public static void printLog(final int which, final String message) {
        if (which == 1) {
            Log.v("VCS", message);
        } else if (which == 2) {
            Log.d("VCS", message);
        } else if (which == 3) {
            Log.i("VCS", message);
        } else if (which == 4) {
            Log.w("VCS", message);
        } else if (which == 5) {
            Log.e("VCS", message);
        }
    }

    public static String getDeviceImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            //return telephonyManager.getDeviceId();
        }
        //return "";

        //return "863580012078942";
        //return "353490062185257";
        //return "355357059583638";
        return "358534047942590";
        //return "357750040963877";
        //return "512710280349426";
        //return "139942933753140";
        //return "911306903002292";
    }

    public static void printLoge(final int which, final String message, final String message1) {
        if (which >= 5) {
            if (which == 11) {
                Log.w(message, message1);
            } else {
                if (message1.length() > 2000) {
                    longInfo(message, message1);
                } else {
                    Log.e(message, message1);
                }
            }
        }
    }

    public static void longInfo(String strq, String str) {
        if (str.length() > 2000) {
            Log.e("" + strq, str.substring(0, 2000));
            longInfo(strq, str.substring(2000));
        } else
            Log.e("" + strq, str);
    }

    public static boolean hasActiveInternetConnection() {

        boolean issend = false;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlc = (HttpURLConnection) (url.openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            Log.e("Utils", "Active internet connection");
            issend = (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e("Utils ret false", "Error checking internet connection", e);
            issend = false;
        }

        return issend;
    }

    public static boolean isNetworkConnected(Context context) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static boolean isSDCardMounted() {
        boolean isMounted = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isMounted = true;
        } else if (Environment.MEDIA_BAD_REMOVAL.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_CHECKING.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_NOFS.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_REMOVED.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_UNMOUNTABLE.equals(state)) {
            isMounted = false;
        } else if (Environment.MEDIA_UNMOUNTED.equals(state)) {
            isMounted = false;
        }

        return isMounted;
    }

    public static boolean isDirectoryExists(final String filePath) {
        //boolean isDirectoryExists = false;
        File mFilePath = new File(filePath);
        /*if (mFilePath.exists()) {
            isDirectoryExists = true;
        } else {
            isDirectoryExists = mFilePath.mkdirs();
        }*/

        //isDirectoryExists = mFilePath.exists() || mFilePath.mkdirs();

        return mFilePath.exists() || mFilePath.mkdirs();
    }

    public static boolean directoryCreated(final String directoryName) {
        boolean isDirectoryCreated;
        if (isSDCardMounted()) {
            final File createdDirectory = new File(directoryName);
            /*if (!createdDirectory.exists()) {
                isDirectoryCreated = createdDirectory.mkdirs();
            } else {
                isDirectoryCreated = true;
            }*/

            isDirectoryCreated = createdDirectory.exists() || createdDirectory.mkdirs();

        } else {
            isDirectoryCreated = false;
        }

        return isDirectoryCreated;
    }

    public static String getDataPath(final String mDirName) {
        String returnedPath = null;
        if (isSDCardMounted()) {
            final String mSDCardDirPath = Environment.getExternalStorageDirectory() + "/"
                    + mDirName;
            if (isDirectoryExists(mSDCardDirPath)) {
                return mSDCardDirPath;
            }
        }

        return returnedPath;
    }

    public static ArrayList<String> returnAllFileNames(final String directoryName) {
        ArrayList<String> fileNameList = new ArrayList<>();
        final File dir = new File(directoryName);
        for (final File imgFile : dir.listFiles()) {
            if (accept(imgFile)) {
                fileNameList.add(imgFile.getName());
            }
        }

        return fileNameList;
    }

    /*public static void deleteFile(final String filePath) {
        //boolean isFileExists = false;
        File mFilePath = new File(filePath);
        if (mFilePath.exists()) {
            mFilePath.delete();
            //isFileExists = true;
        }

        //return isFileExists;
    }*/

    /*public static String removeSpaces(String passedString) {
        passedString = passedString.trim();
        int index;
        String returnedString = "";
        while ((index = passedString.indexOf(" ")) != -1) {
            returnedString += passedString.substring(0, index);
            returnedString += "";
            passedString = passedString.substring(index + 1, passedString.length());
        }
        if (returnedString == "") {
            returnedString = passedString;
        } else {
            returnedString += passedString;
        }

        return returnedString;
    }*/

    /*public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else return false;
    }*/

    /*public static boolean writeToFile(final Context context, final String fileName,
                                      final String fileContent, final String directoryName) {
        boolean isFileCreated = false;
        if (directoryCreated(directoryName)) {
            final String filePath = directoryName + File.separator + fileName;
            try {
                final FileWriter fileWriter = new FileWriter(new File(filePath), false);
                final BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(fileContent);
                writer.newLine();
                writer.flush();
                writer.close();
                isFileCreated = true;
            } catch (IOException e) {
                isFileCreated = false;
            }
        }

        return isFileCreated;
    }*/

    private static boolean accept(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return false;
            }
            String extension = getExtension(file);
            if (extension != null && isAndroidSupported(extension)) {
                return true;
            }
        }

        return false;
    }

    /*public static String returnFileName(final String filePath) {
        String fileName = "";
        final File imgFile = new File(filePath);
        if (accept(imgFile)) {
            fileName = imgFile.getName();
        }

        return fileName;
    }*/

    public static String getExtension(File file) {
        if (file != null) {
            String filename = file.getName();
            int dot = filename.lastIndexOf('.');
            if (dot > 0 && dot < filename.length() - 1)
                return filename.substring(dot + 1).toLowerCase();
        }

        return null;
    }

    public static String getExtension(String filename) {
        if (!filename.equalsIgnoreCase("")) {
            //String filename = file.getName();
            int dot = filename.lastIndexOf('.');
            if (dot > 0 && dot < filename.length() - 1)
                return filename.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    private static boolean isAndroidSupported(final String extension) {
        String PNG = "png";
        String JPG = "jpg";
        String JPEG = "jpeg";
        String PDF = "pdf";
        String BMP = "bmp";
        String GIF = "gif";
        String THREEGP = "3gp";
        String MP4 = "mp4";
        return extension.equalsIgnoreCase(PNG)
                || extension.equalsIgnoreCase(JPG)
                || extension.equalsIgnoreCase(BMP)
                || extension.equalsIgnoreCase(JPEG)
                || extension.equalsIgnoreCase(PDF)
                || extension.equalsIgnoreCase(GIF)
                || extension.equalsIgnoreCase(THREEGP)
                || extension.equalsIgnoreCase(MP4);
    }

    public static boolean isAndroidSupportedEmail(final String extension) {
        String PNG = "png";
        String JPG = "jpg";
        String JPEG = "jpeg";
        String PDF = "pdf";
        String BMP = "bmp";
        String GIF = "gif";
        String THREEGP = "3gp";
        String MP4 = "mp4";
        String doc = "doc";
        String ppt = "ppt";
        String docx = "docx";
        String pptx = "pptx";
        String txt = "txt";
        String apk = "apk";
        return extension.equalsIgnoreCase(PNG)
                || extension.equalsIgnoreCase(JPG)
                || extension.equalsIgnoreCase(BMP)
                || extension.equalsIgnoreCase(JPEG)
                || extension.equalsIgnoreCase(PDF)
                || extension.equalsIgnoreCase(GIF)
                || extension.equalsIgnoreCase(THREEGP)
                || extension.equalsIgnoreCase(doc)
                || extension.equalsIgnoreCase(ppt)
                || extension.equalsIgnoreCase(docx)
                || extension.equalsIgnoreCase(pptx)
                || extension.equalsIgnoreCase(txt)
                || extension.equalsIgnoreCase(apk)
                || extension.equalsIgnoreCase(MP4);
    }

    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    public static boolean isDatabaseCorrupted(Context context) {
        boolean isCorrupted = false;
        String mPath = "/data/data/" + context.getPackageName()
                + "/databases/" + "ecp_mobile";
        File pathFile = new File(mPath);
        if (pathFile.exists()) {
            SQLiteDatabase sqliteDatabase = SQLiteDatabase.openDatabase(mPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Cursor cursor = sqliteDatabase.rawQuery("SELECT MAX(intGlCode) AS intGlCode FROM Login", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                final int count = cursor.getInt(cursor.getColumnIndex("intGlCode"));
                isCorrupted = count <= 0;
            }

            if (cursor != null)
                cursor.close();

            sqliteDatabase.close();
        }

        return isCorrupted;
    }

    public static boolean isSendingDatabaseCorrupted(Context context, final String fileName) {
        boolean isCorrupted = false;
        String mPath = "/data/data/" + context.getPackageName()
                + "/databases/" + fileName;
        File pathFile = new File(mPath);
        if (pathFile.exists()) {
            SQLiteDatabase sqliteDatabase = SQLiteDatabase.openDatabase(mPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Cursor cursor = sqliteDatabase.rawQuery("SELECT MAX(intGlCode) AS intGlCode FROM Login", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                final int count = cursor.getInt(cursor.getColumnIndex("intGlCode"));
                isCorrupted = count <= 0;
            }
            if (cursor != null)
                cursor.close();

            sqliteDatabase.close();
        }

        return isCorrupted;
    }

    @SuppressWarnings("resource")
    public static boolean copyFile(File source, File destination) {
        boolean isCopied = false;
        try {
            FileChannel inChannel = new FileInputStream(source).getChannel();
            FileChannel outChannel = new FileOutputStream(destination).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
            isCopied = true;
        } catch (IOException e) {
            isCopied = false;
        }

        return isCopied;
    }

    public static byte[] convertDatabaseFile(String mPath) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            File pathFile = new File(mPath);
            inputStream = new BufferedInputStream(new FileInputStream(pathFile));
            byteArrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.available() > 0) {
                byteArrayOutputStream.write(inputStream.read());
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream == null ? null : byteArrayOutputStream.toByteArray();
    }

    public static String getToday(final String format) {
        String today = "";
        final Calendar calendar = new GregorianCalendar();
        DateFormat dateFormat = new SimpleDateFormat(format);
        today = dateFormat.format(calendar.getTime());
        return today;
    }

    /*public static void deleteMasteres(String fileName) {
        File dbfile = new File(fileName);
        if (dbfile.exists()) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            db.delete(TableConstants.TABLE_Doctor_Details, null, null);
            db.delete(TableConstants.TABLE_TourPlan_Doctor, null, null);
            db.delete(TableConstants.TABLE_City_Mst, null, null);
            db.delete(TableConstants.TABLE_Employee_Mst, null, null);
            db.delete(TableConstants.TABLE_Division_Mst, null, null);
            db.delete(TableConstants.TABLE_Geo_Mst, null, null);
            db.delete(TableConstants.TABLE_Dr_Category, null, null);
            db.delete(TableConstants.TABLE_Medical_Speciality, null, null);
            db.delete(TableConstants.TABLE_Product_Mst, null, null);
            db.delete(TableConstants.TABLE_Gift_Mst, null, null);
            db.delete(TableConstants.TABLE_SMS_ReasonCode, null, null);
            db.delete(TableConstants.TABLE_Designation_Mst, null, null);
            db.delete(TableConstants.TABLE_Stockist_Mst, null, null);
            db.delete(TableConstants.TABLE_Chemist_Category, null, null);
            db.delete(TableConstants.TABLE_Chemist_Mst, null, null);
            db.delete(TableConstants.TABLE_Visit_Date_Setting, null, null);
            db.delete(TableConstants.TABLE_CallAverageReport, null, null);
            db.delete(TableConstants.TABLE_MSLReport, null, null);
            db.delete(TableConstants.TABLE_Doctor_Visit, null, null);
            db.delete(TableConstants.TABLE_Last_Dr_Visit, null, null);
            db.delete(TableConstants.TABLE_Last_Dr_GiftSample, null, null);
            db.delete(TableConstants.TABLE_Last_Dr_VisitRemarks, null, null);
            db.delete(TableConstants.TABLE_Dr_Support, null, null);
            db.close();
        }
    }*/

    // convert yyyy-MM-dd to dd-MMM-yyyy to show date
    /*public static String getFormat(final String date) {
        String retDate = "";
        try {
            if (!date.equalsIgnoreCase("")) {
                String dates[] = date.split("-");
                // year, month , day
                LocalDate date1 = new LocalDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM");
                String month = formatter.print(date1);
                printLoge(5, "Utils getFormat", "month -->" + month);
                retDate = dates[2] + "-" + month + "-" + dates[0];
            }
        } catch (Exception e) {
            retDate = date;
        }
        printLoge(5, "Utils getFormat", "retDate -->" + retDate);
        return retDate;
    }*/

    public static String getDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String convertedDate = "";
        try {
            Date parseDate = dateFormat.parse(date);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
            convertedDate = fmtOut.format(parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static String GetDateddmmyyyyFormat(Date dat) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //String date = dateFormat.format(dat);
        return dateFormat.format(dat);
    }

    /**
     * check Device Location is Enable or not
     */
    /*public static boolean checkLocationenable(Context con) {

        if (Constant.isGps) {
            // TODO Auto-generated method stub
            LocationManager lm = null;
            boolean gps_enabled = false, network_enabled = false;
            if (lm == null)
                lm = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                Log.e("error--->", "GPS_PROVIDER");
                ex.printStackTrace();
            }
            try {
                network_enabled = lm
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("error--->", "NETWORK_PROVIDER");
            }

            return !(!gps_enabled && !network_enabled);
        } else {
            return true;
        }

    }*/

    /**
     * check Device Location is Enable or not
     */
    /*public static boolean checkLocationEnableCompalsoryNetwork(Context con) {

        if (Constant.isGps) {
            // TODO Auto-generated method stub
            LocationManager lm = null;
            boolean network_enabled = false;
            if (lm == null)
                lm = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);

            try {
                network_enabled = lm
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("error--->", "NETWORK_PROVIDER");
            }

            return network_enabled;
        } else {
            return true;
        }

    }

    *//**
     * check Device Location is Enable or not
     *//*
    public static boolean checkLocationEnableCompalsory(Context con) {

        if (Constant.isGps) {
            // TODO Auto-generated method stub
            LocationManager lm = null;
            boolean gps_enabled = false, network_enabled = false;
            if (lm == null)
                lm = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                Log.e("error--->", "GPS_PROVIDER");
                ex.printStackTrace();
            }
            try {
                network_enabled = lm
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                Log.e("error--->", "NETWORK_PROVIDER");
            }

            return gps_enabled && network_enabled;
        } else {
            return true;
        }

    }*/

    /**
     * get difference between two milliseconds
     */
    public static boolean getDiffOfMillis(long startTime, long endTime) {
        long differenceTime = endTime - startTime;
        long sec = TimeUnit.MILLISECONDS.toSeconds(differenceTime);
        /*long minute = sec / 60;
        Utils.printLoge(5, "differenceTime", "-->" + differenceTime);
        Utils.printLoge(5, "minute", "-->" + minute);
        if (minute <= 5) {
            Utils.printLoge(5, "DONE", "DONE");
        }*/

        int seconds = (int) (differenceTime / 1000) % 60;
        int minutes = (int) ((differenceTime / (1000 * 60)) % 60);
        int minutes1 = (int) ((sec / (1000 * 60)) % 60);
        //int hours = (int) ((differenceTime / (1000 * 60 * 60)) % 24);
        Utils.printLoge(5, "sec", "-->" + sec);
        Utils.printLoge(5, "seconds", "-->" + seconds);
        Utils.printLoge(10, "first method minutes", "-->" + minutes);
        Utils.printLoge(10, "first method minutes1", "-->" + minutes1);
        // insert location
        return minutes <= 3;
    }

    /**
     * get difference between two milliseconds
     */
    public static boolean getDiffOfMillisTEN(long startTime, long endTime) {
        long differenceTime = endTime - startTime;
        long sec = TimeUnit.MILLISECONDS.toSeconds(differenceTime);
        /*long minute = sec / 60;
        Utils.printLoge(5, "differenceTime", "-->" + differenceTime);
        Utils.printLoge(5, "minute", "-->" + minute);
        if (minute <= 5) {
            Utils.printLoge(5, "DONE", "DONE");
        }*/

        int seconds = (int) (differenceTime / 1000) % 60;
        int minutes = (int) ((differenceTime / (1000 * 60)) % 60);
        int minutes1 = (int) ((sec / (1000 * 60)) % 60);
        //int hours = (int) ((differenceTime / (1000 * 60 * 60)) % 24);
        Utils.printLoge(5, "sec", "-->" + sec);
        Utils.printLoge(5, "seconds", "-->" + seconds);
        Utils.printLoge(10, "second method minutes", "-->" + minutes);
        Utils.printLoge(10, "second method minutes1", "-->" + minutes1);
        // insert location
        return minutes < 10;
    }

    /**
     * get difference between two milliseconds
     */
    /*public static boolean getDiffOfErrorReport(long startTime, long endTime) {
        long differenceTime = endTime - startTime;
        long sec = TimeUnit.MILLISECONDS.toSeconds(differenceTime);
        *//*long minute = sec / 60;
        Utils.printLoge(5, "differenceTime", "-->" + differenceTime);
        Utils.printLoge(5, "minute", "-->" + minute);
        if (minute <= 5) {
            Utils.printLoge(5, "DONE", "DONE");
        }*//*

        int seconds = (int) (differenceTime / 1000) % 60;
        int minutes = (int) ((differenceTime / (1000 * 60)) % 60);
        int minutes1 = (int) ((sec / (1000 * 60)) % 60);
        //int hours = (int) ((differenceTime / (1000 * 60 * 60)) % 24);
        Utils.printLoge(5, "sec", "-->" + sec);
        Utils.printLoge(5, "seconds", "-->" + seconds);
        Utils.printLoge(5, "minutes", "-->" + minutes);
        Utils.printLoge(5, "minutes1", "-->" + minutes1);
        if (minutes <= 2) {
            // insert location
            return true;
        } else {
            return false;
        }
    }*/
    public static String CallHttpMethod(Context context, String URL, String Parameters) {

        String result = "";
        try {
            /*HttpPost httpPost = new HttpPost(URL);
            StringEntity stringEntity = new StringEntity(Parameters);
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, SyncConstant.WEB_SERVICE_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, SyncConstant.WEB_SERVICE_TIMEOUT);
            DefaultHttpClient client = new DefaultHttpClient(httpParameters);
            BasicHttpResponse response = (BasicHttpResponse) client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity.getContentLength() > 0) {
                BufferedReader bufferReader = null;
                InputStream inputStream = entity.getContent();
                bufferReader = new BufferedReader(new InputStreamReader(inputStream), 8192);
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                result = stringBuilder.toString();
                Utils.printLoge(5, "CallHttpMethod result--->", "-->" + result);
            }*/

            String finaUrl = URL + "?" + Parameters;

            //String URL = "http://172.16.8.150/ECPMobileWebService_Ver201/ecpMobileToWebSync.asmx/Get_DrAnniBirthDetails?" + param1;
            printLoge(10, "finaUrl", "--->" + finaUrl);

            RequestTickle mRequestTickle = VolleyTickle.newRequestTickle(context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, finaUrl, null, null);
            mRequestTickle.add(stringRequest);
            NetworkResponse response = mRequestTickle.start();
            Log.e("TIME END", "" + System.currentTimeMillis());
            if (response.statusCode == 200) {
                result = VolleyTickle.parseResponse(response);
                printLoge(10, "responce", " " + result);
            } else {
                printLoge(10, "no responce", "no responce");
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }

        return result;
    }

    public static String CallHttpMethodPost(Context context, String URL, String Parameters) {

        String result = "";
        try {


            //String finaUrl = URL + "?" + Parameters;

            //String URL = "http://172.16.8.150/ECPMobileWebService_Ver201/ecpMobileToWebSync.asmx/Get_DrAnniBirthDetails?" + param1;
            printLoge(10, "finaUrl", "--->" + URL);
            if (Utils.parameterPost.size() > 0) {
                // for (int i = 0; i < Utils.parameterPost.size(); i++)
                //printLoge(10, "pera", "--->" + Utils.parameterPost.g);
                //String value = (String) newMap.get("my_code");
                /*printLoge(10, "pera", "--->" + Utils.parameterPost.get("fk_EmpGLCode_Login"));
                printLoge(10, "pera", "--->" + Utils.parameterPost.get("varClientName"));
                printLoge(10, "pera", "--->" + Utils.parameterPost.get("fk_DrGlCode"));
                printLoge(10, "pera", "--->" + Utils.parameterPost.get("varLongitude"));
                printLoge(10, "pera", "--->" + Utils.parameterPost.get("varLatitude"));*/

                for (Map.Entry<String, String> entry : Utils.parameterPost.entrySet()) {
                    String key = entry.getKey();
                    String tab = entry.getValue();
                    // do something with key and/or tab
                    printLoge(10, "pera", "--->" + key + " - " + tab);

                }
            }
            RequestTickle mRequestTickle = VolleyTickle.newRequestTickle(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, null, null) {

                protected Map<String, String> getParams() throws com.android.volley.error.AuthFailureError {
                    /*Map<String, String> params = new HashMap<>();
                    params.put("param1", num1);
                    params.put("param2", num2);
                    return params;*/

                    if (Utils.parameterPost.size() > 0) {
                        return Utils.parameterPost;
                    } else {
                        Utils.printLoge(5, "noperameters jsonTask....", "............" + Utils.parameterPost);
                        return null;
                    }
                }
            };
            mRequestTickle.add(stringRequest);
            NetworkResponse response = mRequestTickle.start();
            Log.e("TIME END", "" + System.currentTimeMillis());
            if (response.statusCode == 200) {
                result = VolleyTickle.parseResponse(response);
                printLoge(10, "responce", " " + result);
            } else {
                printLoge(10, "no responce", "no responce");
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }

        return result;
    }

    public static String checkRemarks(String remarks) {
        if (!remarks.equalsIgnoreCase("")) {
            remarks = remarks.replace("'", " ");
            remarks = remarks.replace('"', ' ');
            remarks = remarks.replace('/', ' ');
            remarks = remarks.replace('&', ' ');
            /*remarks = remarks.replace('}', ' ');
            remarks = remarks.replace(',', ' ');
            remarks = remarks.replace('[', ' ');
            remarks = remarks.replace(']', ' ');
            remarks = remarks.replace(';', ' ');
            remarks = remarks.replace(':', ' ');*/
        }
        return remarks;
    }

    public static BigDecimal round(Double num, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(num));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        Utils.printLoge(5, "after BigDecimal decQuantity", "-->" + bd);
        return bd;
        //double number = num;

        // long number = Math.round(num * 100);
        //return number / 100;
    }

    /*public static void checkExternalStorage(int currentSdk) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = 0;
        long totalSize = 0;
        long availableSize = 0;
        long freeSize = 0;
        if (currentSdk >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSize();
            totalSize = statFs.getBlockCount() * blockSize;
            availableSize = statFs.getAvailableBlocks() * blockSize;
            freeSize = statFs.getFreeBlocks() * blockSize;
        } else {
            blockSize = statFs.getBlockSizeLong();
            totalSize = statFs.getBlockCountLong() * blockSize;
            availableSize = statFs.getAvailableBlocks() * blockSize;
            freeSize = statFs.getFreeBlocksLong() * blockSize;
        }
    }*/

    /**
     * get difference between two milliseconds
     */
    /*public static int getDiffOfMillisSync(long startTime, long endTime) {
        long differenceTime = endTime - startTime;
        //long sec = TimeUnit.MILLISECONDS.toSeconds(differenceTime);
        //int seconds = (int) (differenceTime / 1000) % 60;
        //int minutes = (int) ((differenceTime / (1000 * 60)) % 60);
        //int minutes1 = (int) ((sec / (1000 * 60)) % 60);
        if (differenceTime < 0) {
            Constant.isTimeMinus = true;
        }
        int hours = (int) ((differenceTime / (1000 * 60 * 60)) % 24);
        //Utils.printLoge(5, "sec", "-->" + sec);
        ///Utils.printLoge(5, "seconds", "-->" + seconds);
        //Utils.printLoge(5, "minutes", "-->" + minutes);
        //Utils.printLoge(5, "minutes1", "-->" + minutes1);
        *//*if (days <= 7) {
            // insert location
            return true;
        } else {
            return false;
        }*//*
        return (int) (differenceTime
                / (1000 * 60 * 60 * 24));
    }

    // HTTP call method any other methods are depricated with Android API 22
    *//*public static String excutePost(String targetURL, String urlParameters) {

        //Log.e("targetURL", "--->" + targetURL);
        //Log.e("urlParameters", "--->" + urlParameters);

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }*//*

    public static void writetoLog(String text) {
        try {
            File myFile = new File(Constant.ECTERNAL_STORAGE_APP_ROOT + "/MultiDivLog.txt");
            if (!myFile.exists())
                myFile.createNewFile();

            FileWriter wrte = new FileWriter(myFile, true);
            //OutputStreamWriter wrte = new OutputStreamWriter(fout);
            wrte.append("\n").append(text);
            wrte.close();
            //fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Initiates the fetch operation.
     */
    public static String loadFromNetwork(String urlString, String perameters, boolean isPOST) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString, perameters, isPOST);
            if (stream != null)
                str = readIt(stream, 500);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * HTTP new methods
     *
     * */

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     *
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    public static InputStream downloadUrl(String urlString, String perameters, boolean isPost) throws IOException {
        try {
            URL url = null;
            if (isPost) {
                url = new URL(urlString);
            } else {
                url = new URL(urlString + "?" + perameters);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            if (isPost) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestProperty("Content-Length",
                        Integer.toString(perameters.length()));
            } else {
                conn.setRequestMethod("GET");
            }
        /*conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setRequestProperty("Content-Length",
                Integer.toString(perameters.length()));*/
            conn.setDoInput(true);
            // Start the query
            conn.connect();
            return conn.getInputStream();
        } catch (Exception e) {
            Log.e("error utils", "InputStream downloadUrl");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream containing HTML from targeted site.
     * @param len    Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        //Reader reader = null;
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    public static String post(String endpoint, String params)
            throws IOException {
        String str = "";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        //params.put("key", key);
        /*StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }*/
        //String body = bodyBuilder.toString();
        Log.e("ECP POST", "Posting '" + params + "' to " + url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setChunkedStreamingMode(0);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setRequestProperty("Content-Length",
                    Integer.toString(params.length()));
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(params.getBytes());
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                InputStream stream = conn.getInputStream();
                str = readIt(stream, 500);
                if (stream != null)
                    stream.close();
                return str;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String appendZeroDay(int selectedDay) {
        String day = "";
        if (selectedDay < 10) {
            day = "0" + String.valueOf(selectedDay);
        } else {
            day = String.valueOf(selectedDay);
        }
        return day;
    }

    /*public static File takeScreenshot(View v1) {
        File imageFile = null;
        //Date now = new Date();
        //android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String dir = Constant.ECTERNAL_STORAGE_APP_ROOT1;
            File dirF = new File(dir);
            if (!dirF.exists()) {
                dirF.mkdirs();
            }

            String mPath = dir + "/ECP_SCREEN_" + System.currentTimeMillis() + ".jpg";

            // create bitmap screen capture
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
            //fileName = imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            imageFile = null;
            e.printStackTrace();
        }

        return imageFile;
    }*/

    public static String monthName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";

        }
        return null;
    }

    /*public static boolean checkDate(String cDate) {
        Log.e("checkDate", "-->" + cDate);
        boolean isRetValue = true;
        try {
            if (!cDate.equalsIgnoreCase("")) {

                String dateA[] = cDate.split("-");

                DateTime ds = new DateTime();

                String selectedDate = dateA[0] + "-" + dateA[1] + "-" + dateA[2];
                String currentDate = ds.getYear() + "-" + Utils.appendZeroDay(ds.getMonthOfYear()) + "-" + Utils.appendZeroDay(ds.getDayOfMonth());

                Utils.printLoge(5, "checkDate", "selectedDate-->" + selectedDate + " currentDate -->" + currentDate);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                *//*Date date1 = sdf.parse("2009-12-31");
                Date date2 = sdf.parse("2010-01-31");*//*
                Date date1 = sdf.parse(selectedDate);
                Date date2 = sdf.parse(currentDate);

                System.out.println(sdf.format(date1));
                System.out.println(sdf.format(date2));

                if (date1.compareTo(date2) > 0) {
                    //isCurrentDate = false;
                    isRetValue = false;
                    Utils.printLoge(5, "selectedDate is after currentDate", "selectedDate is after currentDate");
                } else if (date1.compareTo(date2) < 0) {
                    //isCurrentDate = false;
                    isRetValue = true;
                    Utils.printLoge(5, "selectedDate is before currentDate", "selectedDate is before currentDate");
                } else if (date1.compareTo(date2) == 0) {
                    //isCurrentDate = true;
                    isRetValue = true;
                    Utils.printLoge(5, "selectedDate is equal to currentDate", "selectedDate is equal to currentDate");
                } else {
                    Utils.printLoge(5, "How to get here?", "How to get here?");
                }
            } else {
                //isCurrentDate = false;
                isRetValue = false;
            }

        } catch (Exception ex) {
            isRetValue = false;
            //isCurrentDate = false;
            //Utils.printLoge(5, "error checkDate", "--->" + ex.getMessage().toString());
            ex.printStackTrace();
        }

        return isRetValue;
    }


    *//**
     * for visit POB dates in POB,chemist,Doctor
     *//*
    public static boolean checkDateNew(String cDate) {
        Log.e("checkDate", "-->" + cDate);
        boolean isRetValue = true;
        try {
            if (!cDate.equalsIgnoreCase("")) {

                String dateA[] = cDate.split("-");

                DateTime ds = new DateTime();

                String selectedDate = dateA[0] + "-" + dateA[1] + "-" + dateA[2];
                String currentDate = ds.getYear() + "-" + Utils.appendZeroDay(ds.getMonthOfYear()) + "-" + Utils.appendZeroDay(ds.getDayOfMonth());

                Utils.printLoge(5, "checkDate", "selectedDate-->" + selectedDate + " currentDate -->" + currentDate);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                *//*Date date1 = sdf.parse("2009-12-31");
                Date date2 = sdf.parse("2010-01-31");*//*
                Date date1 = sdf.parse(selectedDate);
                Date date2 = sdf.parse(currentDate);

                System.out.println(sdf.format(date1));
                System.out.println(sdf.format(date2));

                if (date1.compareTo(date2) > 0) {
                    //isCurrentDate = false;
                    isRetValue = true;
                    Utils.printLoge(5, "selectedDate is after currentDate", "selectedDate is after currentDate");
                } else if (date1.compareTo(date2) < 0) {
                    //isCurrentDate = false;
                    isRetValue = false;
                    Utils.printLoge(5, "selectedDate is before currentDate", "selectedDate is before currentDate");
                } else if (date1.compareTo(date2) == 0) {
                    //isCurrentDate = true;
                    isRetValue = true;
                    Utils.printLoge(5, "selectedDate is equal to currentDate", "selectedDate is equal to currentDate");
                } else {
                    Utils.printLoge(5, "How to get here?", "How to get here?");
                }
            } else {
                //isCurrentDate = false;
                isRetValue = false;
            }

        } catch (Exception ex) {
            isRetValue = false;
            //isCurrentDate = false;
            //Utils.printLoge(5, "error checkDate", "--->" + ex.getMessage().toString());
            ex.printStackTrace();
        }

        return isRetValue;
    }

    public static boolean checkDataBase(Context context) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DbExportImport.DATA_DIRECTORY_DATABASE_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
            if (checkDB != null) {
                //DBAdapter db = new DBAdapter(context);
                //db.open();
                //int loginCount = db.getCountLogin();
                //db.close();
                if (loginCount <= 0) {
                    checkDB = null;
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            // database doesn't exist yet.
            //Utils.printLoge(5, "erro location service 3", "DB BOT FOUND -->" + e.getMessage().toString());
        }
        return checkDB != null;
    }

    public static String getTodaysDate() {
        Calendar currentDate = Calendar.getInstance(); //Get the current date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy "); //format it as per your requirement

        return formatter.format(currentDate.getTime());
    }

    public static void ExportDb() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.vcs.ecp//databases//" + DBAdapter.DATABASE_NAME;
                String backupDBPath = DBAdapter.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static String getDoubleDigits(int passedDay) {
        String returnString = "";
        if (passedDay > 0 && passedDay < 10) {
            switch (passedDay) {
                case 1:
                    returnString = "01";
                    break;
                case 2:
                    returnString = "02";
                    break;
                case 3:
                    returnString = "03";
                    break;
                case 4:
                    returnString = "04";
                    break;
                case 5:
                    returnString = "05";
                    break;
                case 6:
                    returnString = "06";
                    break;
                case 7:
                    returnString = "07";
                    break;
                case 8:
                    returnString = "08";
                    break;
                case 9:
                    returnString = "09";
                    break;
            }
        } else {
            returnString = String.valueOf(passedDay);
        }

        return returnString;
    }
}