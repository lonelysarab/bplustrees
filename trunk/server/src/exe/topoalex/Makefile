# Makefile for a program P in P.c and P.h, where P is 
# a make parameter

# For example, for a program in hello_world.cpp, invoke by:

#  make -k P=hello_world

# The variables OBJS and HDRS below need to be tuned to whatever classes
# are needed by your program

# Linker flags
# LDLIBS  =  -L/usr/X11R6/lib -lglut -lGLU -lGL -lm -ldl -lXi -lXmu
# LDLIBS  =  -L/usr/X11R6/lib -lm -ldl -lXi -lXmu
LDLIBS  =  -lm 
# C compiler flags
CFLAGS = -g 
# The commands to call the C and C++ compilers
CC = gcc 
C++ = g++
# Where to find the include files:
INCLUDE = -I/usr/X11R6/include
# The name of the output program 
PROG = grapdriv
# Object files that go into the final executable
OBJS = grapdriv.o  
# Header files 
# HDRS = randgen.h stack.h stack.cpp

########
# Finally, the commands that actually make stuff
# You shouldn't have to change anything here 
# These commands are of the form:
#   output_thing:  stuff it depends on
#   <TAB> command to make output_thing 
########

# re-link the program when the object files change
$(PROG):  $(OBJS)
#	$(C++) $(OBJS) $(LDLIBS) -o $(PROG) 
	$(CC) $(OBJS) $(LDLIBS) -o $(PROG) 

# change a C object file whenever the corresponding source file
# or any of the header files changes
%.o : %.c  
	$(CC) -c $(CFLAGS) $(INCLUDE) $<
# What this means: 
# The %.o and %.c mean any .o file and the corresponding .c file
# All .c's are recompiled if you change one of the header files
# The $< symbol refers to the FIRST dependancy - so don't try to
# put $(HDRS) before %.c!

# change a C++ object file whenever the corresponding source file
# or any of the header files changes
%.o : %.cpp 
	$(C++) -c $(CFLAGS) $(INCLUDE) $<

clean:
	rm -f *.o *~ $(PROG)
