organization in ThisBuild := "regsystem.dp"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.7"

lazy val userApi = project("user-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPubSub,
      lagomJavadslApi,
      "org.projectlombok" % "lombok" % "1.16.12"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val userImpl = project("user-impl")
  .enablePlugins(LagomJava)
  .dependsOn(userApi, registrationApi)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPubSub,
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit,
      "org.projectlombok" % "lombok" % "1.16.12"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val registrationApi = project("registration-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPubSub,
      lagomJavadslApi,
      "org.projectlombok" % "lombok" % "1.16.12"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val registrationImpl = project("registration-impl")
  .enablePlugins(LagomJava)
  .dependsOn(registrationApi, userApi)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPubSub,
      lagomJavadslBroker,
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit,
      "org.projectlombok" % "lombok" % "1.16.12"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val performanceTests = project("performance-tests")
  .enablePlugins(GatlingPlugin)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "net.liftweb" % "lift-json_2.10" % "2.5.1",
      "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.3" % "test",
      "io.gatling" % "gatling-test-framework" % "2.2.3" % "test"
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

def project(id: String) = Project(id, base = file(id))
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))
  .settings(jacksonParameterNamesJavacSettings: _*) // applying it to every project even if not strictly needed.

// See https://github.com/FasterXML/jackson-module-parameter-names
lazy val jacksonParameterNamesJavacSettings = Seq(
  javacOptions in compile += "-parameters"
)

fork in run := true