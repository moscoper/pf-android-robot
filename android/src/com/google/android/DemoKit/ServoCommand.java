package com.google.android.DemoKit;

import junit.framework.Assert;

public class ServoCommand {
    public static final byte commandBase = (byte) (2 << 6);

    public static final byte motor1Stop = 0;

    public static final byte motor1Forward = (0x01) << 2;

    public static final byte motor1Backward = (3) << 2;

    public static final byte motor2Stop = 0;

    public static final byte motor2Forward = (0x01) << 4;

    public static final byte motor2Backward = (byte) ((3) << 4);

    private byte commandByte = 0;

    private byte servo1Degree = -1;

    private byte servo2Degree = -1;

    private byte servo3Degree = -1;

    public void setDegree(int level) {
        Assert.assertEquals(true, level >= 0 && level < 64);
        servo1Degree = (byte) level;
    }

    /**
     * コマンド全体のバイト列を返す<br>
     * @return
     */
    public byte[] toBytes() {
        byte[] r = new byte[4];
        r[0] = commandBase;
        if (servo1Degree >= 0) {
            r[0] |= 1;
            r[1] = servo1Degree;
        }
        if (servo2Degree >= 0) {
            r[0] |= 2;
            r[2] = servo2Degree;
        }
        if (servo3Degree >= 0) {
            r[0] |= 4;
            r[3] = servo3Degree;
        }

        System.out.println("====ServoCommand STT===");
        for (int i = 0; i < 4; i++) {
            System.out.println(r[i]);
        }
        System.out.println("====ServoCommand END===");
        return r;
    }
}