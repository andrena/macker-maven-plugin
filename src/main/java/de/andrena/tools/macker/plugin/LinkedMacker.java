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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

class LinkedMacker implements de.andrena.tools.macker.plugin.Macker
{
    private final Object macker;

    private final Method addClass;
    private final Method addRulesFile;
    private final Method check;
    private final Method setAngerThreshold;
    private final Method setPrintMaxMessages;
    private final Method setPrintThreshold;
    private final Method setVariable;
    private final Method setVerbose;
    private final Method setXmlReportFile;

    private final Class<?> ruleSeverity;
    private final Class<?> classParseException;
    private final Class<?> mackerIsMadException;
    private final Class<?> rulesException;
    private final Class<?> listenerException;

    LinkedMacker()
    {
        try
        {
            macker = Class.forName( "de.andrena.tools.macker.Macker" ).newInstance();

            ruleSeverity = Class.forName( "de.andrena.tools.macker.rule.RuleSeverity" );
            classParseException = Class.forName( "de.andrena.tools.macker.structure.ClassParseException" );
            mackerIsMadException = Class.forName( "de.andrena.tools.macker.event.MackerIsMadException" );
            rulesException = Class.forName( "de.andrena.tools.macker.rule.RulesException" );
            listenerException = Class.forName( "de.andrena.tools.macker.event.ListenerException" );

            addClass = macker.getClass().getMethod( "addClass", File.class );
            addRulesFile = macker.getClass().getMethod( "addRulesFile", File.class );
            check = macker.getClass().getMethod( "check" );
            setAngerThreshold = macker.getClass().getMethod( "setAngerThreshold", ruleSeverity );
            setPrintMaxMessages = macker.getClass().getMethod( "setPrintMaxMessages", Integer.TYPE );
            setPrintThreshold = macker.getClass().getMethod( "setPrintThreshold", ruleSeverity );
            setVariable = macker.getClass().getMethod( "setVariable", String.class, String.class );
            setVerbose = macker.getClass().getMethod( "setVerbose", Boolean.TYPE );
            setXmlReportFile = macker.getClass().getMethod( "setXmlReportFile", File.class );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void addClass(final File clazz)
            throws IOException, MojoExecutionException
    {
        try
        {
            invoke( addClass, clazz );
        }
        catch ( final RuntimeException e )
        {
            if ( e.getCause().getClass().equals( classParseException.getClass() ) )
            {
                throw new MojoExecutionException( String.format( "Error parsing classfile '%s'", clazz.getName() ), e.getCause() );
            }
            throw new MojoExecutionException( "", e.getCause() );
        }
    }

    @Override
    public void addRulesFile(final File rule)
            throws IOException, MojoExecutionException
    {
        try
        {
            invoke( addRulesFile, rule );
        }
        catch ( final RuntimeException e )
        {
            if ( e.getCause().getClass().equals( rulesException.getClass() ) )
            {
                throw new MojoExecutionException( String.format( "Error parsing rule file '%s'", rule.getName() ), e.getCause() );
            }
            throw new MojoExecutionException( "", e.getCause() );
        }
    }


    @Override
    public void check()
            throws MojoExecutionException, MojoFailureException
    {
        try
        {
            invoke( check );
        }
        catch ( final RuntimeException e )
        {
            final Throwable cause = e.getCause().getCause();
            if ( mackerIsMadException.isAssignableFrom( cause.getClass() ) )
            {
                throw new MojoFailureException( "" );
            }
            if ( rulesException.isAssignableFrom( cause.getClass() ) )
            {
                throw new MojoExecutionException( "", cause );
            }
            if ( listenerException.isAssignableFrom( cause.getClass() ) )
            {
                throw new MojoExecutionException( "", cause );
            }
            throw e;

        }
    }

    @Override
    public void setAngerThreshold(final String anger)
    {
        invoke( setAngerThreshold, getRuleSeverityFromName( anger ) );
    }

    @Override
    public void setPrintMaxMessages(final int maxMsg)
    {
        invoke( setPrintMaxMessages, Integer.valueOf( maxMsg ) );
    }

    @Override
    public void setPrintThreshold(final String print)
    {
        invoke( setPrintThreshold, getRuleSeverityFromName( print ) );
    }

    @Override
    public void setVariable(final String name, final String value)
    {
        invoke( setVariable, name, value );
    }

    @Override
    public void setVerbose(final boolean verbose)
    {
        invoke( setVerbose, Boolean.valueOf( verbose ) );
    }

    @Override
    public void setXmlReportFile(final File report)
            throws IOException
    {
        invoke( setXmlReportFile, report );
    }

    @Override
    public void setLog(final Log log)
    {
    }

    @Override
    public void setMaxmem(final String maxmem)
    {
    }

    @Override
    public void setPluginClasspathList(final List<Artifact> collectArtifactList)
    {
    }

    @Override
    public void setQuiet(final boolean quiet)
    {
    }


    private Object getRuleSeverityFromName(final String value)
    {
        final Object severity;
        try
        {
            final Method method = ruleSeverity.getDeclaredMethod( "fromName", String.class );
            severity = method.invoke( ruleSeverity, value );
        }
        catch ( final InvocationTargetException e )
        {
            throw new RuntimeException( e );
        }
        catch ( final IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
        catch ( final SecurityException e )
        {
            throw new RuntimeException( e );
        }
        catch ( final NoSuchMethodException e )
        {
            throw new RuntimeException( e );
        }
        return severity;
    }

    private void invoke(final Method method, Object... objects)
    {
        try
        {
            method.invoke( macker, objects );
        }
        catch ( final InvocationTargetException e )
        {
            throw new RuntimeException( e );
        }

        catch ( final IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }

}
