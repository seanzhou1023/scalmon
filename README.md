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

sbt "run --ui t"

### Graphical Interface

Based on scala.swing.

sbt "run --ui g"

## Roadmap

Version 0.1:

    Basic logic is working. The scalmons can (theorectial) fight each other.

Version 0.2:

    Basic Textual Interface is working.

Version 0.3:

    Basic GUI Interface is working.

Version 0.4:

    Basic Web Interface is working.

Version 0.5:

    General improvements. Finetuning on game logic esp. balancing.