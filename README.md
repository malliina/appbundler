[![Build Status](https://github.com/malliina/appbundler/workflows/Test/badge.svg)](https://github.com/malliina/appbundler)
[![Maven Central](https://img.shields.io/maven-central/v/com.malliina/appbundler_2.12.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.malliina%22%20AND%20a%3A%22appbundler_2.13%22)

# appbundler

A port of Oracle's appbundler for OSX.

For SBT integration, see [sbt-packager](https://github.com/malliina/sbt-packager).

## Features

- Does not rely on Ant
- Supports Oracle Java 7 and later
- Can include a suitable JRE in the .app package
- Can optionally run the app as a service (using launchd)

## Installation

    "com.malliina" %% "appbundler" % "1.5.0"

## Usage

First, create an `InfoPlistConf` instance representing your app:

    import java.nio.file.{Path, Paths}
    import com.malliina.appbundler.{InfoPlistConf, Installer}
    
    val plist = InfoPlistConf(
      displayName = "My App",
      name = "myapp",
      identifier = "com.malliina.myapp",
      version = "0.0.1",
      mainClass = "com.malliina.myapp.Start",
      jars = Seq("myapp.jar", "lib.jar", "other_lib.jar") map (jar => Paths.get(jar))
    )

To create an installer of your app with the above config, use:

    Installer(rootOutput = ???, infoPlistConf = plist).macPackage()

To create an installer that also starts the app automatically when the machine boots (using launchd), use:

    val launchd = LaunchdConf.defaultSettings(plist.displayName, plist.identifier)
    Installer(rootOutput = ???, infoPlistConf = plist, launchdConf = Some(launchd)).macPackage()

To further customize the installer and app packaging, check the various parameters to `InfoPlistConf`, `Installer`
and `LaunchdConf`, then eventually run `Installer.macPackage()`.
