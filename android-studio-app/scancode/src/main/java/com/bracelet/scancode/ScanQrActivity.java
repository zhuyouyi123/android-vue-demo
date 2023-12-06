package com.bracelet.scancode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huawei.hms.hmsscankit.OnLightVisibleCallBack;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;

public class ScanQrActivity extends Activity {
    private FrameLayout frameLayout;
    private RemoteView remoteView;
    private ImageView backBtn;
    private ImageView flushBtn;
    int mScreenWidth;
    int mScreenHeight;

    // The width and height of scan_view_finder is both 240 dp.
    final int SCAN_FRAME_SIZE = 240;

    private final int[] img = {R.drawable.flashlight_on, R.drawable.flashlight_off};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_defined);

        // 绑定相机预览屏幕。
        frameLayout = findViewById(R.id.rim);

        //1. 获取屏幕密度以计算取景器的矩形
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        //2. Obtain the screen size.
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        // 3. 计算取景器的矩形，该矩形位于布局的中间
        // 设置扫描区域。（可选。Rect可以为null。如果未指定任何设置，则它将位于布局的中间。）
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;

        // Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = new RemoteView.Builder().setContext(this)
                .setBoundingBox(rect)
                .setFormat(HmsScan.QRCODE_SCAN_TYPE).build();
        // When the light is dim, this API is called back to display the flashlight switch.
        flushBtn = findViewById(R.id.flush_btn);
        remoteView.setOnLightVisibleCallback(new OnLightVisibleCallBack() {
            @Override
            public void onVisibleChanged(boolean visible) {
                if (visible) {
                    flushBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                //Check the result.
                if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                    Intent intent = new Intent();
                    intent.putExtra(ScanUtil.RESULT, result[0]);
                    setResult(RESULT_OK, intent);

//                    SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_STATUS_REQUEST_KEY, "false");
                    ScanQrActivity.this.finish();
                }
            }
        });
        // Load the customized view to the activity.
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);
        // Set the back, photo scanning, and flashlight operations.
        setBackOperation();
        setFlashOperation();
    }

    private void setFlashOperation() {
        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteView.getLightStatus()) {
                    remoteView.switchLight();
                    flushBtn.setImageResource(img[1]);
                } else {
                    remoteView.switchLight();
                    flushBtn.setImageResource(img[0]);
                }
            }
        });
    }

    private void setBackOperation() {
        backBtn = findViewById(R.id.back_img);
        backBtn.setOnClickListener(v -> ScanQrActivity.this.finish());
    }

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
//        SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_STATUS_REQUEST_KEY, "true");
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
//        SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_STATUS_REQUEST_KEY, "false");
    }

    /**
     * Handle the return results from the album.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(ScanQrActivity.this, bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                if (hmsScans != null && hmsScans.length > 0 && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                    Intent intent = new Intent();
                    intent.putExtra(ScanUtil.RESULT, hmsScans[0]);
                    setResult(RESULT_OK, intent);
                    ScanQrActivity.this.finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 重写intent setIntent(intent);
        super.onNewIntent(intent);
    }
}
