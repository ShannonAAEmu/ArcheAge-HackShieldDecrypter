package entities;


import models.CompleteUpdate;
import org.apache.commons.codec.binary.Hex;
import utils.Utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Environment {

    private final File ENV_BINARY, ENV_JSON;
    private final byte[] XOR_KEY;
    private final byte[][] HEADERS = {{85, 86}, {72, 77}, {73, 80}, {72, 68}};      // HEX 55 56, 48 4D, 49 50, 48 44
    private RandomAccessFile envRAF;
    private CompleteUpdate completeUpdate;

    public Environment(File rootFolder) {
        this.ENV_BINARY = new File(rootFolder + "\\hsupdate.env");
        this.ENV_JSON = new File(rootFolder + "\\hsupdate.json");
        this.XOR_KEY = new byte[4];
    }

    public void decrypt() throws Exception {
        envRAF = Utils.openRAF(ENV_BINARY);
        byte[] header;
        int i = 0;
        while (envRAF.getFilePointer() < envRAF.length()) {
            header = readHeader();
            if (Arrays.equals(header, HEADERS[0])) {
                readCompleteUpdateFile();
                while (i != 12) {
                    readData(i);
                    i += 1;
                }
            } else if (Arrays.equals(header, HEADERS[1])) {
                while (i != 16) {
                    readData(i);
                    i += 1;
                }
            } else if (Arrays.equals(header, HEADERS[2])) {
                while (i != 18) {
                    readData(i);
                    i += 1;
                }
            } else if (Arrays.equals(header, HEADERS[3])) {
                while (i != 20) {
                    readData(i);
                    i += 1;
                }
            } else {
                System.out.println("UNKNOWN file header: " + header[0] + " " + header[1]);
            }
        }
        Utils.writeDataToJson(ENV_JSON, completeUpdate);
        Utils.closeRAF(envRAF);
    }

    private byte[] readHeader() throws Exception {
        return Utils.readCustomLength(envRAF, 2);
    }

    private void readCompleteUpdateFile() throws Exception {
        byte[] xorKey1 = Utils.readCustomLength(envRAF, 4);
        byte[] xorKey2 = Utils.readCustomLength(envRAF, 4);
        String[] xorKeys = new String[]{Hex.encodeHexString(xorKey1).toUpperCase(), Hex.encodeHexString(xorKey2).toUpperCase()};
        completeUpdate = new CompleteUpdate(xorKeys);
        calculateCommonXorKey(xorKey1, xorKey2);
    }

    private void calculateCommonXorKey(byte[] xorKey1, byte[] xorKey2) {
        byte[] commonXorKey = new byte[4];
        int k = 0;
        for (byte b : xorKey1) {
            commonXorKey[k] = (byte) (b ^ xorKey2[k++]);
        }
        for (int i = 1; i < commonXorKey.length; i++) {
            commonXorKey[i] = 0;
        }
        k = 0;
        for (byte b : xorKey1) {
            XOR_KEY[k] = (byte) (b ^ commonXorKey[k++]);
        }
    }

    private void readData(int key) throws Exception {
        int size = Utils.readInt(envRAF);
        byte[] data = Utils.readCustomLength(envRAF, size);
        byte[] result = new byte[data.length];
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ size);
            result[i] = (byte) (result[i] ^ XOR_KEY[j]);
            size = size + data[i];
            j += 1;
            if (4 == j) {
                j = 0;
            }
        }
        completeUpdate.getEnvData().put(key, new String(result));
    }
}
