


name := "service-base"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.13"
ThisBuild / version      := "1.0-SNAPSHOT"


//-----------------------------------------------------------------------------
// PROJECT
//-----------------------------------------------------------------------------

lazy val root = project.in(file("."))
  .settings(settings)
  .settings(
    libraryDependencies ++= Seq(
    "org.scalatest"  %% "scalatest"  % "3.1.1" % Test,
    "de.dnpm.dip"    %% "core"       % "1.0-SNAPSHOT",
    "de.dnpm.dip"    %% "icd-api"    % "1.0-SNAPSHOT",
    "de.ekut.tbi"    %% "validators" % "1.0-SNAPSHOT",
    "de.ekut.tbi"    %% "generators" % "1.0-SNAPSHOT" % Test
   )
 )


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings

lazy val compilerOptions = Seq(
  "-encoding", "utf8",
  "-unchecked",
  "-Xfatal-warnings",
  "-feature",
  "-language:higherKinds",
  "-language:postfixOps",
  "-deprecation"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++=
    Seq("Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository") ++
    Resolver.sonatypeOssRepos("releases") ++
    Resolver.sonatypeOssRepos("snapshots")
)
