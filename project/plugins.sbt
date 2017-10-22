scalaVersion := "2.12.4"
resolvers += Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.malliina" %% "sbt-utils" % "0.7.1")
