package org.testpackage.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Mojo to assemble a TestPackage fat, executable JAR.
 *
 */
@Mojo(name = "fatjar", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class TestPackageMojo extends AbstractMojo {

    @Parameter(alias = "package-name")
    private String packageName;

    @Parameter(defaultValue = "-Xmx1G")
    private String flags;

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello world");

        getLog().info("Dependencies: " + mavenProject.getDependencies());

        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-shade-plugin"),
                        version("2.1")
                ),
                "shade",
                configuration(
                        element("transformers",
                                element("transformer", attribute("implementation", "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"),
                                        element("manifestEntries",
                                                element("Main-Class", "org.testpackage.TestPackage"),
                                                element("TestPackage-Package", packageName)
                                        )
                                )
                        )
                ),
                executionEnvironment(
                        mavenProject,
                        mavenSession,
                        pluginManager
                )
        );

        executeMojo(
                plugin(
                        groupId("org.skife.maven"),
                        artifactId("really-executable-jar-maven-plugin"),
                        version("1.1.0")
                ),
                "really-executable-jar",
                configuration(
                        element("flags", flags)
                ),
                executionEnvironment(
                        mavenProject,
                        mavenSession,
                        pluginManager
                )
        );
    }
}
