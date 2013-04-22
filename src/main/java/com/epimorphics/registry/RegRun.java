/******************************************************************
 * File:        UKLReg.java
 * Created by:  Dave Reynolds
 * Created on:  27 Nov 2012
 *
 * (c) Copyright 2012, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.registry;

import java.io.File;
import java.net.URL;

import org.apache.catalina.startup.Tomcat;

public class RegRun {
    public static final String HELP = "--help";

    public static final String USAGE = "java -jar bench [webapp-root]";

    public static void main(String[] args) throws Exception {
        String root = "src/main/webapp";
        if (args.length == 1 && ! HELP.equals(args[0])) {
            root = args[0];
        } else if (args.length != 0) {
            System.out.println("Usage: " + USAGE);
            System.exit(0);
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.setBaseDir(".");

        String contextPath = "/";

        File rootF = new File(root);
        if (!rootF.exists()) {
            rootF = new File(".");
        }
        if (!rootF.exists()) {
            System.err.println("Can't find root app: " + root);
            System.exit(1);
        }

        org.apache.catalina.Context context = tomcat.addWebapp(contextPath,  rootF.getAbsolutePath());
        context.setConfigFile(new URL("file:src/main/webapp/META-INF/context.xml"));

        tomcat.start();
        tomcat.getServer().await();

      }

}
