package tests

import java.nio.file.{Path, Paths}

import com.malliina.appbundler.{InfoPlistConf, Installer, LaunchdConf}
import org.scalatest.FunSuite

class Samples extends FunSuite {
  ignore("can create installer") {
    val outDir: Path = ???
    val plist = InfoPlistConf(
      displayName = "My App",
      name = "myapp",
      identifier = "com.malliina.myapp",
      version = "0.0.1",
      mainClass = "com.malliina.myapp.Start",
      jars = Seq("myapp.jar", "lib.jar", "other_lib.jar") map (jar => Paths.get(jar))
    )
    val appInstaller = Installer(rootOutput = outDir, infoPlistConf = plist)
    val launchd = LaunchdConf.defaultSettings(plist.displayName, plist.identifier)
    val serviceInstaller = appInstaller.copy(launchdConf = Some(launchd))
  }
}
