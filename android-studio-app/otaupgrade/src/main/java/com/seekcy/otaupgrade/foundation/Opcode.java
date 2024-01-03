/********************************************************************************************************
 * @file Opcode.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2019
 *
 * @par Copyright (c) 2019, Telink Semiconductor (Shanghai) Co., Ltd. ("TELINK")
 *
 *          Licensed under the Apache License, Version 2.0 (the "License");
 *          you may not use this file except in compliance with the License.
 *          You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *          Unless required by applicable law or agreed to in writing, software
 *          distributed under the License is distributed on an "AS IS" BASIS,
 *          WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *          See the License for the specific language governing permissions and
 *          limitations under the License.
 *******************************************************************************************************/
package com.seekcy.otaupgrade.foundation;

public enum Opcode {

    /**
     * 该命令为获得 slave 当前 firmware 版本号的命令，user 若采用 OTA Legacy protocol 进行 OTA 升级，
     * 可以选 择使用，在使用该命令时，可通过 slave 端预留的回调函数来完成 firmware 版本号的传递。
     */
    CMD_OTA_VERSION(0xFF00, "Legacy"),

    /**
     * 该命令为 OTA 升级开始命令，master 发这个命令给 slave，用来正式启动 OTA 更新。
     * 该命令仅供 Legacy Protocol 进行使用，user 若采用 OTA Legacy protocol，则必须使用该命令。
     */
    CMD_OTA_START(0xFF01, "Legacy"),

    /**
     * 该命令为结束命令，OTA 中的 legacy 和 extend protocol 均采用该命令为结束命令，
     * 当 master 确定所有的 OTA 数据都被 slave 正确接收后，发送 OTA end 命令。
     */
    CMD_OTA_END(0xFF02, "All"),

    /**
     * 该命令为 extend protocol 中的 OTA 升级开始命令，master 发这个命令给 slave，用来正式启动 OTA 更新。
     * user 若采用 OTA extend protocol 则必须采用该命令作为开始命令。
     */
    CMD_OTA_START_EXT(0xFF03, "Extend"),

    /**
     * 该命令为 OTA 升级过程中的版本比较请求命令，该命令由 client 发起给 Server 端，请求获取版本号和升级许可。
     */
    CMD_OTA_FW_VERSION_REQ(0xFF04, "Extend"),

    /**
     * 该命令为版本响应命令，server 端在收到 client 发来的版本比较请求命令(CMD_OTA_FW_VERSION_REQ) 后，会将已有的 firmware 版本号与 client 端请求升级的版本号进行对比，确定是否升级，相关信息通过该命令返 回发送给 client.
     */
    CMD_OTA_FW_VERSION_RSP(0xFF05, "Extend"),

    /**
     * 该命令为 OTA 结果返回命令，OTA 结束后 slave 会将结果信息发送给 master，在整个 OTA 过程中，无论成功 或失败，OTA_result 只会上报一次，user 可根据返回的结果来判断升级是否成功。
     */
    CMD_OTA_RESULT(0xFF06, "All"),

    CMD_OTA_SET_FW_INDEX(0xFF80, "All"),

    ;

    public final int value;
    public final String usage;

    Opcode(int value, String usage) {
        this.value = value;
        this.usage = usage;
    }
}
