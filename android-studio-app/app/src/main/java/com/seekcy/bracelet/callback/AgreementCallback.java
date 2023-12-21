package com.seekcy.bracelet.callback;

import com.seekcy.bracelet.data.enums.AgreementEnum;

public interface AgreementCallback {

    void success(AgreementEnum agreementEnum);

    void failed(AgreementEnum agreementEnum, byte[] msg);
}
