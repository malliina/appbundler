package tests

import java.nio.file.Paths

import org.scalatest.FunSuite

import scala.sys.process.Process

/**
 * test
 * a
 * b
 * c
 * f1.txt
 * f2.txt
 * f3.txt
 * a.txt
 * b
 *
 * @author Michael
 */
class PathTests extends FunSuite {
  val src = Paths get "testsrc"
  val dest = Paths get "testdest"
  val includes = Seq("a/")
  val excludes = Seq("a/b/f2.txt", "a/b/c/")

  //  test("Files.*") {
  //    FileCopier.copy(IncludeConf(
  //      src,
  //      dest,
  //      includes.map(s => Paths.get(s)),
  //      excludes.map(s => Paths.get(s))))
  //    assert(Files.exists(dest / "a" / "a.txt"))
  //  }
//  test("File.list()") {
//    val p = Paths get ""
//    val paths = p.toFile.list()
//    println(paths.mkString(Platform.EOL))
//  }
  test("java_home script") {
    val out = Process("/usr/libexec/java_home").lines.head
    println(out)
  }
}
