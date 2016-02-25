
name := "hello"

  version := "1.0"

  scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.0.1",
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % "2.0.1",
  "org.elasticsearch.plugin" % "shield" % "2.0.1"// from "https://maven.elasticsearch.org/releases/org/elasticsearch/plugin/shield/2.0.1/shield-2.0.1.jar",
)

resolvers ++= Seq(
  "elasticsearch-releases" at "https://maven.elasticsearch.org/releases"
)

credentials += Credentials("Artifactory Realm", "maven.elasticsearch.org", "", "")