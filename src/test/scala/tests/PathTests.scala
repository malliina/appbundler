package tests

import java.nio.file.Paths

import org.scalatest.FunSuite

import scala.compat.Platform
import scala.sys.process.Process

class PathTests extends FunSuite {
  val src = Paths get "testsrc"
  val dest = Paths get "testdest"
  val includes = Seq("a/")
  val excludes = Seq("a/b/f2.txt", "a/b/c/")

  ignore("File.list()") {
    val p = Paths get ""
    val paths = p.toFile.list()
    println(paths.mkString(Platform.EOL))
  }

  ignore("java_home script") {
    val out = Process("/usr/libexec/java_home").lines.head
    println(out)
  }

  //    test("Files.*") {
  //      FileCopier.copy(IncludeConf(
  //        src,
  //        dest,
  //        includes.map(s => Paths.get(s)),
  //        excludes.map(s => Paths.get(s))))
  //      assert(Files.exists(dest / "a" / "a.txt"))
  //    }
}
