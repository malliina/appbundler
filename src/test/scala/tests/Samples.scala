package tests

import java.nio.file.{Path, Paths}

import com.malliina.appbundler.{LaunchdConf, InfoPlistConf}
import com.malliina.appbundler.Installer
import org.scalatest.FunSuite

/**
 * @author Michael
 */
class Samples extends FunSuite {
  test("can create installer") {
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
