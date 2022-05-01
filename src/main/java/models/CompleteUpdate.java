package models;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class CompleteUpdate {

    @Expose()
    private String[] xorKeys;

    @Expose()
    private HashMap<Integer, String> envData;

    public CompleteUpdate() {
    }

    public CompleteUpdate(String[] xorKeys) {
        this.xorKeys = xorKeys;
        this.envData = new HashMap<>();
    }

    public String[] getXorKeys() {
        return xorKeys;
    }

    public void setXorKeys(String[] xorKeys) {
        this.xorKeys = xorKeys;
    }

    public HashMap<Integer, String> getEnvData() {
        return envData;
    }

    public void setEnvData(HashMap<Integer, String> envData) {
        this.envData = envData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompleteUpdate that = (CompleteUpdate) o;
        return Arrays.equals(xorKeys, that.xorKeys) && Objects.equals(envData, that.envData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(envData);
        result = 31 * result + Arrays.hashCode(xorKeys);
        return result;
    }

    @Override
    public String toString() {
        return "CompleteUpdate{" +
                "xorKeys=" + Arrays.toString(xorKeys) +
                ", envData=" + envData +
                '}';
    }
}
