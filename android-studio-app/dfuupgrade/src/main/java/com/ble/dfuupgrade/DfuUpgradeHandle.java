package com.ble.dfuupgrade;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ble.dfuupgrade.callback.IDfuProgressCallback;

import java.io.File;
import java.util.Objects;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class DfuUpgradeHandle implements DfuProgressListener {

    private static final String TAG = DfuUpgradeHandle.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static DfuUpgradeHandle INSTANCE = null;

    private IDfuProgressCallback progressCallback = null;

    private DfuServiceController serviceController;
    private DfuServiceInitiator starter;

    private static boolean isUpgrading = false;

    public static DfuUpgradeHandle getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (DfuUpgradeHandle.class) {
                INSTANCE = new DfuUpgradeHandle();
            }
        }
        return INSTANCE;
    }

    private static BluetoothLeScannerCompat scanner = null;

    private static String address = "";
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;
    private static IDfuProgressCallback iDfuProgressCallback = null;

    public void init(Context accontext, String mac, IDfuProgressCallback callback) {
        address = mac;
        context = accontext;
        iDfuProgressCallback = callback;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            // 处理扫描到的蓝牙设备
            BluetoothDevice device = result.getDevice();
            if (device.getAddress().equals(address)) {
                start(context, address, iDfuProgressCallback);
                stopScan();
            }

        }
    };

    public void startScan() {
        if (Objects.isNull(scanner)) {
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 设置扫描模式
                    .build();

            scanner = BluetoothLeScannerCompat.getScanner();
            scanner.startScan(null, settings, scanCallback);
            new Handler().postDelayed(this::stopScan, 20000); // 设置扫描时间
        }
    }

    private void stopScan() {
        if (Objects.nonNull(scanner)) {
            BluetoothLeScannerCompat.getScanner().stopScan(scanCallback);
            scanner = null;
        }
    }

    public void start(Context context, String address, IDfuProgressCallback callback) {
        this.progressCallback = callback;
        try {
            // 监听升级进度
            DfuServiceListenerHelper.registerProgressListener(context, this);

            starter = new DfuServiceInitiator(address)
                    .setDeviceName("X15X_DFU")
                    .setKeepBond(false)//保持设备绑定 官方demo为false
                    .setForceDfu(false)
                    .setPacketsReceiptNotificationsEnabled(false)
                    .setPacketsReceiptNotificationsValue(12)
                    .setForceScanningForNewAddressInLegacyDfu(false)
                    .setPrepareDataObjectDelay(400)
                    // 官方ndemo为true;
                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

            //闪退问题解决 兼容   启动前台通知的问题，因为这个库在升级的时候会在通知栏显示进度，
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                starter.setForeground(true);
                DfuServiceInitiator.createDfuNotificationChannel(context);
            }

            String path = context.getFilesDir().getAbsolutePath() + File.separator + "DFU_FIRMWARE";
            File file = new File(path);

            File[] files = file.listFiles();

            if (Objects.isNull(files)) {
                callback.onError(address, -1, -1, "固件获取失败");
                return;
            }
            File newFile = null;
            for (File listFile : files) {
                if (!listFile.isHidden()) {
                    if (Objects.isNull(newFile)) {
                        newFile = listFile;
                    } else if (listFile.lastModified() > newFile.lastModified()) {
                        newFile = listFile;
                    }
                }
            }

            if (Objects.isNull(newFile)) {
                return;
            }

            starter.setZip(newFile.getPath());
            this.serviceController = starter.start(context, DfuService.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DfuUpgradeHandle", e.getMessage());
            isUpgrading = false;
            progressCallback.onDfuAborted(address);
        }
    }

    /**
     * 判断dfu状态
     *
     * @return
     */
    private boolean isDfuServiceRunning(Context mContext) {
        final ActivityManager manager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean getIsUpgrading() {
        return isUpgrading;
    }

    public static void setIsUpgrading(boolean isUpgrading) {
        DfuUpgradeHandle.isUpgrading = isUpgrading;
    }

    //退出 dfu
    public void dispose(Context mContext) {
        setIsUpgrading(false);
        DfuServiceListenerHelper.unregisterProgressListener(mContext, this);
        if (isDfuServiceRunning(mContext)) {
            if (serviceController != null) {
                serviceController.abort();
                mContext.stopService(new Intent(mContext, DfuService.class));
            }
        }
        if (starter != null) {
            starter = null;
        }
        if (serviceController != null) {
            serviceController = null;
        }
        release();
    }

    public void release() {
        address = null;
        context = null;
        iDfuProgressCallback = null;
    }

    /**
     * Method called when the DFU service started connecting with the DFU target.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDeviceConnecting(@NonNull String deviceAddress) {
        Log.i(TAG, "onDeviceConnecting");
        progressCallback.onDeviceConnecting(deviceAddress);
    }

    /**
     * Method called when the service has successfully connected, discovered services and found
     * DFU service on the DFU target.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDeviceConnected(@NonNull String deviceAddress) {
        Log.i(TAG, "onDeviceConnected");
        progressCallback.onDeviceConnected(deviceAddress);
    }

    /**
     * Method called when the DFU process is starting. This includes reading the DFU Version
     * characteristic, sending DFU_START command as well as the Init packet, if set.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDfuProcessStarting(@NonNull String deviceAddress) {
        Log.i(TAG, "onDfuProcessStarting");
    }

    /**
     * Method called when the DFU process was started and bytes about to be sent.
     *
     * @param deviceAddress the target device address
     */
    @Override
    public void onDfuProcessStarted(@NonNull String deviceAddress) {
        Log.i(TAG, "onDfuProcessStarted");
        progressCallback.onDfuProcessStarted(deviceAddress);
    }

    /**
     * Method called when the service discovered that the DFU target is in the application mode
     * and must be switched to DFU mode. The switch command will be sent and the DFU process
     * should start again. There will be no {@link #onDeviceDisconnected(String)} event following
     * this call.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onEnablingDfuMode(@NonNull String deviceAddress) {
        Log.i(TAG, "onEnablingDfuMode");
        progressCallback.onDfuProcessStarted(deviceAddress);
    }

    /**
     * Method called during uploading the firmware. It will not be called twice with the same
     * value of percent, however, in case of small firmware files, some values may be omitted.
     *
     * @param deviceAddress the target device address.
     * @param percent       the current status of upload (0-99).
     * @param speed         the current speed in bytes per millisecond.
     * @param avgSpeed      the average speed in bytes per millisecond
     * @param currentPart   the number pf part being sent. In case the ZIP file contains a Soft Device
     *                      and/or a Bootloader together with the application the SD+BL are sent as part 1,
     *                      then the service starts again and send the application as part 2.
     * @param partsTotal    total number of parts.
     */
    @Override
    public void onProgressChanged(@NonNull String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
        Log.i(TAG, "onProgressChanged");
        progressCallback.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
    }

    /**
     * Method called when the new firmware is being validated on the target device.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onFirmwareValidating(@NonNull String deviceAddress) {
        Log.i(TAG, "onFirmwareValidating");
    }

    /**
     * Method called when the service started to disconnect from the target device.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDeviceDisconnecting(String deviceAddress) {
        Log.i(TAG, "onDeviceDisconnecting");
        progressCallback.onDeviceDisconnecting(deviceAddress);
    }

    /**
     * Method called when the service disconnected from the device. The device has been reset.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDeviceDisconnected(@NonNull String deviceAddress) {
        Log.i(TAG, "onDeviceDisconnected");
        setIsUpgrading(false);
        progressCallback.onDeviceDisconnected(deviceAddress);
    }

    /**
     * Method called when the DFU process succeeded.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDfuCompleted(@NonNull String deviceAddress) {
        Log.i(TAG, "onDfuCompleted");
        setIsUpgrading(false);
        progressCallback.onDfuCompleted(deviceAddress);
        progressCallback = null;
    }

    /**
     * Method called when the DFU process has been aborted.
     *
     * @param deviceAddress the target device address.
     */
    @Override
    public void onDfuAborted(@NonNull String deviceAddress) {
        Log.i(TAG, "onDfuAborted");
        setIsUpgrading(false);
        if (Objects.nonNull(progressCallback)) {
            progressCallback.onDfuAborted(deviceAddress);
        }
    }

    /**
     * Method called when an error occur.
     *
     * @param deviceAddress the target device address.
     * @param error         error number.
     * @param errorType     the error type, one of
     * @param message       the error message.
     */
    @Override
    public void onError(@NonNull String deviceAddress, int error, int errorType, String message) {
        Log.i(TAG, "onError");
        setIsUpgrading(false);
        progressCallback.onError(deviceAddress, error, errorType, message);
    }
}
