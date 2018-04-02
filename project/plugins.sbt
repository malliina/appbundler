scalaVersion := "2.12.5"
resolvers += Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.malliina" %% "sbt-utils" % "0.8.0")
