scalaVersion := "2.12.10"
resolvers += Resolver.url("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))(Resolver.ivyStylePatterns)

Seq(
	"com.malliina" %% "sbt-utils-maven" % "0.15.0",
	"ch.epfl.scala" % "sbt-bloop" % "1.3.4",
  	"org.scalameta" % "sbt-scalafmt" % "2.3.0"
) map addSbtPlugin
