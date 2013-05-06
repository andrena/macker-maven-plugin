package org.codehaus.mojo.macker;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;
import org.xml.sax.SAXException;

public class MackerMojoTest
    extends AbstractMojoTestCase
{
    private static final String TEST_PROJECT = "target/test/unit";
    private static final String TEST_TARGET = TEST_PROJECT + "/target/";
    private static final String TEST_POM_LOCATION = "src/test/resources/unit/";

    private final XmlComparer comparer = new XmlComparer( TEST_POM_LOCATION );

    protected void setUp() throws Exception
    {
        super.setUp();

        File testTarget = new File( getBasedir(), TEST_TARGET );
        FileUtils.deleteDirectory( testTarget );
        testTarget.mkdirs();

        final String exampleClassesPath = "org/codehaus/mojo/macker/classes";
        File exampleClassesTarget = new File( testTarget, "classes/" + exampleClassesPath );
        exampleClassesTarget.mkdirs();
        FileUtils.copyDirectory( new File( getBasedir(), "target/test-classes/" + exampleClassesPath ), exampleClassesTarget );

        final String testClassesPath = "org/codehaus/mojo/macker/testclasses";
        File testClassesTarget = new File( testTarget, "test-classes/" + testClassesPath );
        testClassesTarget.mkdirs();
        FileUtils.copyDirectory( new File( getBasedir(), "target/test-classes/" + testClassesPath ), testClassesTarget );
    }

    private File copyPom( String source )
        throws IOException
    {
        final File testPom = new File( getBasedir(), TEST_PROJECT + "/pom.xml" );
        FileUtils.copyFile( new File( getBasedir(), TEST_POM_LOCATION + source ), testPom );
        return testPom;
    }

    private void executeMackerMojo( String configXml )
        throws Exception
    {
        File testPom = copyPom( configXml );
        MackerMojo mojo = (MackerMojo) lookupMojo( "macker", testPom );
        assertNotNull( mojo );
        mojo.execute();
    }

    private void executeMackerMojoFails( String configXml )
        throws Exception
    {
        try
        {
            executeMackerMojo( configXml );
            fail( "MojoFailureException should be thrown." );
        }
        catch ( MojoFailureException e )
        {
            assertTrue( true );
        }
    }

    private void executeMackerMojoError( String configXml )
        throws Exception
    {
        try
        {
            executeMackerMojo( configXml );
            fail( "MojoExecutionException should be thrown." );
        }
        catch ( MojoExecutionException e )
        {
            assertTrue( true );
        }
    }

    private void assertNoOutput()
    {
        File generatedFile = new File( getBasedir(), TEST_TARGET + "macker-out.xml" );
        assertFalse( generatedFile.exists() );
    }

    private void assertOutput( String configFolder )
        throws SAXException, IOException
    {
        File generatedFile = new File( getBasedir(), TEST_TARGET + "macker-out.xml" );
        assertTrue( "macker-out was not created", FileUtils.fileExists( generatedFile.getAbsolutePath() ) );
        comparer.compareXml( configFolder + File.separator + "macker-out.xml", generatedFile );
    }

    public void testDefaultConfiguration() throws Exception
    {
        // POM configures a ruleset that does not fail on the given classes
        executeMackerMojo( "default-configuration-plugin-config.xml" );
        assertOutput( "default-configuration" );
    }

    public void testNotFailOnViolation() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        // but failOnError is false
        executeMackerMojo( "notfailonviolation-plugin-config.xml" );
        assertOutput("violation-configuration");
    }

    public void testNotFailOnViolationButBroken() throws Exception
    {
        // POM configures plugin with a wrong value
        executeMackerMojoError( "broken-notfailon-plugin-config.xml" );
        assertNoOutput();
    }

    public void testFailOnViolation() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        executeMackerMojoFails( "failonviolation-plugin-config.xml");
        assertOutput("violation-configuration");
    }

    public void testFailOnBroken() throws Exception
    {
        // POM configures plugin with a wrong value
        executeMackerMojoError( "broken-configuration-plugin-config.xml" );
        assertNoOutput();
    }

    public void testSkipped() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        // but the whole check is skipped
        executeMackerMojo( "skip-plugin-config.xml" );
        assertNoOutput();
    }

    public void testIgnoreTestClasses() throws Exception
    {
        // POM configures a ruleset that fails on the given test classes
        // but the test classes are not configured zo execute
        executeMackerMojo( "notfailontestclasses-plugin-config.xml" );
        assertOutput( "notfailontestclasses-configuration" );
    }

    public void testFailInTestClasses() throws Exception
    {
        // POM configures a ruleset that fails on the given test classes
        executeMackerMojoFails( "failontestclasses-plugin-config.xml" );
        assertOutput( "testclasses-configuration" );
    }

    public void testIgnoreMissingTestClassesWhenIncluded() throws Exception
    {
        // POM configures a include tests
        // but test-classes folder is not there
        executeMackerMojo( "includetestswithoutclasses-configuration-plugin-config.xml" );
        assertOutput( "default-configuration" );
    }

    public void testSingleRuleInList() throws Exception
    {
        // POM configures two rulesets that each fail on the given classes
        executeMackerMojo( "onerule-configuration-plugin-config.xml" );
        assertOutput( "default-configuration" );
    }

    public void testMultipleRules() throws Exception
    {
        // POM configures two rulesets that each fail on the given classes
        executeMackerMojoFails( "tworule-configuration-plugin-config.xml" );
        assertOutput( "double-configuration" );
    }

    public void testExcludes() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        // but the offending class is excluded
        executeMackerMojo( "excludefailonviolation-plugin-config.xml" );
        assertOutput( "excludefailonviolation-configuration" );
    }

    public void testIncludes() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        // but the offending class is not included
        executeMackerMojo( "notincludefailonviolation-plugin-config.xml" );
        assertOutput( "excludefailonviolation-configuration" );
    }

    public void testFileURL() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        File testPom = copyPom( "norule-configuration-plugin-config.xml" );
        MackerMojo mojo = (MackerMojo) lookupMojo( "macker", testPom );
        assertNotNull( mojo );
        URL url = getClass().getClassLoader().getResource( "unit/default-configuration/macker-rules.xml" );
        mojo.setRules( new String[] { url.toString() } );
        mojo.execute();

        assertOutput( "default-configuration" );
    }

    public void testClasspathRules() throws Exception
    {
        // POM configures a rulesets from classpath that fails on the given classes
        executeMackerMojoFails( "classpath-configuration-plugin-config.xml" );
        assertOutput("violation-configuration");
    }

    public void testFailOnVariable() throws Exception
    {
        // POM configures a ruleset that fails on the given classes
        executeMackerMojoFails( "variableusage-plugin-config.xml" );
        assertOutput("violation-configuration");
    }

}
