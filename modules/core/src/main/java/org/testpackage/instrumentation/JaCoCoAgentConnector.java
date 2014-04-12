package org.testpackage.instrumentation;

import com.googlecode.javaewah.datastructure.BitSet;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author rnorth
 */
public class JaCoCoAgentConnector implements IExecutionDataVisitor, ISessionInfoVisitor {

    public static void main(String[] args) throws IOException {

        // Open a socket to the coverage agent:
        final Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 6300);
        final RemoteControlWriter writer = new RemoteControlWriter(
                socket.getOutputStream());
        final RemoteControlReader reader = new RemoteControlReader(
                socket.getInputStream());

        final JaCoCoAgentConnector connector = new JaCoCoAgentConnector();
        reader.setExecutionDataVisitor(connector);
        reader.setSessionInfoVisitor(connector);
//
//            // Send a dump command and read the response:
//            writer.visitDumpCommand(true, false);
//            reader.read();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.visitDumpCommand(true, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        while (reader.read()) ;

        socket.close();
    }

    @Override
    public void visitClassExecution(ExecutionData data) {

        if (data.getName().startsWith("org/testpackage")) {

            final BitSet classBitSet = new BitSet(data.getProbes().length);
            for (int i = 0; i < data.getProbes().length; i++) {
                boolean covered = data.getProbes()[i];
                classBitSet.set(i, covered);
            }
            classBitSet.trim();

            System.out.printf("%016x  %3d of %3d   %80s %s%n",
                    Long.valueOf(data.getId()),
                    Integer.valueOf(getHitCount(data.getProbes())),
                    Integer.valueOf(data.getProbes().length),
                    data.getName(),
                    bitSetAsString(classBitSet, data.getProbes().length));
        }

    }

    private String bitSetAsString(BitSet classBitSet, int length) {
        StringBuffer sb = new StringBuffer();

        sb.append('[');
        for (int i = 0; i < length; i++) {

            sb.append(i < classBitSet.size() && classBitSet.get(i) ? 'X' : ' ');
        }
        sb.append(']');

        return sb.toString();
    }

    private int getHitCount(final boolean[] data) {
        int count = 0;
        for (final boolean hit : data) {
            if (hit) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void visitSessionInfo(SessionInfo info) {
        System.out.println("Session info: " + info);
    }
}
