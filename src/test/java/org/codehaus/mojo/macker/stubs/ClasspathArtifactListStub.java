package org.codehaus.mojo.macker.stubs;

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

import org.apache.maven.plugin.testing.stubs.ArtifactStub;

import java.io.File;
import java.util.ArrayList;

/**
 * An artifact list stub which populates itself using the java.class.path.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public class ClasspathArtifactListStub
    extends ArrayList/*<Artifact>*/
{
    public ClasspathArtifactListStub()
    {
        String[] elements = System.getProperty( "java.class.path" ).split( File.pathSeparator );
        for ( int i = 0; i < elements.length; i++ )
        {
            final String classPathElement = elements[i];
            add( new ArtifactStub()
            {
                public File getFile()
                {
                    return new File( classPathElement );
                }
            } );
        }
    }
}
