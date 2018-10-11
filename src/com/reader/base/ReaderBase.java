package com.reader.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class ReaderBase {
    private WaitThread mWaitThread = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    /**
     * 连接丢失。
     */
    public abstract void onLostConnect();

    /**
     * 可重写函数，解析到一包数据后会调用此函数。
     * @param msgTran
     */
    public abstract void analyData(byte[] btAryReceiveData);

    // 记录未处理的接收数据，主要考虑接收数据分段
    private byte[] m_btAryBuffer = new byte[1024];
    // 记录未处理数据的有效长度
    private int m_nLength = 0;

    /**
     * 带参构造函数，构造一个带输入、输出流的Reader。
     * 
     * @param in
     * @param out
     */
    public ReaderBase(InputStream in, OutputStream out) {
        this.mInStream = in;
        this.mOutStream = out;

        StartWait();
    }

    public boolean IsAlive() {
        return mWaitThread != null && mWaitThread.isAlive();
    }

    public void StartWait() {
        mWaitThread = new WaitThread();
        mWaitThread.start();
    }

    /**
     * 循环接收数据线程。
     */
    private class WaitThread extends Thread {
        private boolean mShouldRunning = true;

        public WaitThread() {
            mShouldRunning = true;
        }

        @Override
        public void run() {
            byte[] btAryBuffer = new byte[1024];
            while (mShouldRunning) {
                try {
                    int nLenRead = mInStream.read(btAryBuffer);

                    if (nLenRead > 0) {
                        byte[] btAryReceiveData = new byte[nLenRead];
                        System.arraycopy(btAryBuffer, 0, btAryReceiveData, 0,
                                nLenRead);
                        runReceiveDataCallback(btAryReceiveData);
                    }
                } catch (IOException e) {
                    onLostConnect();
                    return;
                } catch (Exception e) {
                    onLostConnect();
                    return;
                }
            }
        }

        public void signOut() {
            mShouldRunning = false;
            interrupt();
        }
    };

    /**
     * 退出接收线程。
     */
    public final void signOut() {
        mWaitThread.signOut();
        try {
            mInStream.close();
            mOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从输入流读取数据后，会调用此函数。
     * 
     * @param btAryReceiveData
     */
    private void runReceiveDataCallback(byte[] btAryReceiveData) {
        try {
            reciveData(btAryReceiveData);

            int nCount = btAryReceiveData.length;
            byte[] btAryBuffer = new byte[nCount + m_nLength];

            System.arraycopy(m_btAryBuffer, 0, btAryBuffer, 0, m_nLength);
            System.arraycopy(btAryReceiveData, 0, btAryBuffer, m_nLength,
                    btAryReceiveData.length);

            // 分析接收数据，以HEAD为数据起点，以协议中数据长度为数据终止点
            int nIndex = 0; // 当数据中存在HEAD时，记录数据的终止点
            int nMarkIndex = 0; // 当数据中不存在HEAD时，nMarkIndex等于数据组最大索引
            for (int nLoop = 0; nLoop < btAryBuffer.length; nLoop++) {
                if (btAryBuffer.length > nLoop + 1) {
                    if (btAryBuffer[nLoop] == CMD.HEAD1) {
                        int nLen = btAryBuffer[nLoop + 1] & 0xFF;
                        if (nLoop + 1 + nLen < btAryBuffer.length) {
                            byte[] btAryAnaly = new byte[nLen + 2];
                            System.arraycopy(btAryBuffer, nLoop, btAryAnaly, 0,
                                    nLen + 2);

                            analyData(btAryAnaly);

                            nLoop += 1 + nLen;
                            nIndex = nLoop + 1;
                        } else {
                            nLoop += 1 + nLen;
                        }
                    } else if (btAryBuffer[nLoop] == CMD.HEAD2) {
                        int nLen = 21;
                        if (nLoop + nLen < btAryBuffer.length) {
                            byte[] btAryAnaly = new byte[nLen + 1];
                            System.arraycopy(btAryBuffer, nLoop, btAryAnaly, 0,
                                    nLen + 1);// +1 is BEG

                            analyData(btAryAnaly);

                            nLoop += nLen;
                            nIndex = nLoop + 1;// +1 move to END index
                        } else {
                            nLoop += nLen;
                        }
                    } else if (btAryBuffer[nLoop] == CMD.BEG) {
                        int nLen = 7;
                        if (nLoop + nLen < btAryBuffer.length) {
                            byte[] btAryAnaly = new byte[nLen + 1];
                            System.arraycopy(btAryBuffer, nLoop, btAryAnaly, 0,
                                    nLen + 1);// +1 is BEG

                            analyData(btAryAnaly);
                            sendCMDResponse(btAryAnaly);// add

                            nLoop += nLen;
                            nIndex = nLoop + 1;// +1 move to END index
                        } else {
                            nLoop += nLen;
                        }
                    } else {
                        nMarkIndex = nLoop;
                    }
                }
            }

            if (nIndex < nMarkIndex) {
                nIndex = nMarkIndex + 1;
            }

            if (nIndex < btAryBuffer.length) {
                m_nLength = btAryBuffer.length - nIndex;
                Arrays.fill(m_btAryBuffer, 0, 1024, (byte) 0);
                System.arraycopy(btAryBuffer, nIndex, m_btAryBuffer, 0,
                        btAryBuffer.length - nIndex);
            } else {
                m_nLength = 0;
            }
        } catch (Exception e) {

        }
    }

    /**
     * @param btAryReceiveData
     */
    public void reciveData(byte[] btAryReceiveData) {
    };

    /**
     * @param btArySendData
     */
    public void sendData(byte[] btArySendData) {
    };

    /**
     * 发送数据，使用synchronized()防止并发操作。
     * 
     * @param btArySenderData to send
     * @return success 0, fail -1
     */
    private int sendMessage(byte[] btArySenderData) {

        try {
            synchronized (mOutStream) { // 防止并发
                mOutStream.write(btArySenderData);
                mOutStream.flush();
            }
        } catch (IOException e) {
            onLostConnect();
            return -1;
        } catch (Exception e) {
            onLostConnect();
            return -1;
        }

        sendData(btArySenderData);

        return 0;
    }

    /**
     * open house
     * 
     * @param btAryBuf
     *            to send
     * @return success 0, fail -1
     */
    public final int sendOpenHouse(boolean open) {
        byte[] btAryBuf = new byte[8];
        btAryBuf[0] = CMD.BEG;
        btAryBuf[1] = open ? CMD.TYPE_OPEN_HOUSE : CMD.TYPE_CLOSE_HOUSE;
        btAryBuf[2] = 0x00;
        btAryBuf[3] = 0x00;
        btAryBuf[4] = 0x00;// DATA2
        btAryBuf[5] = open ? (byte) 0x01 : (byte) 0x02;// DATA1
        btAryBuf[6] = (byte) (btAryBuf[4] + btAryBuf[5]);// CHECK
        btAryBuf[7] = CMD.END;
        int nResult = sendMessage(btAryBuf);
        return nResult;
    }

    /**
     * send response
     * 
     * @param btAryBuf
     *            to send
     * @return success 0, fail -1
     */
    public final int sendCMDResponse(byte[] cmd) {
        if (cmd[0] == CMD.BEG) {
            byte[] btAryBuf = new byte[8];
            btAryBuf[0] = CMD.BEG;
            btAryBuf[1] = cmd[1];
            btAryBuf[2] = 0x00;
            btAryBuf[3] = 0x00;
            btAryBuf[4] = 0x00;
            btAryBuf[5] = CMD.RESPONSE;
            btAryBuf[6] = btAryBuf[5];// CHECK
            btAryBuf[7] = CMD.END;
            int nResult = sendMessage(btAryBuf);
            return nResult;
        }
        return 0;
    }
}