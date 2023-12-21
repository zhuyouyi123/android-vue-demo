package com.seekcy.bracelet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.seekcy.bracelet.data.entity.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppInfoAdapter extends ArrayAdapter<AppInfo> {

    // 在 AppAdapter 类中
    private final Map<Integer, AppInfo> clickedPositionsMap = new HashMap<>();

    // 添加方法来判断某个位置是否已点击
    public boolean isPositionClicked(int position) {
        return clickedPositionsMap.containsKey(position);
    }

    // 添加方法来设置选中项
    public void setSelectedPosition(int position, AppInfo appInfo) {
        if (isPositionClicked(position)) {
            clickedPositionsMap.remove(position); // 如果已点击，则移除
        } else {
            clickedPositionsMap.put(position, appInfo); // 如果未点击，则添加
        }
        notifyDataSetChanged(); // 刷新列表
    }

    public AppInfoAdapter(Context context, List<AppInfo> appInfoList) {
        super(context, 0, appInfoList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_app_info, parent, false);
        }

        AppInfo appInfo = getItem(position);

        // 设置应用名称
        TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
        appNameTextView.setText(appInfo.getAppName());

        // 设置应用图标
        ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
        appIconImageView.setImageDrawable(appInfo.getAppIcon());

        // 设置 CheckBox
        CheckBox appCheckBox = convertView.findViewById(R.id.appCheckBox);
        appCheckBox.setChecked(isPositionClicked(position));

        // CheckBox 点击事件
        appCheckBox.setOnClickListener(view -> setSelectedPosition(position, appInfo));

        // 列表项点击事件
        convertView.setOnClickListener(view -> {
            appCheckBox.setChecked(!appCheckBox.isChecked()); // 切换 CheckBox 状态
            setSelectedPosition(position, appInfo); // 切换点击状态
        });

        return convertView;
    }

    public List<AppInfo> listAppInfo() {
        List<AppInfo> packageNameList = new ArrayList<>();
        for (Map.Entry<Integer, AppInfo> entry : clickedPositionsMap.entrySet()) {
            packageNameList.add(entry.getValue());
        }
        return packageNameList;
    }
}
