import Dependencies._
name := "FastAndFurious"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies := akka ++ json ++ fp ++ persistance ++ tests ++ logging
