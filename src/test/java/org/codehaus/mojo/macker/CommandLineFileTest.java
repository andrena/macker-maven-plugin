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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.codehaus.mojo.macker.forked.SetArgs;

public class CommandLineFileTest
    extends TestCase
{

    private final File TEST_FILE;

    public CommandLineFileTest( String name )
        throws IOException
    {
        super( name );
        TEST_FILE = new File( "target/cmdlineargs.txt" ).getCanonicalFile();
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        SetArgs.reset();
    }

    protected void tearDown()
        throws Exception
    {
        TEST_FILE.delete();
        super.tearDown();
    }

    public void testInvoke()
        throws Exception
    {
        CommandLineFile.main( new String[] { SetArgs.class.getName() } );
        assertNotNull( SetArgs.getLastArgs() );
        assertEquals( 0, SetArgs.getLastArgs().length );
    }

    public void testInvokeEmptyFile()
        throws Exception
    {
        FileWriter out = new FileWriter( TEST_FILE );
        out.flush();
        out.close();

        CommandLineFile.main( new String[] { SetArgs.class.getName(), TEST_FILE.toString() } );
        assertNotNull( SetArgs.getLastArgs() );
        assertEquals( 0, SetArgs.getLastArgs().length );
    }

    private void writeArgsFile(List/*<String>*/ lines)
        throws IOException
    {
        Writer out = new OutputStreamWriter(new FileOutputStream( TEST_FILE ), "UTF-8" );
        IOUtils.writeLines( lines, "\n", out);
        out.flush();
        out.close();
    }

    public void testInvokeParamFile()
        throws Exception
    {
        String CHEROKEE_LETTER_A = "\u13A0";

        writeArgsFile(Arrays.asList( new String[] { "aaa", CHEROKEE_LETTER_A, " tralala " } ));

        CommandLineFile.main( new String[] { SetArgs.class.getName(), TEST_FILE.toString() } );
        assertNotNull( SetArgs.getLastArgs() );
        assertEquals( 3, SetArgs.getLastArgs().length );
        assertEquals( "aaa", SetArgs.getLastArgs()[0] );
        assertEquals( CHEROKEE_LETTER_A, SetArgs.getLastArgs()[1] );
        assertEquals( " tralala ", SetArgs.getLastArgs()[2] );
    }

    public void testInvokeHugeParamFile()
        throws Exception
    {
        final int max = 100000;

        List/*<String>*/lines = new ArrayList/*<String>*/();
        for ( int i = 0; i < max; i++ )
        {
            lines.add( "a2b4c6d8x" );
        }

        writeArgsFile( lines );

        CommandLineFile.main( new String[] { SetArgs.class.getName(), TEST_FILE.toString() } );
        assertNotNull( SetArgs.getLastArgs() );
        assertEquals( max, SetArgs.getLastArgs().length );
        assertEquals( "a2b4c6d8x", SetArgs.getLastArgs()[0] );
        assertEquals( "a2b4c6d8x", SetArgs.getLastArgs()[max - 1] );
    }

    public void testInvokeException()
        throws Exception
    {
        writeArgsFile(Arrays.asList( new String[] { "uoex" } ));

        try
        {
            CommandLineFile.main( new String[] { SetArgs.class.getName(), TEST_FILE.toString() } );
            fail( "UnsupportedOperationException expected" );
        }
        catch ( UnsupportedOperationException ex )
        {
            assertNotNull( SetArgs.getLastArgs() );
            assertEquals( 1, SetArgs.getLastArgs().length );
            assertEquals( "uoex", SetArgs.getLastArgs()[0] );
        }
    }
}
