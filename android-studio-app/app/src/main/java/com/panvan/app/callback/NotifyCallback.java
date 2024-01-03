// package com.panvan.app.callback;
//
// import android.bluetooth.BluetoothGattCharacteristic;
// import android.os.Build;
//
// import androidx.annotation.RequiresApi;
//
// import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
// import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
// import com.ble.blescansdk.ble.scan.handle.BleHandler;
// import com.ble.blescansdk.ble.utils.ProtocolUtil;
// import com.db.database.cache.CacheScheduled;
// import com.db.database.callback.DBCallback;
// import com.db.database.service.CommunicationDataService;
// import com.panvan.app.Receiver.service.NotificationMonitorService;
// import com.panvan.app.data.constants.JsBridgeConstants;
// import com.panvan.app.data.enums.AgreementEnum;
// import com.panvan.app.scheduled.CommandRetryScheduled;
// import com.panvan.app.scheduled.DeviceDataUpdateScheduled;
// import com.panvan.app.scheduled.DeviceHistoryDataUpdateScheduled;
// import com.panvan.app.service.CommunicationService;
// import com.panvan.app.utils.AsyncTaskProcessorUtil;
// import com.panvan.app.utils.DataConvertUtil;
// import com.panvan.app.utils.JsBridgeUtil;
// import com.panvan.app.utils.LogUtil;
//
// import java.util.Objects;
// import java.util.concurrent.TimeUnit;
//
// public class NotifyCallback extends BleNotifyCallback<BraceletDevice> {
//
//     private static NotifyCallback callback = null;
//
//     public static NotifyCallback getInstance() {
//         if (Objects.isNull(callback)) {
//             callback = new NotifyCallback();
//         }
//         return callback;
//     }
//
//     private final Object object = new Object();
//
//     private AgreementPacketInfo agreementPacketInfo;
//
//     private boolean packetBreakage;
//
//     private long previousPackageTime;
//
//     private final AgreementCallback AGREEMENT_CALLBACK = new AgreementCallback() {
//         @Override
//         public void success(AgreementEnum agreementEnum) {
//             packetBreakage = false;
//             synchronized (object) {
//                 if (Objects.nonNull(agreementPacketInfo)) {
//                     agreementPacketInfo = null;
//                 }
//             }
//
//         }
//
//         @Override
//         public void failed(AgreementEnum agreementEnum, byte[] msg) {
//             packetBreakage = true;
//             synchronized (object) {
//                 if (Objects.isNull(agreementPacketInfo)) {
//                     agreementPacketInfo = new AgreementPacketInfo();
//                     agreementPacketInfo.setAgreementEnum(agreementEnum);
//                 }
//                 agreementPacketInfo.setBytes(msg);
//                 agreementPacketInfo.setTime(System.currentTimeMillis());
//             }
//
//
//         }
//     };
//
//
//     @Override
//     public void onChanged(BraceletDevice device, BluetoothGattCharacteristic characteristic) {
//         // LogUtil.info("接收到onChanged:" + ProtocolUtil.byteArrToHexStr(bytes));
//             byte[] bytes = characteristic.getValue();
//
//             long currentTimeMillis = System.currentTimeMillis();
//             if (previousPackageTime != 0) {
//                 if (currentTimeMillis - previousPackageTime > 100 && packetBreakage) {
//                     packetBreakage = false;
//                 }
//             }
//
//             previousPackageTime = currentTimeMillis;
//
//             if (bytes[0] == 0x68 && bytes[1] == 0x17) {
//                 if (packetBreakage) {
//                     packetBreakage = false;
//                 }
//             }
//
//
//             if (packetBreakage && Objects.nonNull(agreementPacketInfo)) {
//                 bytes = DataConvertUtil.mergeBytes(agreementPacketInfo.getBytes(), bytes);
//                 agreementPacketInfo.getAgreementEnum().responseHandle(bytes, AGREEMENT_CALLBACK);
//                 return;
//             }
//
//             AgreementEnum agreementByResponse = AgreementEnum.getAgreementByResponse(bytes);
//             agreementByResponse.responseHandle(bytes, AGREEMENT_CALLBACK);
//
//
//     }
//
//
//     @RequiresApi(api = Build.VERSION_CODES.N)
//     @Override
//     public void onNotifySuccess(BraceletDevice device) {
//         LogUtil.info("开启通知成功");
//         JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_REQUEST_DATA);
//
//         BleHandler.of().postDelayed(() -> JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_FINISHED), 2000);
//
//         // 加载通知监听
//         NotificationMonitorService.toggleNotificationListenerService();
//         // 开启定时任务
//         DeviceDataUpdateScheduled.start();
//         try {
//             TimeUnit.MILLISECONDS.sleep(200);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//
//         CommunicationDataService.getInstance().cacheDataInit(new DBCallback() {
//             @Override
//             public void success() {
//                 CommunicationService.getInstance().reloadCommand();
//             }
//
//             @Override
//             public void failed() {
//                 CommunicationService.getInstance().reloadCommand();
//             }
//         });
//
//         CommandRetryScheduled.getInstance().start();
//         CacheScheduled.getInstance().start();
//     }
//
//     @Override
//     public void onNotifyFailed(BraceletDevice device, int failedCode) {
//         LogUtil.info("开启通知失败");
//         JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_FAILED);
//     }
//
//     @Override
//     public void onNotifyCanceled(BraceletDevice device) {
//
//     }
//
//
//     public static class AgreementPacketInfo {
//
//         private byte[] bytes;
//         private AgreementEnum agreementEnum;
//         private long time;
//
//
//         public byte[] getBytes() {
//             return bytes;
//         }
//
//         public void setBytes(byte[] bytes) {
//             this.bytes = bytes;
//         }
//
//         public AgreementEnum getAgreementEnum() {
//             return agreementEnum;
//         }
//
//         public void setAgreementEnum(AgreementEnum agreementEnum) {
//             this.agreementEnum = agreementEnum;
//         }
//
//         public long getDiffTime() {
//             return System.currentTimeMillis() - time;
//         }
//
//         public void setTime(long time) {
//             this.time = time;
//         }
//
//     }
// }
