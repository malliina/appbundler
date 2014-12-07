# appbundler #

A work in progress port of Oracle's appbundler for OSX.

For SBT integration, see [sbt-packager](https://github.com/malliina/sbt-packager).

## Requirements ##

- Must not rely on Ant
- Must support Oracle Java 7 and later
- Must be able to include a suitable JRE in the .app package
- Must be able to run the app as a service (launchd)

## Usage ##

To create a .pkg installer of your application, create an instance of `com.mle.appbundler.Installer`
and then run `Installer.macPackage()`.

Example:

```
import java.nio.file.{Path, Paths}
import com.mle.appbundler.{InfoPlistConf, Installer}
val outDir: Path = ???
val plist = InfoPlistConf(
  displayName = "My App",
  name = "myapp",
  identifier = "com.github.malliina.myapp",
  version = "0.0.1",
  mainClass = "com.github.malliina.myapp.Start",
  jars = Seq("myapp.jar", "lib.jar", "other_lib.jar") map (jar => Paths.get(jar))
)
```

To create an app using the above config, use:

```
Installer(rootOutput = outDir, infoPlistConf = plist).macPackage()
```

To create an app that starts automatically when the machine boots (using launchd), use:

```
val launchd = LaunchdConf.defaultSettings(plist.displayName, plist.identifier)
Installer(rootOutput = outDir, infoPlistConf = plist, launchdConf = Some(launchd)).macPackage()
```