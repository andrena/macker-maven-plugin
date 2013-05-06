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
import java.io.FileWriter;
import java.io.IOException;

/**
 * Helper class for storing long command lines inside a temporary file.
 * <p>
 * Typical usage:
 *
 * <pre>
 *  builder = new CommandLineBuilder();
 *  builder.addArg(&quot;--someoption&quot;);
 *  builder.addArg(&quot;optionValue&quot;);
 *  ...
 *  builder.saveArgs();
 *  doSomething(builder.getCommandLineFile());
 *  builder.dispose();
 * </pre>
 *
 * It will save options in <code>builder.getCommandLineFile()</code>.  Options
 * will be stored one in a line.  Options are saved in UTF-8 encoding.
 *
 * @author Grzegorz Lukasik (Cobertura)
 */
public class CommandLineBuilder
{

    private static final String LINESEP = System.getProperty( "line.separator" );

    // File that will be used to store arguments
    private final File commandLineFile;

    // Writer that will be used to write arguments to the file
    private final FileWriter commandLineWriter;

    /**
     * Creates a new instance of the builder. Instances of this class should not
     * be reused to create many command lines.
     * @throws IOException if problems with creating temporary file for storing command line occur
     */
    public CommandLineBuilder( String name )
        throws IOException
    {
        commandLineFile = File.createTempFile( name + ".", ".cmdline" );
        commandLineFile.deleteOnExit();
        commandLineWriter = new FileWriter( commandLineFile );
    }

    /**
     * Adds command line argument. Each argument can be thought as a single cell
     * in array passed to main method. This method should not be used after
     * arguments were saved.
     *
     * @param arg command line argument to save
     * @throws IOException if problems with temporary file occur
     */
    public void addArg( String arg )
        throws IOException
    {
        if ( arg == null )
        {
            throw new IllegalArgumentException( "arg is null" );
        }
        commandLineWriter.write( arg + LINESEP );
    }

    /**
     * Adds two command line arguments. Convienience function, calls {@link #addArg(String)} two times.
     *
     * @param arg1 first command line argument to save
     * @param arg2 second command line argument to save
     * @throws IOException if problems with temporary file occur
     */
    public void addArg( String arg1, String arg2 )
        throws IOException
    {
        addArg( arg1 );
        addArg( arg2 );
    }

    /**
     * Saves options and made file available to use. Use method
     * {@link #getCommandLineFile} to get the file the arguments are saved in.
     * @throws IOException if problems with temporary file occur
     */
    public void saveArgs()
        throws IOException
    {
        commandLineWriter.flush();
        commandLineWriter.close();
    }

    /**
     * Gets absolute path to the file with saved arguments. Notice, that however
     * this method can be used as soon as an instance of this class is created,
     * arguments should be read from the file after a call to {@link #saveArgs} method.
     * @return absolute path to the file with arguments
     */
    public String getCommandLineFile()
        throws IOException
    {
        return commandLineFile.getCanonicalFile().getAbsolutePath();
    }

    /**
     * Explicity frees all resources associated with this instance. Result of
     * any other method call after disposing an instance of this class is unspecified.
     */
    public void dispose()
    {
        commandLineFile.delete();
    }

}
