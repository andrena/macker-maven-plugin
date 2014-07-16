/*
 *  (c) tolina GmbH, 2014
 */
package de.andrena.tools.macker.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import de.andrena.tools.macker.Macker;
import de.andrena.tools.macker.event.ListenerException;
import de.andrena.tools.macker.event.MackerIsMadException;
import de.andrena.tools.macker.rule.RuleSeverity;
import de.andrena.tools.macker.rule.RulesException;
import de.andrena.tools.macker.structure.ClassParseException;

public class LinkedMacker implements de.andrena.tools.macker.plugin.Macker
{

    private final Macker macker;

    public LinkedMacker()
    {
        macker = new Macker();
    }

    @Override
    public void addClass(final File clazz)
            throws IOException, MojoExecutionException
    {
        try
        {
            macker.addClass( clazz );
        }
        catch ( final ClassParseException e )
        {
            throw new MojoExecutionException( String.format( "Error parsing classfile '%s'", clazz.getName() ), e );
        }
    }

    @Override
    public void addRulesFile(final File rule)
            throws IOException, MojoExecutionException
    {
        try
        {
            macker.addRulesFile( rule );
        }
        catch ( final RulesException e )
        {
            throw new MojoExecutionException( String.format( "Error parsing rule file '%s'", rule.getName() ), e );
        }
    }

    @Override
    public void check()
            throws MojoExecutionException, MojoFailureException
    {
        try
        {
            macker.check();
        }
        catch ( final MackerIsMadException e )
        {
            throw new MojoFailureException( "" );
        }
        catch ( final RulesException e )
        {
            throw new MojoExecutionException( "", e );
        }
        catch ( final ListenerException e )
        {
            throw new MojoExecutionException( "", e );
        }
    }

    @Override
    public void setAngerThreshold(final String anger)
    {
        macker.setAngerThreshold( RuleSeverity.fromName( anger ) );
    }

    @Override
    public void setPrintMaxMessages(final int maxMsg)
    {
        macker.setPrintMaxMessages( maxMsg );
    }

    @Override
    public void setPrintThreshold(final String print)
    {
        macker.setPrintThreshold( RuleSeverity.fromName( print ) );
    }

    @Override
    public void setVariable(final String name, final String value)
    {
        macker.setVariable( name, value );
    }

    @Override
    public void setVerbose(final boolean verbose)
    {
        macker.setVerbose( verbose );
    }

    @Override
    public void setXmlReportFile(final File report)
            throws IOException
    {
        macker.setXmlReportFile( report );
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

}
