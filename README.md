# Software for the lego line written in LeJOS NXJ

This is an attempt at replacing the current matlab code, which has faults including:
* Poor error handling
* A lack of structured data, leading to lots of string parsing
* requiring multiple open instances of matlab
* inability to be run locally on the bricks

With code written in java.

Currently, the software is designed to be run completely PC side. In future, it would be sensible to distribute the code between bricks and PC, and device a custom communication protocol between them, as the <acronym title="Lego Communication Protocol">LCP</acronym> used normally is somewhat restrictive.

## Getting set up

* Install the [Eclipse IDE for java developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/marsr)
* Install the [Java SE Development Kit 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) (LeJOS does not work with the newer 8)
* Install the patched version of lejos (see below).
* Install the [LeJOS NXJ plugin](http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm) into eclipse
  * Then open Window → Preferences → LeJOS, and change the LeJOS Home variable to point at the patched version
* Import this project into eclipse, using Import projects → git → clone

### Patched LeJOS

There is a patched LeJOS version at https://github.com/lego-line/lejos. This patch fixes bugs related to remote motor control, including:
* Changing the speed of a running motor
* Allowing a motor to be floated when stopped
* Making remote control threadsafe
* Allowing motors to be used in unregulated mode  (ie for intentional stalling)

A build of the patch version is committed in the [`snapshot`](https://github.com/lego-line/lejos/tree/master/snapshot) directory. Download this directory to your computer.

You'll then need to run `snapshot/bin/flashg.bat` to program the NXT with the snapshot firmware.
Finally, the eclipse plugin should be pointed at this snapshot directory.

#### Rebuilding the snapshot

**This shouldn't be necessary, normally**

To rebuild the patched version, check out the git repository from above, and import the "snapshot" project into eclipse. Then right click on the build.xml file within the project, and hit "Run as → Ant build".
This will rebuild the snapshot.


## Running the sample

A sample program can be found in legoline.tools.ManualControl.java. This finds connected bricks with names starting "Merge" and "Feeder", and provides a GUI for controlling each one.
