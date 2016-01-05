package de.andrena.tools.macker.plugin;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for future API-compatibility (because of reflection use)
 */
public class LinkedMackerTest
{
    @SuppressWarnings("javadoc")
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @SuppressWarnings("javadoc")
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    final LinkedMacker macker = new LinkedMacker();

    @SuppressWarnings("javadoc")
    @Test
    public void testAddClass()
            throws Exception
    {
        thrown.expect( MojoExecutionException.class );
        macker.addClass( temporaryFolder.newFile() );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testRulesFile()
            throws Exception
    {
        thrown.expect( MojoExecutionException.class );
        macker.addRulesFile( temporaryFolder.newFile() );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testCheck()
            throws Exception
    {
        macker.check();
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetAngerThreshold()
            throws Exception
    {
        macker.setAngerThreshold( "WARNING" );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testLog()
            throws Exception
    {
        macker.setLog( null );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testMaxmem()
            throws Exception
    {
        macker.setMaxmem( null );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetPluginClasspathList()
            throws Exception
    {
        macker.setPluginClasspathList( null );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetPrintMaxMessages()
            throws Exception
    {
        macker.setPrintMaxMessages( 12 );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetPrintThreshold()
            throws Exception
    {
        macker.setPrintThreshold( "ERROR" );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetQuiet()
            throws Exception
    {
        macker.setQuiet( false );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetVariable()
            throws Exception
    {
        macker.setVariable( null, null );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetVerbose()
            throws Exception
    {
        macker.setVerbose( false );
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSetXmlReportFile()
            throws Exception
    {
        macker.setXmlReportFile( null );
    }
}
