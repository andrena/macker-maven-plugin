[![Build Status](https://travis-ci.org/arxes-tolina/macker-maven-plugin.svg?branch=master)](https://travis-ci.org/arxes-tolina/macker-maven-plugin)

This is a [fork from Codehaus](http://mojo.codehaus.org/macker-maven-plugin/) ([source](http://svn.codehaus.org/mojo/trunk/sandbox/macker-maven-plugin/)), who initiated and developed this project.

The intent of this fork is solely to ensure availability in Maven Central and updating it as necessary to provide support for today's JVMs (for details, see the [Macker project](https://github.com/andrena/macker)).

Usage:

```xml
<build>
  [...]
  <plugin>
    <groupId>de.andrena.tools.macker</groupId>
      <artifactId>macker-maven-plugin</artifactId>
      <version>1.0.2</version>
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

Some examples for macker-rules.xml (more sophisticated examples are available on the [original Macker project page](http://innig.net/macker/guide/)): 

If you don’t want any classes from your persistence layer to reference any of your application-logic classes (which is usually the case), you can define the following Rule-Set:
```xml
<?xml version="1.0"?>
<!DOCTYPE macker PUBLIC "-//innig//DTD Macker 0.4//EN" "http://innig.net/macker/dtd/macker-0.4.dtd">
<macker>
  <ruleset name="Persistence layer cannot reference application-logic">
    <access-rule>
      <deny>
        <from class="myproject.persistence.**"/>
        <to class="myproject.app.**"/>
      </deny>
    </access-rule>
  </ruleset>
</macker>
```

You can also define more complicated, generic Rule-Sets. For example, if you want to enforce the convention, that classes in “internal” packages can only be accessed by themselves and their parent package, you could define the following Rule-Set:
```xml
<?xml version="1.0"?>
<!DOCTYPE macker PUBLIC "-//innig//DTD Macker 0.4//EN" "http://innig.net/macker/dtd/macker-0.4.dtd">
<macker>
  <ruleset name="Internal package may only be referenced by parent package">
    <foreach var="module" class="(**).internal.**">
      <pattern name="parent" class="${module}.**"/>
      <pattern name="inside" class="${module}.internal.**"/>
      <pattern name="outside">
        <exclude pattern="inside"/>
      </pattern>

      <access-rule>
        <message>${from-full} cannot access internal classes from ${module}</message>
        <deny>
          <from pattern="outside"/>
          <to pattern="inside"/>
          <allow>
            <from pattern="parent"/>
          </allow>
        </deny>
      </access-rule>
    </foreach>
  </ruleset>
</macker>
```
This would allow access from package myproject.app.* to myproject.app.internal.*, but not from myproject.web.* to myproject.app.internal.*. Note that myproject.app.subpackage.* could also access myproject.app.internal.*, but the rule could easily be modified to prevent subpackage access to internal packages.
