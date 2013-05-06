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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.cli.shell.Shell;
/**
 * Special version of Plexus CLI Shell that forks to a JVM immediately without
 * creating a command line.  It uses the current java.home setting to find the java
 * executable.  So we have more memory for command line arguments.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public class JavaShell
    extends Shell
{

    public JavaShell()
    {
        this( new String[0] );
    }

    public JavaShell( List vmArgs )
    {
        this( (String[]) vmArgs.toArray( new String[vmArgs.size()] ) );
    }

    public JavaShell( String[] vmArgs )
    {
        String javaPath = System.getProperty( "java.home" ) + File.separator + "bin";
        setShellCommand( javaPath + File.separator + "java" );
        setQuotedExecutableEnabled( false );
        setQuotedArgumentsEnabled( false );
        setShellArgs( vmArgs );
    }

    protected List getRawCommandLine( String executable, String[] arguments )
    {
        List commandLine = new ArrayList();

        if ( executable != null )
        {
            commandLine.add( getExecutable() );
        }
        for ( int i = 0; i < arguments.length; i++ )
        {
            commandLine.add( arguments[i] );
        }
        return commandLine;
    }
}
