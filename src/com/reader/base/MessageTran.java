package com.reader.base;

public class MessageTran {
    private byte btPacketType; // 数据帧头
    private byte btCmd; // 数据包命令
    private byte[] btAryData; // 数据包数据
    private byte btCheck; // 校验和，为所有DATA字节之和取，低八位
    private byte[] btAryTranData; // 完整数据包

    public MessageTran(byte btCmd, byte[] btAryData) {
        this.btPacketType = CMD.BEG;
        this.btCmd = btCmd;

        this.btAryData = new byte[4];
        System.arraycopy(btAryData, 0, this.btAryData, 0, btAryData.length);

        this.btAryTranData = new byte[8];
        this.btAryTranData[0] = this.btPacketType;
        this.btAryTranData[1] = this.btCmd;
        System.arraycopy(this.btAryData, 0, this.btAryTranData, 2,
                this.btAryData.length);

        this.btCheck = checkSum(this.btAryData);
        this.btAryTranData[6] = this.btCheck;
        this.btAryTranData[7] = CMD.END;
    }

    /**
     * 获取完整数据包。
     */
    public byte[] getAryTranData() {
        return this.btAryTranData;
    }

    /**
     * 获取数据包数据包数据
     */
    public byte[] getAryData() {
        return this.btAryData;
    }

    /**
     * 获取数据包命令字
     */
    public byte getCmd() {
        return this.btCmd;
    }

    /**
     * 获取数据帧头
     */
    public byte getPacketType() {
        return this.btPacketType;
    }

    /**
     * 校验帧头
     */
    public boolean checkPacketType() {
        return this.btPacketType == CMD.BEG;
    }

    /**
     * 计算校验和
     */
    private byte checkSum(byte[] btAryBuffer) {
        byte btSum = 0x00;

        for (int nloop = 0; nloop < btAryBuffer.length; nloop++) {
            btSum += btAryBuffer[nloop];
        }

        return (byte) (((~btSum) + 1) & 0xFF);
    }
}