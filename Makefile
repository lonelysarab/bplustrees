# This target is used to re-build all sources

all:
	printf "****** Making the core\n"; \
	cd core; rm -r build; rm -r dist; ant; \
	printf "****** Making the client\n"; \
	cd ../client; rm -r build; rm -r dist; ant; \
	printf "****** Making gaigs\n"; \
	cd ../gaigs; rm -r build; rm -r dist; ant; \
	printf "****** Making the simple GaffVisualizer\n"; \
	cd ../gaff; rm -r build; rm -r dist; ant; \
	printf "****** Making the server\n"; \
	cd ../server; rm -r build; rm -r dist; ant

# Use this target to insert the contents of text.txt into files as per
# David Furcy's instructions:

# Suppose you want to insert some lines of text in all .java files in the directory structure rooted at 'dir':

# 1. in dir, create a file called text.txt containing your lines of text
# 2. in dir, create a Makefile containing:

# insert:
#     cp text.txt text.tmp; cat ${F} >> text.tmp; mv text.tmp ${F}


# 3. in dir, type at the prompt:

# find . -name '*.java' -exec make -s insert F={} \;

# This solution works (as far as I can tell after a couple of tests;
# no guarantees beyond that 8^) using a temporary file called
# 'txt.tmp' in dir. At least one case where it will not work is when a
# .java file name contains blank spaces. But I don't think that this
# case will show up in jhave, right?

#insert:
#	cp text.txt text.tmp; cat ${F} >> text.tmp; mv text.tmp ${F}

insert:
	cp gnu_insert.txt text.tmp; cat ${F} >> text.tmp; mv text.tmp ${F}
