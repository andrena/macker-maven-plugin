ProjectTool.java is part of the org.apache.maven.plugin-testing:maven-plugin-testing-tools:1.2.
It was copied from Revision 677115 2008-07-16 00:16:13Z from the source.

I needed to set a Java property called "maven-plugin-testing-tools:ProjectTool:packageProjectArtifact" 
to the forked JVM executing Maven call, to change the behavior in the POM. This is necessary as the
helpmojo execution has to be disabled. It makes IT preparation using maven-plugin-testing-tools fail.
(The same problem exists in the org.apache.maven.plugins:maven-eclipse-plugin, but there integration
tests are NOT run before install phase but only on demand in a separate profile.)

~~~ Peter
