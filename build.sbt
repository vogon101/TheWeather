name := "TheWeather"

version := "0.1"

scalaVersion := "2.12.4"

// https://mvnrepository.com/artifact/org.apache.commons/commons-compress
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.16.1"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"

unmanagedBase := baseDirectory.value / "lib-jars"


mainClass in (Compile, run) := Some("com.vogonjeltz.weather.Plotting")