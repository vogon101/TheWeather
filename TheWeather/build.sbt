name := "TheWeather"

version := "0.1"

scalaVersion := "2.12.4"

updateOptions := updateOptions.value.withGigahorse(false)

resolvers += "Boundless" at "http://repo.boundlessgeo.com/main"

resolvers += "OSGEO" at "http://download.osgeo.org/webdav/geotools/"

// https://mvnrepository.com/artifact/org.apache.commons/commons-compress
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.16.1"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"

// https://mvnrepository.com/artifact/org.geotools/gt-main
//libraryDependencies += "org.geotools" % "gt-main" % "19.0"

// https://mvnrepository.com/artifact/org.geotools/gt-shapefile
libraryDependencies += "org.geotools" % "gt-shapefile" % "19.0"


unmanagedBase := baseDirectory.value / "lib-jars"


mainClass in (Compile, run) := Some("com.vogonjeltz.weather.app.NewCacheTest")