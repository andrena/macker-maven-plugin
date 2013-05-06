package org.codehaus.mojo.macker.it;

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

/**
 * This class executes the IT tests. The setup will create a pom-test.xml from the plugin pom. The version is changed
 * to "test" and the tests themselves turned off to avoid an infinite loop. The test version of the plugin is then
 * built and installed to a new temporary local repo used to execute the tests. This only occurs once for the suite of
 * tests. Each test below just uses the tools to execute Maven on the named project with the passed in goals.
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a> (Copied from the ounce-maven-plugin
 * copied from the Eclipse AbstractEclipsePluginTestCase v2.4)
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:fgiust@apache.org">Fabrizio Giustina</a>
 */
public class MackerMojoIT
    extends AbstractMackerPluginITCase
{

    public void testBasic()
        throws Exception
    {
        testProject( "basic", "clean,test" );
    }

    public void testFail()
        throws Exception
    {
        testProject( "fail", "clean,test" );
    }

}
