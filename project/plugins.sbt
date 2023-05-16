addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

libraryDependencies ++= Seq(
  "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.0-test8",
  "com.thesamet.scalapb" %% "compilerplugin" % "0.11.11",
  "com.thesamet.scalapb" %% "scalapb-validate-codegen" % "0.3.2",
)
