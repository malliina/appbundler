package tests

import com.malliina.util.Util
import org.scalatest.FunSuite

/**
 * @author mle
 */
class Resources extends FunSuite {
  test("can find resource") {
    Util.resource("JavaAppLauncher")
  }
}
