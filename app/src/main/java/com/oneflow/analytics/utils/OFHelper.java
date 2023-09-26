/*
 *  Copyright 2021 1Flow, Inc.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.oneflow.analytics.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.ColorUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oneflow.analytics.BuildConfig;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OFHelper {

    public static boolean commanLogEnable = false;
    static boolean builds = false;

    public static String headerKey = "";

    public static final Random random = new Random();

    private OFHelper() {
    }

    public static String getJSONValues(String contents) {
        StringBuilder sb = new StringBuilder();
        try {

            JSONObject jsonObject = new JSONObject(contents.trim());
            Iterator<String> keys = jsonObject.keys();


            while (keys.hasNext()) {
                String key = keys.next();

                sb.append(jsonObject.getString(key));

            }
        } catch (JSONException j) {
            // error
        }

        return sb.toString();
    }

    public static String mongoObjectId() {
        String time = Long.toHexString(Calendar.getInstance().getTimeInMillis()/1000);

        String machine = String.format("%06d", random.nextInt(999999 - 100000 + 1) + 100000);
        String pid = String.format("%04d", random.nextInt(9999 - 1000 + 1) + 1000);
        String counter = String.format("%06d", random.nextInt(999999 - 100000 + 1) + 100000);
        return time + machine + pid + counter;
    }

    static int printCharLimit = 4000;


    //Log methods
    public static void v(String tag, String msg) {

        int printRange = 4000;

        if (commanLogEnable) {

            if (msg.length() > printRange) {
                long range = msg.length() / printRange;
                for (int k = 0; k <= range; k++) {
                    if (k == range) {
                        Log.v(tag, "continueLast::" + msg.substring((k * printRange)));
                        break;
                    }
                    Log.v(tag, "continue[" + k + "]::" + msg.substring((k * printRange), (k * printRange) + printRange) + "]");
                }
            } else {
                Log.v(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg, boolean shouldPrint) {
        if (commanLogEnable) {
            if (msg.length() > 4075) {
                Log.d(tag, msg.substring(0, 4075));
                Log.d("continue", msg.substring(4076, msg.length()));
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg, boolean shouldPrint) {
        if (commanLogEnable) {
            if (msg.length() > 4075) {
                Log.i(tag, msg.substring(0, 4075));
                Log.i("continue", msg.substring(4076, msg.length()));
            } else {
                Log.i(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (commanLogEnable) {
            if (msg.length() > 4075) {
                Log.e(tag, msg.substring(0, 4075));
                Log.e("continue", msg.substring(4076, msg.length()));
            } else {
                Log.e(tag, msg);
            }
        }
    }

    /**
     * Toast with app theme
     * .show() function call not required
     */
    public static void makeText(Context context, String msg, int duration) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.oneflow_sdk_toast, null);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();

    }


    public static String formatedDate(long milisec, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(milisec));
    }

    public static String validateString(String str) {
        if (str == null) {
            return "NA";
        }
        str = str.trim();

        if (str.isEmpty() || str.length() == 0) {
            return "NA";
        }
        return str;
    }


    public static String getDeviceId(Context context) {
        String deviceId = "";


        // KAI suggested to use random key 19-feb-2022

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(context);
        deviceId = shp.getStringValue(OFConstants.SHP_DEVICE_UNIQUE_ID);

        if (deviceId.equalsIgnoreCase("NA")) {
            deviceId = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
            shp.storeValue(OFConstants.SHP_DEVICE_UNIQUE_ID, deviceId);
        }
        return deviceId;
    }


    public static void hideKeyboard(Activity mActivity, EditText edt) {
        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity mActivity, EditText edt) {
        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(edt, 0);
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();

            if (netInfo != null) {
                return netInfo.isConnected();
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static void showAlert1(Context context, String message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());

        dialog.show();
    }

    public static void showAlert(Context context, String titleStr, String message) {
        showAlert1(context, titleStr, message, false);
    }

    public static void showAlert1(final Context context, String titleStr, String message,
                                  final boolean shouldClose) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_dialog);
        OFCustomTextView title = (OFCustomTextView) dialog.findViewById(R.id.selected_title);
        OFCustomTextView msg = (OFCustomTextView) dialog.findViewById(R.id.response_msg);
        OFCustomTextView okBtn = (OFCustomTextView) dialog.findViewById(R.id.submit_btn);
        okBtn.setOnClickListener(v -> {
            dialog.cancel();
            if (shouldClose) {
                ((Activity) context).finish();
            }
        });
        msg.setText(message);
        title.setText(titleStr);

        dialog.show();
    }

    public static void showAlertWithIntent(final Context context, String titleStr, String
            message, final boolean shouldClose, final Intent intent) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_dialog);
        OFCustomTextView title = (OFCustomTextView) dialog.findViewById(R.id.selected_title);
        OFCustomTextView msg = (OFCustomTextView) dialog.findViewById(R.id.response_msg);
        OFCustomTextView okBtn = (OFCustomTextView) dialog.findViewById(R.id.submit_btn);
        okBtn.setOnClickListener(v -> {
            dialog.cancel();
            if (shouldClose) {
                ((Activity) context).startActivity(intent);
                ((Activity) context).finish();
            }
        });
        msg.setText(message);
        title.setText(titleStr);

        dialog.show();
    }

    public static void showAlertWithIntent2(final Context context, String titleStr, String
            message, final boolean shouldClose, final Intent intent) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_dialog);
        OFCustomTextView title = (OFCustomTextView) dialog.findViewById(R.id.selected_title);
        OFCustomTextView msg = (OFCustomTextView) dialog.findViewById(R.id.response_msg);
        OFCustomTextView okBtn = (OFCustomTextView) dialog.findViewById(R.id.submit_btn);

        dialog.setCancelable(false);

        okBtn.setOnClickListener(v -> {
            dialog.cancel();
            if (shouldClose) {
                ((Activity) context).startActivity(intent);
                ((Activity) context).finish();
            }
        });
        msg.setText(message);
        title.setText(titleStr);

        dialog.show();
    }


    public static void showAlertWithCancelListener(final Context context, String
            titleStr, String message, final boolean shouldClose, DialogInterface.
                                                           OnCancelListener cancelListener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_dialog);
        OFCustomTextView title = (OFCustomTextView) dialog.findViewById(R.id.selected_title);
        OFCustomTextView msg = (OFCustomTextView) dialog.findViewById(R.id.response_msg);
        OFCustomTextView okBtn = (OFCustomTextView) dialog.findViewById(R.id.submit_btn);

        dialog.setOnCancelListener(cancelListener);
        okBtn.setOnClickListener(v -> {
            dialog.cancel();
            if (shouldClose) {
                ((Activity) context).finish();
            }
        });
        msg.setText(message);
        title.setText(titleStr);
        dialog.show();
    }

    public static void showAlertWithCancelListener2(final Context context, String
            titleStr, String message, final boolean shouldClose, DialogInterface.
                                                            OnCancelListener cancelListener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_alert_dialog);
        OFCustomTextView title = (OFCustomTextView) dialog.findViewById(R.id.selected_title);
        OFCustomTextView msg = (OFCustomTextView) dialog.findViewById(R.id.response_msg);
        OFCustomTextView okBtn = (OFCustomTextView) dialog.findViewById(R.id.submit_btn);

        dialog.setCancelable(false);
        dialog.setOnCancelListener(cancelListener);
        okBtn.setOnClickListener(v -> {
            dialog.cancel();
            if (shouldClose) {
                ((Activity) context).finish();
            }
        });
        msg.setText(message);
        title.setText(titleStr);
        dialog.show();
    }

    public static boolean validateEmail(String email) {
        final String EMAIL_REGEX = "^(.+)@(.+)$";

        Pattern pattern;

        Matcher matcher;

        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static String validateStringeReturnEmpty(String str) {
        if (str == null) {
            return "";
        }
        str = str.trim();

        if (str.isEmpty() || str.length() == 0) {
            return "";
        }
        return str;
    }

    public static String maskString(int startMask, int endMask, String inputString) {
        String outputString = "";


        int total = inputString.length();
        int masklen = total - (startMask + endMask);
        StringBuffer maskedbuf = new StringBuffer(inputString.substring(0, startMask));
        for (int i = 0; i < masklen; i++) {
            maskedbuf.append('X');
        }

        maskedbuf.append(inputString.substring(startMask + masklen, total));
        outputString = maskedbuf.toString();

        return outputString;
    }


    /**
     * Function to convert string to title case
     *
     * @param string - Passed string
     */
    public static String toTitleCase(String string) {

        // Check if String is null
        if (string == null) {

            return null;
        }

        boolean whiteSpace = true;

        StringBuilder builder = new StringBuilder(string); // String builder to store string
        final int builderLength = builder.length();

        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {

            char c = builder.charAt(i); // Get character at builders position

            if (whiteSpace) {

                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    whiteSpace = false;
                }
            } else if (Character.isWhitespace(c)) {

                whiteSpace = true; // Set character is white space

            } else {

                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }

    public static Date convertStringToDate(String dateInString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format.parse(dateInString);

        } catch (ParseException e) {
            // error
        }
        return date;
    }

    public static String formatDateIntoCustomFormat(Date dateValue, String format) {
        String formattedDate = "";
        try {
            Calendar cl = Calendar.getInstance();
            cl.setTime(dateValue);
            SimpleDateFormat sdfNewThemeDate = new SimpleDateFormat(format);
            formattedDate = sdfNewThemeDate.format(dateValue);
        } catch (Exception e) {
            // error
        }

        return formattedDate;
    }


    static StringBuilder sb;
    static int counter = 1;

    public static String getJSONAllValues(String jsonRaw) {
        try {

            JSONObject outerMost = new JSONObject(jsonRaw);
            JSONArray jName = outerMost.names();


            for (int i = 0; i < jName.length(); i++) {

                if (outerMost.get(jName.get(i).toString()) instanceof JSONObject) {

                    getJSONAllValues(outerMost.getString(jName.get(i).toString()));

                } else if (outerMost.get(jName.get(i).toString()) instanceof JSONArray) {

                    JSONArray innerJson = outerMost.getJSONArray(jName.get(i).toString());
                    for (int j = 0; j < innerJson.length(); j++) {
                        if (innerJson.get(j) instanceof JSONObject) {
                            getJSONAllValues(innerJson.getString(j));
                        } else {
                            sb.append((counter++) + ". " + innerJson.getString(j) + "\n");
                        }
                    }

                } else {
                    sb.append((counter++) + ". " + outerMost.getString(jName.get(i).toString()) + "\n");
                }

            }

        } catch (JSONException je) {
            je.printStackTrace();
        }
        return sb.toString();
    }
    public static File createLogFile() {
        File fl = null;
        try {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + "OneFlowLog" + File.separator + "log.txt";

            fl = new File(filePath);

            if (!fl.exists()) {
                File folderOuter = new File(Environment.getExternalStorageDirectory(), "OneFlowLog");
                folderOuter.mkdir();

                if (folderOuter.exists()) {
                    File logFile = new File(folderOuter, "log.txt");
                    if(!logFile.createNewFile()){
                        // failed
                    }
                    fl = logFile;
                }
            }
        } catch (Exception ue) {
            // error
        }
        return fl;
    }

    public static String writeLogToFile(String body) {

        try {

            String writeText = formatedDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss:SSS") + " ===> " + body;
            if (!BuildConfig.DEBUG)
                return "";
            File fl = createLogFile();
            if (fl != null && fl.exists()) {

                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(fl, true)  //Set true for append mode
                )) {
                    writer.newLine();   //Add new line
                    writer.write(writeText);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }



    public static double[] getScreenSize(Activity context) {

        double[] data = new double[3];
        DisplayMetrics dm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        data[0] = wi;
        data[1] = hi;
        data[2] = screenInches;

        return data;
    }

    /**
     * This method will accept HashMap and check for date object if found will convert date object to timestamp (in seconds) and return changed HashMap
     *
     * @param map
     * @return
     */
    public static Map<String, Object> checkDateInHashMap(Map<String, Object> map) {

        Gson gson = new Gson();
        gson.toJson(map);

        for (String key : map.keySet()) {
            if (map.get(key) instanceof Date || map.get(key) instanceof java.sql.Date) {
                Date dt = (Date) map.get(key);
                map.put(key, dt.getTime() / 1000);
            }
        }
        return map;
    }

    public static String getAlphaHexColor(String color, int per) {
        String mainColor = "";
        String hex = "";
        String returnColor = "";
        if (color.length() > 0) {
            mainColor = color.substring(color.length() - 6);

            hex = Integer.toHexString(getAlphaNumber(per)).toUpperCase();

            returnColor = "#" + hex + mainColor.toUpperCase();
        } else {
            returnColor = "NA";
        }
        return returnColor;
    }

    public static String handlerColor(String color) {
        String colorNew = "";
        try {

            String tranparancy = "";

            if (color.startsWith("#")) {
                if (color.length() > 7) {
                    tranparancy = color.substring(7, 8);
                }
            } else {
                if (color.length() > 6) {
                    tranparancy = color.substring(6, 8);
                }
            }

            String tempColor;
            if (!color.startsWith("#")) {
                tempColor = color.substring(0, 6);
            } else {
                tempColor = color.substring(1, 7);
            }

            colorNew = "#" + tranparancy + tempColor;
        } catch (Exception ex) {
            // error
        }


        return colorNew;
    }

    public static int getAlphaNumber(int percentage) {
        return Math.round((float)(255 * percentage) / 100);
    }

    public static int manipulateColor(int color, float factor) {
        factor = 1.0f - factor;

        return ColorUtils.blendARGB(color, Color.WHITE, factor);
    }

    public static int manipulateColorNew(int color, int factor) {

        return ColorUtils.setAlphaComponent(color, factor);
    }


    public static int lighten(int color, double fraction) {

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = lightenColor(red, fraction);
        green = lightenColor(green, fraction);
        blue = lightenColor(blue, fraction);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    private static int lightenColor(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }
}

