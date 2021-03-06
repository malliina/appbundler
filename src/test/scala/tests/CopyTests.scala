package tests

import java.nio.file.{Files, Paths}

import com.malliina.appbundler.{IncludeConf, AppBundler}

class CopyTests extends munit.FunSuite {
  test("can copy directory".ignore) {
    val javaHomeDir = Paths get "/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home"
    val testOut = Paths get "/tmp/jdk8/"
    Files.createDirectories(testOut)
    val conf = IncludeConf(javaHomeDir, testOut, AppBundler.includePaths, AppBundler.excludePaths)
    AppBundler.copyRuntime(conf.src, testOut)
  }
}
