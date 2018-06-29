name := "TheWeatherWeb"
 
version := "1.0" 
      
lazy val `theweatherweb` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

// https://mvnrepository.com/artifact/org.apache.commons/commons-compress
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.16.1"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"

//updateOptions := updateOptions.value.withGigahorse(false)

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

