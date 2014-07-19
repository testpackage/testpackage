package org.testpackage.example.jacoco;

import com.google.common.io.ByteStreams;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author richardnorth
 */
public class RestServerTests {

    @Test
    public void testA() throws IOException {
        hitUrl("/testA");
    }

    @Test
    public void testB() throws IOException {
        hitUrl("/testB");
    }

    @Test
    public void testC() throws IOException {
        hitUrl("/testC");
    }

    private void hitUrl(String url) throws IOException {
        final InputStream connection = new URL("http://localhost:5555" + url).openStream();
        final String content = new String(ByteStreams.toByteArray(connection));
        System.out.println(content);
    }
}
