package org.testpackage.streams;

import org.apache.commons.io.output.ProxyOutputStream;

import java.io.OutputStream;

/**
 * @author richardnorth
 */
public class RedirectableOutputStream extends ProxyOutputStream {

    /**
     * Constructs a new RedirectableOutputStream.
     *
     * @param delegate the OutputStream to delegate to
     */
    public RedirectableOutputStream(OutputStream delegate) {
        super(delegate);
    }

    public void setOutputStream(OutputStream newDelegate) {
        this.out = newDelegate;
    }
}
