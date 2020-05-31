package tests

import com.malliina.appbundler.AppBundler

class Resources extends munit.FunSuite {
  test("can find resource") {
    AppBundler.resource("JavaAppLauncher")
  }
}
