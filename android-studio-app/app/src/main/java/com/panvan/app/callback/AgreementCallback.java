package com.panvan.app.callback;

import com.panvan.app.data.enums.AgreementEnum;

public interface AgreementCallback {

    void success(AgreementEnum agreementEnum);

    void failed(AgreementEnum agreementEnum, byte[] msg);
}
