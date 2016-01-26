# BTrace Maven Support

Provides Maven integration for [BTrace](https://github.com/jbachorik/btrace)

__Version: 1.3.4-0__


## Components

### BTrace Maven Plugin (btrace-maven-plugin)

Allows compilation of BTrace scripts as a part of the Maven project lifecycle.

```
<plugins>
    <plugin>
	<groupId>io.btrace</groupId>
	<artifactId>btrace-maven-plugin</artifactId>
	<version>1.3.4</version>
	<executions>
	    <execution>
		<goals>
		    <goal>btracec</goal>
		</goals>
	    </execution>
	</executions>
    </plugin>
    ...
</plugins>
```

The plugin configuration accepts the following parameters:
* __sourceRoots__ - where the sources to compile are located (_default: ${project.build.sourceDirectory}_)
* __classPath__ - additional class path for compilation
* __outputDirectory__ - where to put the compiled binaries (_default: ${project.build.outputDirectory}_)


### BTrace Project Archetype (btrace-project-archetype)

A simple archetype to generate a functional scaffolding for a project containing BTrace scripts.

To bootstrap a new project use

```
mvn archetype:generate
  -DgroupId=[your project's group id]
  -DartifactId=[your project's artifact id]
  -DarchetypeArtifactId=btrace-project-archetype
```
