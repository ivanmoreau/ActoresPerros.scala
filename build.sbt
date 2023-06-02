ThisBuild / scalaVersion := Version.scala

val sharedSettings = Seq(
  scalaVersion := Version.scala,
  scalacOptions ++= Seq(
    "-explain",
    "-explain-types",
    // Extra Warnings
    "-deprecation",
    "-feature",
    "-unchecked",
    // Extra flags
    "-Ykind-projector:underscores",
    "-Xfatal-warnings"
  )
)

wartremoverErrors ++= Warts.unsafe

lazy val core =
  project
    .settings(sharedSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.typelevel"     %% "cats-core"           % Version.cats,
        "org.typelevel"     %% "cats-effect"         % Version.catsEffect,
        "org.typelevel"     %% "mouse"               % Version.mouse,
        "co.fs2"            %% "fs2-core"            % Version.fs2,
        "co.fs2"            %% "fs2-io"              % Version.fs2,
        "org.scalatest"     %% "scalatest"           % Version.scalatest          % Test,
        "org.scalatest"     %% "scalatest-wordspec"  % Version.scalatest          % Test,
        "org.scalatestplus" %% "scalacheck-1-17"     % Version.scalatestPlusCheck % Test
      )
    )
