name    := "Todos NG"

version := "0.1"

scalaVersion := "2.8.1"

unmanagedBase <<= baseDirectory { base => base / "sbt_jars" }

artifactPath in Compile in packageBin <<=
  baseDirectory { base => base / "jars" / "application.jar" }

resolvers ++= Seq(
  "scala-tools" at "http://nexus.scala-tools.org/content/repositories/public",
  "codahale"    at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
  "org.eclipse.jetty"   % "jetty-client"         % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-server"         % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-servlet"        % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-continuation"   % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-websocket"      % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-xml"            % "7.4.1.v20110513",
  "org.eclipse.jetty"   % "jetty-jmx"            % "7.4.1.v20110513",
  "org.jruby.ext.posix" % "jnr-posix"            % "1.1.8",
  "org.jruby.extras"    % "constantine"          % "0.7",
  "org.jruby.extras"    % "jffi"                 % "1.0.10",
  "org.jruby.extras"    % "jaffl"                % "0.5.11",
  "jline"               % "jline"                % "0.9.94",
  "ch.qos.logback"      %  "logback-classic"     % "0.9.28",
  "com.foursquare"      %% "rogue"               % "1.0.22" intransitive(),
  "net.liftweb"         %% "lift-mongodb-record" % "2.4-M2",
  "com.codahale"        %% "simplespec"          % "0.3.4" % "test->default"
)

seq(coffeescript.CoffeeScript.coffeeSettings: _*)

bare in Coffee := true

targetDirectory in Coffee := file("lib")

seq(sbtassembly.Plugin.assemblySettings: _*)

outputPath in Assembly := file("jars") / "dependencies.jar"

// test in Assembly := {}
