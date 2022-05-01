import entities.Environment;

import java.io.File;

public class Main {

    private static final File rootFolder = new File(System.getProperty("user.dir"));

    public static void main(String[] args) throws Exception {
        new Environment(rootFolder).decrypt();
    }
}
