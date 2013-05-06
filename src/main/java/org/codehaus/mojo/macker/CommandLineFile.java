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

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Wrapper for calling a Java main method with a number of arguments.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public class CommandLineFile
{

    public static void main( String[] args )
        throws Exception
    {
        if ( args.length == 0 || args.length > 2 )
        {
            System.err.println( "Usage: CommandLineFile <main class> [command line arguments file]" );
            System.exit( 1 );
        }

        String className = args[0];
        Class clazz = Class.forName( className );
        Method main = clazz.getMethod( "main", new Class[] { String[].class } );

        List/*<String>*/lines = new ArrayList/*<String>*/();
        if ( args.length == 2 )
        {
            Reader in = new InputStreamReader( new FileInputStream( args[1] ), "UTF-8" );
            try
            {
                lines = IOUtils.readLines( in );
            }
            finally
            {
                in.close();
            }
        }

        try
        {
            main.invoke( null, new Object[] { lines.toArray( new String[lines.size()] ) } );
        }
        catch ( InvocationTargetException ex )
        {
            Throwable cause = ex.getTargetException();
            if ( cause instanceof Error )
            {
                throw (Error) cause;
            }
            throw (Exception) cause;
        }
    }
}
