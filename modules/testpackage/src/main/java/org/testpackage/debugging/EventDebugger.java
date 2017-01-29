package org.testpackage.debugging;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.testpackage.streams.StreamCapture;

import java.util.List;

/**
 * Created by rnorth on 29/01/2017.
 */
public abstract class EventDebugger {

    public static void add(String event) {
        final String indent = Strings.repeat("-", StreamCapture.depth());
        events.add("\n" + indent + event);
    }

    public static List<String> events = Lists.newArrayList();
}
