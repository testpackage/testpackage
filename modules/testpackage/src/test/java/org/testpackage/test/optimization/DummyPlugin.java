package org.testpackage.test.optimization;

import org.testpackage.pluginsupport.AbstractPlugin;
import org.testpackage.pluginsupport.Plugin;

/**
* @author richardnorth
*/
public class DummyPlugin extends AbstractPlugin implements Plugin {

    public boolean getVerbose() {
        return this.configuration.verbose;
    }
}
