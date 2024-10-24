# BTrace Maven Support

Provides Maven integration for [BTrace](https://github.com/btraceio/btrace)

__Version: 2.2.5__

## Components

### BTrace Maven Plugin (btrace-maven-plugin)

Allows compilation of BTrace scripts as a part of the Maven project lifecycle.

```
<plugins>
    <plugin>
	<groupId>io.btrace</groupId>
	<artifactId>btrace-maven-plugin</artifactId>
	<version>2.2.5</version>
	<executions>
	    <execution>
		<goals>
		    <goal>btracec</goal>
		</goals>
	    </execution>
	</executions>
	<configuration>
	    <classpathElements>
	        <classpathElement>/path/to/jar_or_folder</classpathElement>
	        ...
	    </classpathElements>
	</configuration>
    </plugin>
    ...
</plugins>
```

The plugin configuration accepts the following parameters:
* __sourceDirectory__ - where the sources to compile are located (_default: ${project.build.sourceDirectory}_)
* __classPathElements__ - additional class path for compilation
* __outputDirectory__ - where to put the compiled binaries (_default: ${project.build.outputDirectory}_)


### BTrace Project Archetype (btrace-project-archetype)

A simple archetype to generate a functional scaffolding for a project containing BTrace scripts.

To bootstrap a new project use

```
mvn archetype:generate
  -DgroupId=[your project's group id]
  -DartifactId=[your project's artifact id]
  -DarchetypeGroupId=io.btrace 
  -DarchetypeArtifactId=btrace-project-archetype
  -DarchetypeVersion=2.2.5
```

## Usage

The artifacts are hosted by [Maven Central](https://mvnrepository.com/artifact/io.btrace/btrace-maven-plugin)

Modify your maven __settings.xml__ to include the following section:
```
...
<profiles>
	<profile>
		<repositories>
			<repository>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
				<id>central</id>
				<name>ossrh-central</name>
				<url>https://oss.sonatype.org</url>
			</repository>
    		</repositories>
		<pluginRepositories>
			<pluginRepository>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
				<id>central</id>
				<name>ossrh-central</name>
				<url>https://oss.sonatype.org</url>
			</pluginRepository>
		</pluginRepositories>
		<id>ossrh</id>
	</profile>
</profiles>
<activeProfiles>
	<activeProfile>ossrh</activeProfile>
</activeProfiles>
</settings>
...
```
