/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apricot.cleanmaster.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract device information.
 * <p/>
 * Require permissions:
 * android.permission.READ_PHONE_STATE
 * android.permission.CAMERA
 * android.permission.ACCESS_NETWORK_STATE
 * android.permission.ACCESS_WIFI_STATE
 * <p/>
 * <p/>
 * Created by hhhung on September 4, 2015
 */
public class PhoneInfoUtil {
    private Context mContext;
    private WindowManager mWindowManager;
    private TelephonyManager mTelephonyManager;
    private ActivityManager mActivityManager;
    private WifiManager mWifiManager;
    private ActivityManager.MemoryInfo mMemoryInfo = new ActivityManager.MemoryInfo();

    public PhoneInfoUtil(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        }

        return Utils.readFile("/sys/class/net/" + interfaceName + "/address").toUpperCase().trim();
    }

    /**
     * Return back camera number of megapixels
     */
    public float getBackCameraMegapixels() {
        return getCameraMegapixels(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * Return front camera number of megapixels
     */
    public float getFrontCameraMegapixels() {
        return getCameraMegapixels(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * Return IMEI number
     *
     * @see TelephonyManager for more details
     */
    public String getImei() {
        return mTelephonyManager.getDeviceId();
    }

    /**
     * Return IMSI number
     *
     * @see TelephonyManager for more details
     */
    public String getImsi() {
        return mTelephonyManager.getSubscriberId();
    }

    /**
     * Return phone number if available, or empty string
     *
     * @see TelephonyManager for more details
     */
    public String getPhoneNumber() {
        return mTelephonyManager.getLine1Number();
    }

    /**
     * Return Wifi ip
     */
    public int getWifiIp() {
        return mWifiManager.getConnectionInfo().getIpAddress();
    }

    /**
     * Return MAC address of Wifi interface
     */
    public String getWifiMac() {
        String mac = mWifiManager.getConnectionInfo().getMacAddress();

        return mac != null ? mac.toUpperCase() : null;
    }

    /**
     * Return network operator code
     *
     * @see TelephonyManager for more details
     */
    public String getOperatorCode() {
        return mTelephonyManager.getNetworkOperator();
    }

    /**
     * Return network operator name
     *
     * @see TelephonyManager for more details
     */
    public String getOperatorName() {
        return mTelephonyManager.getNetworkOperatorName();
    }

    /**
     * Return network operator country ISO code
     *
     * @see TelephonyManager for more details
     */
    public String getNetworkCountryIso() {
        return mTelephonyManager.getNetworkCountryIso();
    }

    /**
     * Return SIM card state
     *
     * @see TelephonyManager for more details
     */
    public int getSimState() {
        return mTelephonyManager.getSimState();
    }

    /**
     * Return phone type
     *
     * @see TelephonyManager for more details
     */
    public int getPhoneType() {
        return mTelephonyManager.getPhoneType();
    }

    /**
     * Return string represent phone type
     */
    public String getPhoneTypeString() {
        switch (getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "None";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";

        }

        return null;
    }

    /**
     * Return network data state
     *
     * @see TelephonyManager for more details
     */
    public int getDataState() {
        return mTelephonyManager.getDataState();
    }

    /**
     * Return string represent network data state
     */
    public String getDataStateString() {
        switch (getDataState()) {
            case TelephonyManager.DATA_DISCONNECTED:
                return "Disconnected";
            case TelephonyManager.DATA_CONNECTING:
                return "Connecting";
            case TelephonyManager.DATA_CONNECTED:
                return "Connected";
            case TelephonyManager.DATA_SUSPENDED:
                return "Suspended";
        }

        return null;
    }

    /**
     * Return network data type
     *
     * @see TelephonyManager for more details
     */
    public int getNetworkType() {
        return mTelephonyManager.getNetworkType();
    }

    /**
     * Return string represent network type
     */
    public String getNetworkTypeString() {
        switch (getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA (IS95A or IS95B)";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO revision 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO revision A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO revision B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
        }

        return null;
    }

    /**
     * Check if network is roaming
     */
    public boolean isNetworkRoaming() {
        return mTelephonyManager.isNetworkRoaming();
    }

    /**
     * Return Linux kernel version
     */
    public String getKernelVersion() {
        String procVersionStr = Utils.readFile("/proc/version");

        return procVersionStr;

//        final String PROC_VERSION_REGEX =
//                "\\w+\\s+" + /* ignore: Linux */
//                        "\\w+\\s+" + /* ignore: version */
//                        "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
//                        "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
//                        "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
//                        "([^\\s]+)\\s+" + /* group 3: #26 */
//                        "((\\w+\\s+){1,2})" + /* group 4: [SMP] PREEMPT */
//                        ".+"; /* group 5: date */
//
//        Matcher m = Pattern.compile(PROC_VERSION_REGEX, Pattern.CASE_INSENSITIVE).matcher(procVersionStr);
//
//        if (m.matches() && m.groupCount() >= 4) {
//            return (m.group(1) + "\n" + m.group(2) + " " + m.group(3) + "\n" + m.group(4).trim()).trim();
//        } else {
//            return null;
//        }
    }

    /**
     * Return CPU processor
     */
    public String getCpuProcessor() {
        String cpuInfo = Utils.readFile("/proc/cpuinfo");

        Matcher m = Pattern.compile("Processor[\\s\\t]*:[\\s\\t]*(.*)", Pattern.CASE_INSENSITIVE).matcher(cpuInfo);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    /**
     * Return CPU implementer
     */
    // @TODO need to add missing implementer
    public String getCpuImplementer() {
        String cpuInfo = Utils.readFile("/proc/cpuinfo");

        Matcher m = Pattern.compile("CPU implementer[\\s\\t]*:[\\s\\t]*(.*)", Pattern.CASE_INSENSITIVE).matcher(cpuInfo);

        if (m.find()) {
            switch (m.group(1).toLowerCase()) {
                case "0x41":
                    return "ARM";
                case "0x44":
                    return "Digital Equipment Corporation";
                case "0x4d":
                    return "Motorola";
                case "0x51":
                    return "QUALCOMM";
                case "0x56":
                    return "Marvell";
                case "0x69":
                    return "Intel";
                case "0x65":
                    return "NVIDIA";
            }
        }

        return null;


    }

    /**
     * Return part of the CPU
     */
    // @TODO need to add missing part
    public String getCpuPart() {
        String cpuInfo = Utils.readFile("/proc/cpuinfo");

        Matcher m = Pattern.compile("CPU part[\\s\\t]*:[\\s\\t]*(.*)", Pattern.CASE_INSENSITIVE).matcher(cpuInfo);

        if (m.find()) {
            switch (m.group(1).toLowerCase()) {
                case "0x920":
                    return "ARM920";
                case "0x946":
                    return "ARM946";
                case "0x966":
                    return "ARM966";
                case "0xa26":
                    return "ARM1026";
                case "0xb02":
                    return "ARM11 MPCore";
                case "0xb36":
                    return "ARM1136";
                case "0xb56":
                    return "ARM1156";
                case "0xb76":
                    return "ARM1176";
                case "0xc05":
                    return "Cortex-A5";
                case "0xc07":
                    return "Cortex-A7";
                case "0xc08":
                    return "Cortex-A8";
                case "0xc09":
                    return "Cortex-A9";
                case "0xc0f":
                    return "Cortex-A15";
                case "0xc0e":
                    return "Cortex-A17";
                case "0xc14":
                    return "Cortex-R4";
                case "0xc15":
                    return "Cortex-R5";
                case "0xc20":
                    return "Cortex-M0";
                case "0xc21":
                    return "Cortex-M1";
                case "0xc23":
                    return "Cortex-M3";
                case "0xc24":
                    return "Cortex-M4";
                case "0xf":
                    return "Snapdragon S1 (Scorpion)";
                case "0x2d":
                    return "Snapdragon S3 (Scorpion)";
                case "0x4d":
                    return "Snapdragon S4 Plus (Krait)";
                case "0x06f":
                    return "Snapdragon S4 Pro (Krait)";
            }
        }

        return null;
    }

    /**
     * Return number of CPU cores
     */
    public int getCpuCores() {
        int cores = 0;
        String cpuInfo = Utils.readFile("/proc/cpuinfo");

        Matcher m = Pattern.compile("processor[\\s\\t]*:[\\s\\t]*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(cpuInfo);

        while (m.find()) {
            cores++;
        }

        return cores;
    }

    /**
     * Return min CPU frequency
     */
    public int getCpuMinFrequency() {
        try {
            return Integer.parseInt(Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Return max CPU frequency
     */
    public int getCpuMaxFrequency() {
        try {
            return Integer.parseInt(Utils.readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Return total CPU load, and load for each core. Key 0 is the total load, from 1 to n is each core load
     */
    public Map<Integer, Float> getCpuLoad() {
        Map<Integer, Float> stats = new HashMap<Integer, Float>();

        long[] idles1 = new long[getCpuCores() + 1];
        long[] cpus1 = new long[getCpuCores() + 1];
        long[] idles2 = new long[getCpuCores() + 1];
        long[] cpus2 = new long[getCpuCores() + 1];

        String stat = Utils.readFile("/proc/stat");
        Matcher m = Pattern.compile("cpu(\\d*)[^\\r^\\n]+", Pattern.CASE_INSENSITIVE).matcher(stat);
        int index = 0;
        while (m.find()) {
            String[] toks = m.group().split("\\s+");

            idles1[index] = Long.parseLong(toks[4]);
            cpus1[index] = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            index++;
        }

        try {
            Thread.sleep(360);
        } catch (InterruptedException e) {
        }

        stat = Utils.readFile("/proc/stat");
        m = Pattern.compile("cpu(\\d*)[^\\r^\\n]+", Pattern.CASE_INSENSITIVE).matcher(stat);
        index = 0;
        while (m.find()) {
            String[] toks = m.group().split("\\s+");

            idles2[index] = Long.parseLong(toks[4]);
            cpus2[index] = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            index++;
        }

        for (int i = 0; i < index; i++) {
            float load = (float) (cpus2[i] - cpus1[i]) / ((cpus2[i] + idles2[i]) - (cpus1[i] + idles1[i]));

            stats.put(i, load);
        }

        return stats;
    }

    /**
     * Return number of running processes
     */
    public int getRunningProcesses() {
        return mActivityManager.getRunningAppProcesses().size();
    }

    /**
     * Return string represent resolution, format: "[WIDTH] x [HEIGHT]"
     */
    public String getResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels + " x " + displayMetrics.heightPixels;
    }

    /**
     * Return string represent density, format "[DENSITY_DPI] dpi ([DENSITY] pixel/dpi)"
     */
    public String getDensity() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.densityDpi + "dpi (" + displayMetrics.density + " pixel/dpi)";
    }

    /**
     * Return dimension in inch, format "[WIDTH] x [HEIGHT] ([DIAGONAL] diagonal)"
     */
    public String getDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        DecimalFormat df = new DecimalFormat("0.0");

        float widthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        float heightInches = displayMetrics.heightPixels / displayMetrics.ydpi;

        double diagonalInches = Math.sqrt(
                (widthInches * widthInches)
                        + (heightInches * heightInches));

        return df.format(widthInches) + "\" x " + df.format(heightInches) + "\" (" + df.format(diagonalInches) + "\" diagonal)";
    }

    /**
     * Return OpenGL ES version
     */
    public String getGlVersion() {
        return mActivityManager.getDeviceConfigurationInfo().getGlEsVersion();
    }

    /**
     * Return total RAM in bytes
     */
    public long getRamTotal() {
        String cpuInfo = Utils.readFile("/proc/meminfo");

        Matcher m = Pattern.compile("MemTotal[\\s\\t]*:[\\s\\t]*(\\d+)[\\s\\t]*kb", Pattern.CASE_INSENSITIVE).matcher(cpuInfo);

        if (m.find()) {
            try {
                return Long.parseLong(m.group(1)) * 1024;
            } catch (NumberFormatException e) {

            }
        }

        return 0;
    }

    /**
     * Return free RAM in bytes
     */
    public long getRamFree() {
        mActivityManager.getMemoryInfo(mMemoryInfo);

        return mMemoryInfo.availMem;
    }

    /**
     * Return total internal storage in bytes
     */
    public long getInternalStorageTotal() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return (long) stat.getBlockCount() * (long) stat.getBlockSize();
    }

    /**
     * Return free internal storage in bytes
     */
    public long getInternalStorageFree() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
    }

    /**
     * @param camId
     * @return total megapixel
     */
    private float getCameraMegapixels(int camId) {
        try {
            Camera camera = Camera.open(camId);
            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size result = null;

            camera.release();

            ArrayList<Integer> arrayListForWidth = new ArrayList<>();
            ArrayList<Integer> arrayListForHeight = new ArrayList<>();

            for (int i = 0; i < sizes.size(); i++) {
                result = sizes.get(i);
                arrayListForWidth.add(result.width);
                arrayListForHeight.add(result.height);
            }

            if (arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0) {
                return (float) Collections.max(arrayListForWidth) * Collections.max(arrayListForHeight) / 1000000;
            }
        } catch (Exception e) {

        }

        return 0;
    }
}