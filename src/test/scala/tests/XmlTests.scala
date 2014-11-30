package tests

import java.nio.file.Paths

import com.mle.appbundler.{InfoPlistConf, PlistWriter}
import org.scalatest.FunSuite

/**
 * @author Michael
 */
class XmlTests extends FunSuite {
  test("xml writer") {
    val conf = InfoPlistConf(
      "Test display name",
      "name",
      "id",
      "ver",
      "main",
      Paths get "jvmruntime",
      Seq("option 1", "option 2"),
      Seq("arg1", "arg2"))
    //    PlistWriter.write(conf, Paths get "E:\\Info.plist")
    val xml = PlistWriter.stringify(conf)
    println(xml)
  }
}
