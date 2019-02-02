scalaVersion := "2.12.8"
resolvers += Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.malliina" %% "sbt-utils-maven" % "0.10.1")
