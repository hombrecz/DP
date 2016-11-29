organization in ThisBuild := "regsystem.dp"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.7"

lagomCassandraCleanOnStart in ThisBuild := true

lazy val playerApi = project("player-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies += lagomJavadslApi,
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val playerImpl = project("player-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )
  .dependsOn(playerApi)

lazy val registrationApi = project("registration-api")
  .settings(version := "1.0-SNAPSHOT")
  .settings(
    libraryDependencies += lagomJavadslApi,
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

lazy val registrationImpl = project("registration-impl")
  .settings(version := "1.0-SNAPSHOT")
  .enablePlugins(LagomJava)
  .dependsOn(registrationApi, playerApi)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit
    ),
    javacOptions ++= Seq("-encoding", "UTF-8")
  )

def project(id: String) = Project(id, base = file(id))
  .settings(eclipseSettings: _*)
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))
  .settings(jacksonParameterNamesJavacSettings: _*) // applying it to every project even if not strictly needed.


// See https://github.com/FasterXML/jackson-module-parameter-names
lazy val jacksonParameterNamesJavacSettings = Seq(
  javacOptions in compile += "-parameters"
)

// Configuration of sbteclipse
// Needed for importing the project into Eclipse
lazy val eclipseSettings = Seq(
  EclipseKeys.projectFlavor := EclipseProjectFlavor.Java,
  EclipseKeys.withBundledScalaContainers := false,
  EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
  EclipseKeys.eclipseOutput := Some(".target"),
  EclipseKeys.withSource := true,
  EclipseKeys.withJavadoc := true,
  // avoid some scala specific source directories
  unmanagedSourceDirectories in Compile := Seq((javaSource in Compile).value),
  unmanagedSourceDirectories in Test := Seq((javaSource in Test).value)
)

fork in run := true