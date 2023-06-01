package com.seek.config.controller;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.seek.config.AppActivity;
import com.seek.config.Config;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.system.OpenLinkDTO;
import com.seek.config.entity.dto.system.SystemConfigurationInfoUpdateDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.system.SystemConfigurationInfoVO;
import com.seek.config.services.SystemService;
import com.seek.config.services.impl.SystemServiceImpl;
import com.seek.config.utils.I18nUtil;

import java.util.Locale;

@AppController(path = "system")
public class SystemController {

    private final SystemService systemService = SystemServiceImpl.getInstance();

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/clear-back", method = AppRequestMethod.POST)
    public void clearWebViewBack() {
        try {
            Config.webView.post(() -> {
                Config.webView.clearHistory();
                Config.webView.clearCache(true);
            });

            SeekStandardDeviceHolder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/closeWebView", method = AppRequestMethod.GET)
    public Boolean closeWebView(Integer formId) {
        return AppActivity.appActivity.closeWebView(formId);
    }

    @AppRequestMapper(path = "/scan-qr-code", method = AppRequestMethod.POST)
    public void scanQRCode() {
        systemService.scanQrCode();
    }

    @AppRequestMapper(path = "/open-link", method = AppRequestMethod.POST)
    public void openLink(OpenLinkDTO dto) {
        if (null == dto || StringUtils.isBlank(dto.getLink())) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(dto.getLink()));
        Config.mainContext.startActivity(intent);
    }

    @AppRequestMapper(path = "/configuration-info")
    public RespVO<SystemConfigurationInfoVO> queryConfig() {
        SystemConfigurationInfoVO systemConfigurationInfoVO = new SystemConfigurationInfoVO();
        try {
            Resources resources = Config.mainContext.getResources();
            Configuration config = resources.getConfiguration();
            String displayLanguage = config.locale.getDisplayLanguage();
            if (StringUtils.isBlank(displayLanguage)) {
                return RespVO.success(systemConfigurationInfoVO);
            }
            systemConfigurationInfoVO.setLanguage(displayLanguage);
        } catch (Exception e) {
            return RespVO.success(systemConfigurationInfoVO);
        }
        return RespVO.success(systemConfigurationInfoVO);
    }

    @AppRequestMapper(path = "/update/configuration-info", method = AppRequestMethod.POST)
    public RespVO<Void> updateConfig(SystemConfigurationInfoUpdateDTO dto) {
        if (null == dto || StringUtils.isBlank(dto.getLanguage())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.CONFIG_PARAMS_ERROR));
        }
        try {
            Resources resources = Config.mainContext.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration config = resources.getConfiguration();
            String displayLanguage = config.locale.getDisplayLanguage();
            if (dto.getLanguage().contains(displayLanguage)) {
                return RespVO.success();
            }

            if ("简体中文".equals(dto.getLanguage())) {
                setLanguageConfig(config, "zh");
            } else {
                setLanguageConfig(config, "en");
            }
            resources.updateConfiguration(config, dm);

            return RespVO.success();
        } catch (Exception e) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.REQUEST_ERROR));
        }
    }

    @AppRequestMapper(path = "/webview/reload", method = AppRequestMethod.POST)
    public void reloadWebView() {
        Config.webView.reload();
    }

    private void setLanguageConfig(Configuration config, String language) {
        SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.APP_LANGUAGE, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(language));
        } else {
            config.locale = new Locale(language);
        }


    }
}
