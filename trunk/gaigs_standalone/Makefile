## NOTE: This file is only for compiling the application Main.java,
## which allows you to load a show file and then display the snapshots
## using a GaigsAV Visualizer.  Should you add structures or other
## code to the gaigs2 package and want those compiled, put the source
## code file in jhave2/gaigs/src/gaigs2 directory.  Then re-compile
## from the jhave2/gaigs directory by issuing the command "ant" at the
## prompt.

## To compile with new distribution -- June 2005 -- This setting for
## the classpath may have to change as we progress through the summer
CLIENTCODE = ../gaigs/dist/gaigs.jar:../core/dist/core.jar:../client/build/classes:../server/lib/xercesImpl.jar:../server/lib/xml-apis.jar:../gaigs/lib/jdom.jar

test:
	export CLASSPATH=..:.:$(CLIENTCODE); \
	printf "****** Making  the test\n"; \
	javac Main.java

### Use this if you want to compile Main and run directly from the
### Makefile.  Why?  Because it sets the CLASSPATH correctly
test_and_run:
	export CLASSPATH=..:.:$(CLIENTCODE); \
	printf "****** Making and running the test\n"; \
	javac Main.java; \
	java gaigs_standalone.Main

### Use this if you want to run (only) directly from the Makefile.
### Why?  Because it sets the CLASSPATH correctly
run:
	export CLASSPATH=..:.:$(CLIENTCODE); \
	printf "****** Making and running the test\n"; \
	java gaigs_standalone.Main > foo.out
