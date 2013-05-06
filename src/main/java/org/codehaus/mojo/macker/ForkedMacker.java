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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;

/**
 * Forking to invoke the Macker tool.  Based on
 * <code>org.codehaus.mojo.cobertura.tasks.AbstractTask</code>.
 * This uses the Shell from CommandLine for forking. In Windows XP this has
 * a max of 8kb command line arguments.  So we have to use a command file.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public class ForkedMacker
    implements Macker
{
    private static final String COMMAND_CLASS = CommandLineFile.class.getName();
    private static final String TASK_CLASS = "net.innig.macker.Macker";

    private List/*<String>*/options = new ArrayList/*<String>*/();
    private List/*<String>*/rules = new ArrayList/*<String>*/();
    private List/*<String>*/classes = new ArrayList/*<String>*/();
    private Log log = new SystemStreamLog();
    private String maxmem;
    private List/*<Artifact>*/pluginClasspathList = Collections.EMPTY_LIST;
    private boolean quiet;

    private String createClasspath()
        throws MojoExecutionException
    {
        StringBuffer cpBuffer = new StringBuffer();
        for ( Iterator it = pluginClasspathList.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();
            try
            {
                cpBuffer.append( artifact.getFile().getCanonicalPath() );
                if ( it.hasNext() )
                {
                    cpBuffer.append( File.pathSeparator );
                }
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while creating the canonical path for '" + artifact.getFile()
                        + "'.", e );
            }
        }

        return cpBuffer.toString();
    }

    private Commandline createCommandLine()
        throws MojoExecutionException
    {
        // Commandline cl = new Commandline();
        // cl.setExecutable( "java" );
        // cl.createArg().setValue( "-cp" );
        // cl.createArg().setValue( createClasspath() );
        // if ( maxmem != null )
        // {
        //     cl.createArg().setValue( "-Xmx" + maxmem );
        // }
        // cl.createArg().setValue( commandClass );
        List/*<String>*/jvmArguments = new ArrayList/*<String>*/();
        jvmArguments.add( "-cp" );
        jvmArguments.add( createClasspath() );
        if ( maxmem != null )
        {
            jvmArguments.add( "-Xmx" + maxmem );
        }
        Commandline cl = new Commandline( new JavaShell( jvmArguments ) );
        cl.setExecutable( COMMAND_CLASS );

        cl.createArg().setValue( TASK_CLASS );
        try
        {
            CommandLineBuilder builder = new CommandLineBuilder( "macker" );
            for ( Iterator/*<String>*/it = options.iterator(); it.hasNext(); )
            {
                builder.addArg( (String) it.next() );
            }
            for ( Iterator/*<String>*/it = rules.iterator(); it.hasNext(); )
            {
                builder.addArg( (String) it.next() );
            }
            for ( Iterator/*<String>*/it = classes.iterator(); it.hasNext(); )
            {
                builder.addArg( (String) it.next() );
            }
            builder.saveArgs();
            String commandsFile =  builder.getCommandLineFile();
            cl.createArg().setValue( commandsFile );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create CommandsFile.", e );
        }
        return cl;
    }

    private int executeJava()
        throws MojoExecutionException
    {
        Commandline cl = createCommandLine();

        StringStreamConsumer stdout = new StringStreamConsumer();
        StringStreamConsumer stderr = new StringStreamConsumer();
        if ( quiet )
        {
            StringStreamConsumer nullConsumer = new StringStreamConsumer()
            {
                public void consumeLine( String s )
                {
                }
            };
            stdout = nullConsumer;
            stderr = nullConsumer;
        }
        log.debug( "Working Directory: " + cl.getWorkingDirectory() );
        log.debug( "Executing command line:" );
        log.debug( cl.toString() );

        int exitCode;
        try
        {
            exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );
        }
        catch ( CommandLineException e )
        {
            throw new MojoExecutionException( "Unable to execute Macker.", e );
        }
        log.debug( "exit code: " + exitCode );

        String output = stdout.getOutput();
        if ( output.trim().length() > 0 )
        {
            log.debug( "--------------------" );
            log.debug( " Standard output from the Macker task:" );
            log.debug( "--------------------" );
            log.info( output );
            log.debug( "--------------------" );
        }
        String stream = stderr.getOutput();
        if ( stream.trim().length() > 0 )
        {
            log.debug( "--------------------" );
            log.debug( " Standard error from the Macker task:" );
            log.debug( "--------------------" );
            log.error( stderr.getOutput() );
            log.debug( "--------------------" );
        }
        return exitCode;
    }

    public void check()
        throws MojoExecutionException, MojoFailureException
    {
        int returnCode = executeJava();
        switch ( returnCode )
        {
        case 0:
            log.debug( "All checks passed." );
            break;
        case 2:
            log.debug( "Macker check failed." );
            throw new MojoFailureException( "MackerIsMadException during Macker execution" );
        default:
            log.error( "Macker check had errors. See messages above." );
            throw new MojoExecutionException( "Error during Macker execution" );
        }
    }

    public void setLog( Log log )
    {
        this.log = log;
    }

    public void setMaxmem( String maxmem )
    {
        this.maxmem = maxmem;
    }

    public void setPluginClasspathList( List/*<Artifact>*/pluginClasspathList )
    {
        this.pluginClasspathList = Collections.unmodifiableList( pluginClasspathList );
    }

    public void setQuiet( boolean quiet )
    {
        this.quiet = quiet;
    }

    public void addClass( File clazz )
        throws IOException
    {
        classes.add( clazz.getCanonicalPath() );
    }

    public void addRulesFile( File rule )
        throws IOException
    {
        // -r, --rulesfile <rules.xml>
        rules.add( "-r" );
        rules.add( rule.getCanonicalPath() );
    }

    public void setAngerThreshold( String anger )
    {
        // --anger <threshold>
        options.add( "--anger" );
        options.add( anger );
    }

    public void setPrintMaxMessages( int maxMsg )
    {
        // --print-max <max-messages>
        options.add( "--print-max" );
        options.add( "" + maxMsg );
    }

    public void setPrintThreshold( String print )
    {
        // --print <threshold>
        options.add( "--print" );
        options.add( print );
    }

    public void setVariable( String name, String value )
    {
        // -D, --define <var>=<value>
        options.add( "-D" );
        options.add( name + "=" + value );
    }

    public void setVerbose( boolean verbose )
    {
        if ( verbose )
        {
            // -v, --verbose
            options.add( "-v" );
        }
    }

    public void setXmlReportFile( File report )
        throws IOException
    {
        // -o, --output <report.xml>
        options.add( "-o" );
        options.add( report.getCanonicalPath() );
    }
}
