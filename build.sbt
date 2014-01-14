name := "scalmon"

version := "0.4.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases",
  "sonatype-public" at "https://oss.sonatype.org/content/groups/public",
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.2.3"
  val sprayV = "1.2.0"
  Seq(
    "org.scala-lang"      %   "scala-swing"    % "2.10.3",
    "com.github.scopt"    %%  "scopt"          % "3.1.0",
    "org.scalatest"       %   "scalatest_2.10" % "2.0.RC3" % "test",
    "commons-codec"       %   "commons-codec"  % "1.8",
    "io.spray"            %   "spray-can"      % sprayV,
    "io.spray"            %   "spray-routing"  % sprayV,
    "io.spray"            %   "spray-testkit"  % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"     % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"   % akkaV
  )
}

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

// BuildInfo
buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "htwg.scalmon"

// BuildInfo auch in Eclipse als Source verwenden
EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed

assemblySettings
