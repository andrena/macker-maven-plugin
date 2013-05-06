This is a [fork from Codehaus](http://mojo.codehaus.org/macker-maven-plugin/) ([source](http://svn.codehaus.org/mojo/trunk/sandbox/macker-maven-plugin/)), who initiated and developed this project.

The intent of this fork is solely to ensure availability in Maven Central and updating it as necessary to provide support for today's JVMs.

Usage:

```
<build>
  [...]
  <plugin>
    <groupId>org.github.benromberg</groupId>
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