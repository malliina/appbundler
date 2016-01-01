package tests

import java.nio.file.{Files, Paths}

import com.malliina.appbundler.{IncludeConf, AppBundler}
import org.scalatest.FunSuite

/**
 * @author mle
 */
class CopyTests extends FunSuite {
  test("can copy directory") {
    val javaHomeDir = Paths get "/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home"
    val testOut = Paths get "/tmp/jdk8/"
    Files.createDirectories(testOut)
    val conf = IncludeConf(javaHomeDir, testOut, AppBundler.includePaths, AppBundler.excludePaths)
    AppBundler.copyRuntime(conf.src, testOut)
  }
}
