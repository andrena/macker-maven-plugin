package org.codehaus.mojo.macker.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.test.plugin.BuildTool;
import org.apache.maven.shared.test.plugin.PluginTestTool;
import org.apache.maven.shared.test.plugin.ProjectTool;
import org.apache.maven.shared.test.plugin.TestToolsException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.mojo.macker.XmlComparer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.xml.sax.SAXException;

/**
 * An abstract testcase using the maven-plugin-testing-tools.
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a> (original Eclipse AbstractEclipsePluginIT)
 * @author <a href="mailto:fgiust@apache.org">Fabrizio Giustina</a> (original Eclipse AbstractEclipsePluginIT)
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a> (Modified for ounce-maven-plugin)
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public abstract class AbstractMackerPluginITCase
    extends AbstractMojoTestCase
{

    private BuildTool buildTool;

    private ProjectTool projectTool;

    /**
     * Test repository directory.
     */
    private static File localRepositoryDirectory;

    /**
     * Pom File.
     */
    private static File pomFile = new File( getBasedir(), "pom.xml" );

    /**
     * Version under which the plugin was installed to the test-time local repository for running test builds.
     */
    protected static final String VERSION = "test";

    private static final String BUILD_OUTPUT_DIRECTORY = "target/failsafe-reports/maven-output";

    private static boolean installed = false;

    /**
     * The name of the directory used for comparison of expected output.
     */
    private static final String EXPECTED_DIRECTORY_NAME = "expected";

    /**
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        if ( !installed )
        {
            System.out.println( "*** Running integation test builds; output will be directed to: "
                    + BUILD_OUTPUT_DIRECTORY );
        }
        super.setUp();

        buildTool = (BuildTool) lookup( BuildTool.ROLE, "default" );
        projectTool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );

        String mavenHome = System.getProperty( "maven.home" );
        // maven.home is set by surefire when the test is run with maven, but better make the test
        // run in IDEs without the need of additional properties
        if ( mavenHome == null )
        {
            String path = System.getProperty( "java.library.path" );
            String[] paths = StringUtils.split( path, System.getProperty( "path.separator" ) );
            for ( int j = 0; j < paths.length; j++ )
            {
                String pt = paths[j];
                if ( new File( pt, "mvn" ).exists() )
                {
                    System.setProperty( "maven.home", new File( pt ).getAbsoluteFile().getParent() );
                    break;
                }
            }
        }
        System.setProperty( "MAVEN_TERMINATE_CMD", "on" );

        synchronized (AbstractMackerPluginITCase.class)
        {
            if ( !installed )
            {
                PluginTestTool pluginTestTool = (PluginTestTool) lookup( PluginTestTool.ROLE, "default" );
                localRepositoryDirectory = pluginTestTool.preparePluginForUnitTestingWithMavenBuilds( pomFile, VERSION,
                        localRepositoryDirectory );
                System.out.println( "*** Installed test-version of the Macker plugin to: " + localRepositoryDirectory );
                installed = true;
            }
        }

    }

    /**
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        List/*<PlexusContainer>*/ containers = new ArrayList/*<PlexusContainer>*/();
        containers.add( getContainer() );
        for ( Iterator iter = containers.iterator(); iter.hasNext(); )
        {
            PlexusContainer cont = (PlexusContainer) iter.next();
            if ( cont != null )
            {
                cont.dispose();
                ClassRealm realm = cont.getContainerRealm();
                if ( realm != null )
                {
                    realm.getWorld().disposeRealm( realm.getId() );
                }
            }
        }
    }

    /**
     * Execute the plugin with no properties
     * @param projectName project directory
     * @param goalList comma separated list of goals to execute
     * @throws Exception any exception generated during test
     */
    protected void testProject( String projectName, String goalList )
        throws Exception
    {
        File baseDir = getTestFile( "target/test-classes/it/" + projectName );
        testProject( baseDir, new Properties(), goalList );
    }

    /**
     * Execute the plugin.
     * @param baseDir Execute the plugin goal on a test project and verify generated files.
     * @param properties additional properties
     * @param goalList comma separated list of goals to execute
     * @throws Exception any exception generated during test
     */
    protected void testProject( File baseDir, Properties properties, String goalList )
        throws Exception
    {
        File pom = new File( baseDir, "pom.xml" );

        String[] goal = goalList.split( "," );
        List/*<String>*/ goals = new ArrayList/*<String>*/();
        for ( int i = 0; i < goal.length; i++ )
        {
            goals.add( goal[i] );
        }
        executeMaven( pom, properties, goals, true );

        MavenProject project = readProject( pom );
        File projectOutputDir = new File( project.getBuild().getDirectory() );

        compareMackerOutput( baseDir, projectOutputDir );
    }

    private void executeMaven( File pom, Properties properties, List goals, boolean switchLocalRepo )
         throws TestToolsException
    {
        System.out.println( "  Building " + pom.getParentFile().getName() );

        new File( BUILD_OUTPUT_DIRECTORY ).mkdirs();

        NullPointerException npe = new NullPointerException();
        StackTraceElement[] trace = npe.getStackTrace();

        File buildLog = null;

        for ( int i = 0; i < trace.length; i++ )
        {
            StackTraceElement element = trace[i];
            String methodName = element.getMethodName();
            if ( methodName.startsWith( "test" ) && !methodName.equals( "testProject" ) )
            {
                String classname = element.getClassName();
                buildLog = new File( BUILD_OUTPUT_DIRECTORY, classname + "_" + element.getMethodName() + ".build.log" );
                break;
            }
        }

        if ( buildLog == null )
        {
            buildLog = new File( BUILD_OUTPUT_DIRECTORY, "unknown.build.log" );
        }

        if (properties == null)
        {
            properties = new Properties();
        }
        InvocationRequest request = buildTool.createBasicInvocationRequest( pom, properties, goals, buildLog );
        request.setUpdateSnapshots( false );
        request.setShowErrors( true );
        request.getProperties().setProperty( "downloadSources", "false" );
        request.getProperties().setProperty( "downloadJavadocs", "false" );

        // request.setDebug( true );
        if ( switchLocalRepo )
        {
            request.setLocalRepositoryDirectory( localRepositoryDirectory );
        }
        InvocationResult result = buildTool.executeMaven( request );

        if ( result.getExitCode() != 0 )
        {
            String buildLogUrl = buildLog.getAbsolutePath();
            try
            {
                buildLogUrl = buildLog.toURL().toExternalForm();
            }
            catch ( MalformedURLException e )
            {
            }
            throw new TestToolsException( "Failed to execute build.\nPOM: " + pom + "\nGoals: "
                    + StringUtils.join( goals.iterator(), ", " ) + "\nExit Code: " + result.getExitCode() + "\nError: "
                    + result.getExecutionException() + "\nBuild Log: " + buildLogUrl + "\n", result.getExecutionException() );
        }
    }

    private MavenProject readProject( File pom )
        throws TestToolsException
    {
        return projectTool.readProject( pom, localRepositoryDirectory );
    }

    /**
     * @param baseDir the base directory of the project
     * @param projectOutputDir the directory where the plugin will write the output files.
     */
    private void compareMackerOutput( File baseDir, File projectOutputDir )
            throws IOException, SAXException
    {
        File generatedFile = new File( projectOutputDir, "macker-out.xml" );
        assertTrue( "macker-out was not created", FileUtils.fileExists( generatedFile.getAbsolutePath() ) );
        new XmlComparer( baseDir.toString() + File.separator ).compareXml( EXPECTED_DIRECTORY_NAME + File.separator
                + "macker-out.xml", generatedFile );
    }
}
