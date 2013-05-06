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

import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The mock project needed for Macker MOJO unit tests.
 * @author <a href="http://www.code-cop.org/">Peter Kofler</a>
 */
public class ProjectStub
    extends MavenProjectStub
{
    private static final String TEST_PROJECT_BASE_DIR = "target/test/unit";
    private Map/*<String, Artifact>*/pluginArtifactMap = new HashMap/*<String, Artifact>*/();

    public ProjectStub()
    {
        // mock the POM location for the ResourceManager's searchPath
        setFile( new File( getBasedir(), TEST_PROJECT_BASE_DIR + "/pom.xml" ) );

        // mock the build for the ResourceManager's outputDirectory
        Build build = new Build();
        build.setDirectory( getBasedir() + File.separator + (TEST_PROJECT_BASE_DIR + "/target/") );
        setBuild( build );

        // mock the artifact for the language handler check
        setArtifact( new JavaArtifactHandlerArtifactStub() );

        // mock specific plugin (which is this plugin) in the map to get its classpath for forking
        getPluginArtifactMap().put( "org.codehaus.mojo:macker-maven-plugin", new ArtifactStub()
        {
            public File getFile()
            {
                return new File( "target/classes" );
            }
        } );
    }

    public Map/*<String, Artifact>*/getPluginArtifactMap()
    {
        return pluginArtifactMap;
    }

    public void setPluginArtifactMap( Map/*<String, Artifact>*/pluginArtifactMap )
    {
        this.pluginArtifactMap = pluginArtifactMap;
    }

}
