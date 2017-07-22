scalaVersion := "2.10.6"
resolvers += Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.malliina" %% "sbt-utils" % "0.6.3")
