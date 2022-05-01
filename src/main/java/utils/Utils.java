package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;

public class Utils {

    private static final NumberFormat numberFormat;
    private static final StringBuilder sb;

    static {
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        sb = new StringBuilder();
    }

    public static byte[] reverseBytesArray(byte[] bytes) {
        byte[] reverseArray = new byte[bytes.length];
        int j = bytes.length;
        for (byte b : bytes) {
            reverseArray[j - 1] = b;
            j = j - 1;
        }
        return reverseArray;
    }

    public static int convertHexToInt(byte[] bytes) {
        bytes = reverseBytesArray(bytes);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        String idxStr = sb.toString();
        clearSB(sb);
        if ("ffffffff".equals(idxStr)) {
            return 0;
        }
        return Integer.parseInt(idxStr, 16);
    }

    public static byte[] readCustomLength(RandomAccessFile randomAccessFile, int length) throws Exception {
        byte[] bytes = new byte[length];
        randomAccessFile.read(bytes);
        return bytes;
    }

    public static int readInt(RandomAccessFile randomAccessFile) throws Exception {
        byte[] bytes = new byte[4];
        randomAccessFile.read(bytes);
        return convertHexToInt(bytes);
    }

    public static void writeDataToJson(File jsonFile, Object object) throws Exception {
        Files.deleteIfExists(jsonFile.toPath());
        RandomAccessFile randomAccessFile = new RandomAccessFile(jsonFile, "rw");
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        String json;
        if (!object.getClass().equals(JSONArray.class)) {
            json = gson.toJson(object);
        } else {
            json = object.toString();
        }
        randomAccessFile.write(json.getBytes(StandardCharsets.UTF_8));
        randomAccessFile.close();
    }

    public static void clearSB(StringBuilder sb) {
        sb.setLength(0);
    }

    public static RandomAccessFile openRAF(File file) throws Exception {
        return new RandomAccessFile(file, "r");
    }

    public static void closeRAF(RandomAccessFile randomAccessFile) throws Exception {
        randomAccessFile.close();
    }
}
