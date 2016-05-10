/*
 * Copyright (c) 2016, Jaroslav Bachorik <j.bachorik@btrace.io>.
 * All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Copyright owner designates
 * this particular file as subject to the "Classpath" exception as provided
 * by the owner in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package io.btrace.mvnplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author Jaroslav Bachorik
 */
@Mojo(name = "btracec", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class BTracec extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
    private String sourceDirectory;

    @Parameter(name = "classpathElements", required = false )
    private List<String> classpathElements;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true )
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> sources = collectFiles();
        if (sources.isEmpty()) {
            getLog().info("Nothing to compile.");
            return;
        }

        StringBuilder ccp = new StringBuilder();
        URLClassLoader ucl = (URLClassLoader)getClass().getClassLoader();
        for(URL u : ucl.getURLs()) {
            ccp.append(u.getPath()).append(File.pathSeparatorChar);
        }
        try {
            for(Object e : project.getCompileClasspathElements()) {
                ccp.append(e).append(File.pathSeparatorChar);
            }
        } catch (Throwable t) {
            getLog().error(t);
        }
        if (classpathElements != null) {
            for(String cpe : classpathElements) {
                ccp.append(cpe).append(File.pathSeparatorChar);
            }
        }

        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList(
            "java",
            "-cp",
            ccp.toString(),
            "com.sun.btrace.compiler.Compiler",
            "-d",
            outputDirectory.getAbsolutePath())
        );
        args.addAll(sources);

        ProcessBuilder pb = new ProcessBuilder(args);

        try {
            Process p = pb.start();
            int exit = p.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = br.readLine()) != null) {
                getLog().error("[btracec] " + line);
                exit = 1;
            }
            BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line1;
            while ((line1 = br1.readLine()) != null) {
                getLog().info("[btracec] " + line1);
            }
            if (exit != 0) {
                throw new MojoFailureException("Error running compiler");
            }
        } catch (Exception e) {
            throw new MojoFailureException("Error executing compiler", e);
        }
    }

    private List<String> collectFiles() throws MojoFailureException {
        final List<String> fileList = new ArrayList<>();
        Path scrPath = new File(sourceDirectory).toPath();
        try {
            Files.walkFileTree(scrPath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    boolean isJava = file.toString().endsWith("java");
                    if (isJava) {
                        String c = new String(Files.readAllBytes(file));
                        if (c.contains("@BTrace")) {
                            fileList.add(file.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new MojoFailureException("Source file inaccessible", e);
        }
        return fileList;
    }
}
