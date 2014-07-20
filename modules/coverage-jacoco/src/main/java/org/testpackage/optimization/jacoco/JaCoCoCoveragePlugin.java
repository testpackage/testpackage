package org.testpackage.optimization.jacoco;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractFuture;
import com.googlecode.javaewah.datastructure.BitSet;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.testpackage.Configuration;
import org.testpackage.optimization.ClassCoverage;
import org.testpackage.optimization.CoveragePlugin;
import org.testpackage.pluginsupport.AbstractPlugin;
import org.testpackage.pluginsupport.FatalPluginException;
import org.testpackage.pluginsupport.PluginException;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testpackage.AnsiSupport.ansiPrintf;

/**
 * @author richardnorth
 */
public class JaCoCoCoveragePlugin extends AbstractPlugin implements CoveragePlugin {

    Map<String, Long> testStartTimes = Maps.newHashMap();

    private Configuration configuration;
    private Socket agentSocket;
    private boolean active;

    private final Set<String> knownClasses = new HashSet<String>();
    private RemoteControlWriter agentWriter;
    private RemoteControlReader agentReader;

    @Override
    public void configure(Configuration configuration) throws PluginException {
        this.configuration = configuration;

        // Open a socket to the coverage agent:
        try {
            agentSocket = new Socket(configuration.getJaCoCoAgentHostName(), configuration.getJaCoCoAgentPort());

            agentWriter = new RemoteControlWriter(agentSocket.getOutputStream());
            agentReader = new RemoteControlReader(agentSocket.getInputStream());
        } catch (IOException e) {
            if (agentSocket != null) {
                try {
                    agentSocket.close();
                } catch (IOException ignored) {
                }
            }
            throw new FatalPluginException("Unable to connect JaCoCo agent to " + configuration.getJaCoCoAgentHostName() + ":" + configuration.getJaCoCoAgentPort() +
                    ". Is the system under test running, and is the JaCoCo agent configured correctly?", e);
        }

        ansiPrintf("@|blue Connected to JaCoCo agent at %s:%s. Test coverage will be recorded for classes under package '%s'|@\n",
                configuration.getJaCoCoAgentHostName(),
                configuration.getJaCoCoAgentPort(),
                configuration.getJaCoCoUserPackagePrefix());
        active = true;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void beforeTest(String testIdentifier) throws PluginException {

        testStartTimes.put(testIdentifier, System.currentTimeMillis());
    }

    @Override
    public void afterTest(String testIdentifier) throws PluginException {

        // calculate execution time
        long executionTime = System.currentTimeMillis() - testStartTimes.get(testIdentifier);

        // Trigger an agent dump
        try {
            Set<ClassCoverage> executionData = new JaCoCoDumpFuture(agentWriter, agentReader, knownClasses).get(5, TimeUnit.SECONDS);

            List<ClassCoverage> filteredCoverages = new ArrayList<ClassCoverage>();
            for (ClassCoverage classCoverage : executionData) {
                if (classCoverage.getClassIdentifier().startsWith(this.configuration.getJaCoCoUserPackagePrefix().replace('.', '/'))) {
                    //System.out.println(classCoverage.getClassIdentifier() + " " + classCoverage);

                    filteredCoverages.add(classCoverage);
                }
            }
            configuration.getTestCoverageRepository().addCoverage(testIdentifier, executionTime, filteredCoverages.toArray(new ClassCoverage[filteredCoverages.size()]));

        } catch (IOException e) {
            throw new PluginException("Problem communicating with Java agent", e);
        } catch (InterruptedException e) {
            throw new PluginException("Exception in beforeTest", e);
        } catch (ExecutionException e) {
            throw new PluginException("Exception in beforeTest", e);
        } catch (TimeoutException e) {
            throw new PluginException("Exception in beforeTest", e);
        }
    }

    private class JaCoCoDumpFuture extends AbstractFuture<Set<ClassCoverage>> implements IExecutionDataVisitor, ISessionInfoVisitor {

        private final Set<String> knownClasses;

        private Set<String> unreportedClasses = new HashSet<String>();
        private Set<ClassCoverage> result = new HashSet<ClassCoverage>();

        public JaCoCoDumpFuture(RemoteControlWriter agentWriter, RemoteControlReader agentReader, Set<String> knownClasses) throws IOException {

            this.knownClasses = knownClasses;

            agentReader.setExecutionDataVisitor(this);
            agentReader.setSessionInfoVisitor(this);

            unreportedClasses.addAll(knownClasses);

            agentWriter.visitDumpCommand(true, true);
            agentReader.read();
        }

        @Override
        public void visitClassExecution(ExecutionData data) {

            knownClasses.add(data.getName());
            unreportedClasses.remove(data.getName());

            final BitSet classBitSet = new BitSet(data.getProbes().length);
            for (int i = 0; i < data.getProbes().length; i++) {
                boolean covered = data.getProbes()[i];
                classBitSet.set(i, covered);
            }

            result.add(new ClassCoverage(data.getName(), classBitSet, data.getProbes().length, 0L));

            if (unreportedClasses.isEmpty()) {
                this.set(result);
            }
        }

        @Override
        public void visitSessionInfo(SessionInfo info) {
            //System.out.println(info);
        }
    }
}
