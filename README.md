[![Build Status](https://buildhive.cloudbees.com/job/andrena/job/macker-maven-plugin/badge/icon)](https://buildhive.cloudbees.com/job/andrena/job/macker-maven-plugin/)

This is a [fork from Codehaus](http://mojo.codehaus.org/macker-maven-plugin/) ([source](http://svn.codehaus.org/mojo/trunk/sandbox/macker-maven-plugin/)), who initiated and developed this project.

The intent of this fork is solely to ensure availability in Maven Central and updating it as necessary to provide support for today's JVMs (for details, see the [Macker project](https://github.com/andrena/macker)).

Usage:

```
<build>
  [...]
  <plugin>
    <groupId>de.andrena.tools.macker</groupId>
      <artifactId>macker-maven-plugin</artifactId>
      <version>1.0.0</version>
      <configuration>
        <rule>macker-rules.xml</rule>
      </configuration>
      <executions>
        <execution>
          <phase>compile</phase>
          <goals>
            <goal>macker</goal>
          </goals>
        </execution>
      </executions>
  </plugin>
  [...]
</build>
```
