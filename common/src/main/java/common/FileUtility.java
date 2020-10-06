package common;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileUtility {

    public static File createFile(String pathName) throws IOException {
        File file = new File(pathName);
        if (!file.exists()) {
            if(!file.createNewFile()) {
                throw new IOException(String.format("couldn’t create file %s%n", pathName));
            }
        }
        return file;
    }

    public static File createDirectory(String pathName) throws IOException {
        File file = new File(pathName);
        if (!file.exists()) {
            if(!file.mkdir()) {
                throw new IOException(String.format("couldn’t create directory %s%n", pathName));
            }
        }
        return file;
    }

    public static List<String> listFiles(String pathName) throws IOException {
        File file = new File(pathName);
        String[] array = file.list();
        return array == null ? null: Arrays.asList(array);
    }

    public static void readFileFromStream (String pathName, ObjectInputStream in) throws IOException {
        //System.out.println("pathName: " + pathName);
        File file = FileUtility.createFile(pathName);
        try (FileOutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            while (true) {
                int readBytes = in.read(buffer);
                if (readBytes == -1) break;
                os.write(buffer, 0, readBytes);
                //System.out.printf("read buffer %s%n", readBytes);
            }
        }
    }

    public static void writeFileToStream(String pathName, ObjectOutputStream out) throws IOException {
        //System.out.println("pathName: " + pathName);
        File file = FileUtility.createFile(pathName);
        try(InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8191];
            while(is.available()>0)
            {
                int readBytes = is.read(buffer);
                out.write(buffer, 0, readBytes);
                //System.out.printf("write buffer %s%n", readBytes);
            }
        }
    }

}

