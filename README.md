scalmon
=======

A pokemon-like game, implemented in scala as student project work.

## Setup Dev Environment

    git clone https://github.com/themerius/scalmon.git
    cd scalmon
    sbt eclipse

In Eclipse (Scala IDE) use the Import Wizard to
import Existing Projects into Workspace.
Import scalmon as existing project into your workspace
(only as reference, copy is _not_ necessary, so you can
use terminal sbt and eclipse and git as usual).
[https://github.com/typesafehub/sbteclipse]

## Run the Tests

    sbt test

To run the test specifications in eclipse you can install
the plugin from the Scala IDE bundle:
[http://www.scalatest.org/user_guide/using_scalatest_with_eclipse]

## Run specific GUI

### Textual Interface

    sbt "run --ui t -s 3"

### Graphical Interface

Based on scala.swing.

    sbt "run --ui g -s 3"

## Assemble fat JAR

With the sbt plugin https://github.com/sbt/sbt-assembly it is possible to
bundle a stand alone jar file with all needed dependencies included. Just type:

    sbt assembly

Now in `target/scala-2.10` a `scalmon-assembly-0.2.0.jar` (or newer) appears.
This jar file can be run on every computer with java installed,
for a game with three animals and GUI, just type:

    java -jar scalmon-assembly-0.2.0.jar -u g -s 3

## Code Coverage

To get a code coverage report type:

    sbt jacoco:cover

In the folder target/scala-2.10/jacoco/html is the html report.

## Roadmap

Version 0.1:

    Basic logic is working. The scalmons can (theorectial) fight each other.

Version 0.2:

    Basic GUI Interface is working.

Version 0.3:

    Basic Textual Interface is working.

Version 0.4:

    Basic Web Interface is working.

Version 0.5:

    General improvements. Finetuning on game logic esp. balancing.
