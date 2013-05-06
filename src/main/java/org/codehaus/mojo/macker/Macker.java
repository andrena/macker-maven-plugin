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

import java.io.File;
import java.io.IOException;

/**
 * Abstraction of the Macker tool.  There is a linked and a forked implementation.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public interface Macker
{

    /**
     * Add a class to be checked by Macker.
     * @throws IOException if there's a problem reading a file
     * @throws MojoExecutionException if there's a problem parsing a class
     */
    void addClass( File clazz )
        throws IOException, MojoExecutionException;

    /**
     * Add a rule file to be used by Macker.
     * @throws IOException if there's a problem reading a file
     * @throws MojoExecutionException if there's a problem parsing a rule file
     */
    void addRulesFile( File rule )
        throws IOException, MojoExecutionException;

    void check()
        throws MojoExecutionException, MojoFailureException;

    void setAngerThreshold( String anger );

    void setPrintMaxMessages( int maxMsg );

    void setPrintThreshold( String print );

    void setVariable( String name, String value );

    void setVerbose( boolean verbose );

    /**
     * Set the XML report file to be used by Macker.
     * @throws IOException if there's a problem with the report file
     */
    void setXmlReportFile( File report )
        throws IOException;

}