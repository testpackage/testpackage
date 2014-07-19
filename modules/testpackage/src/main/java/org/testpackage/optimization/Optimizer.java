package org.testpackage.optimization;

import org.junit.runner.Request;

/**
 * @author richardnorth
 */
public interface Optimizer {

    Request filter(Request request);
}
