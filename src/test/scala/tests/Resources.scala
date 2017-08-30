package tests

import com.malliina.appbundler.AppBundler
import org.scalatest.FunSuite

class Resources extends FunSuite {
  test("can find resource") {
    AppBundler.resource("JavaAppLauncher")
  }
}
