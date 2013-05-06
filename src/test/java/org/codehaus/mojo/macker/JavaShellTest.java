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

import java.io.File;

import junit.framework.TestCase;

import org.codehaus.mojo.macker.forked.ExitArgs;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;

public class JavaShellTest
    extends TestCase
{
    private String classPath;
    private static final String FORKED_CLASS = ExitArgs.class.getName();

    public JavaShellTest( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        classPath = new File( "./target/test-classes" ).getCanonicalPath();
    }

    private int execute( Commandline cl )
        throws CommandLineException
    {
        StringStreamConsumer stdout = new StringStreamConsumer();
        StringStreamConsumer stderr = new StringStreamConsumer();
        return CommandLineUtils.executeCommandLine( cl, stdout, stderr );
    }

    public void testDefaultShellCall()
        throws CommandLineException
    {
        Commandline cl = new Commandline();
        cl.setExecutable( "java" );
        cl.createArg().setValue( "-cp" );
        cl.createArg().setValue( classPath );
        cl.createArg().setValue( FORKED_CLASS );
        cl.createArg().setValue( "oneArg" );
        cl.createArg().setValue( "2Arg" );
        cl.createArg().setValue( "3 Arg" );

        int exitCode = execute( cl );
        assertEquals( 3, exitCode );
    }

    public void skip_testDefaultShellMaxArguments()
        throws CommandLineException
    {
        Commandline cl = new Commandline();
        cl.setExecutable( "java" );
        cl.createArg().setValue( "-cp" );
        cl.createArg().setValue( classPath );
        cl.createArg().setValue( FORKED_CLASS );
        final int max = 807; // win XP approx. 8kb
        for ( int i = 0; i < max; i++ )
        {
            cl.createArg().setValue( "a2b4c6d8x" ); // 9 plus blank is 10
        }

        int exitCode = execute( cl );
        assertEquals( max, exitCode );
    }

    public void testJavaShellCall()
        throws CommandLineException
    {
        Commandline cl = new Commandline( new JavaShell( new String[] { "-cp", classPath } ) );
        cl.setExecutable( FORKED_CLASS );
        cl.createArg().setValue( "oneArg" );
        cl.createArg().setValue( "2Arg" );
        cl.createArg().setValue( "3 Arg" );

        int exitCode = execute( cl );
        assertEquals( 3, exitCode );
    }

    public void skip_testJavaShellMaxArguments()
        throws CommandLineException
    {
        Commandline cl = new Commandline( new JavaShell( new String[] { "-cp", classPath } ) );
        cl.setExecutable( FORKED_CLASS );
        final int max = 3261; // win XP approx. 32kb
        for ( int i = 0; i < max; i++ )
        {
            cl.createArg().setValue( "a2b4c6d8x" );
        }

        int exitCode = execute( cl );
        assertEquals( max, exitCode );
    }

}
