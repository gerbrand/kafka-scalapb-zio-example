val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "kafka-protobuf-zio-example",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq("dev.zio" %% "zio" % "2.0.13",
    "dev.zio" %% "zio-kafka" % "2.3.0",
      "io.apicurio" % "apicurio-registry-serdes-protobuf-serde" % "2.4.2.Final"),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    ),
      libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
  )
