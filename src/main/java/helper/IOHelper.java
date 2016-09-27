package helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOHelper {
    private static long copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;

    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyStream(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
}
