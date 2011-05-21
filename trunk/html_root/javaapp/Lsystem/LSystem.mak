# Microsoft Developer Studio Generated NMAKE File, Format Version 4.20
# ** DO NOT EDIT **

# TARGTYPE "Java Virtual Machine Java Workspace" 0x0809

!IF "$(CFG)" == ""
CFG=LSystem - Java Virtual Machine Debug
!MESSAGE No configuration specified.  Defaulting to LSystem - Java Virtual\
 Machine Debug.
!ENDIF 

!IF "$(CFG)" != "LSystem - Java Virtual Machine Release" && "$(CFG)" !=\
 "LSystem - Java Virtual Machine Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE on this makefile
!MESSAGE by defining the macro CFG on the command line.  For example:
!MESSAGE 
!MESSAGE NMAKE /f "LSystem.mak" CFG="LSystem - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "LSystem - Java Virtual Machine Release" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE "LSystem - Java Virtual Machine Debug" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 
################################################################################
# Begin Project
JAVA=jvc.exe

!IF  "$(CFG)" == "LSystem - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\Turtle.class" "$(OUTDIR)\TurtleFrame.class"\
 "$(OUTDIR)\LSystem.class"

CLEAN : 
	-@erase "$(INTDIR)\LSystem.class"
	-@erase "$(INTDIR)\Turtle.class"
	-@erase "$(INTDIR)\TurtleFrame.class"

# ADD BASE JAVA /O
# ADD JAVA /O

!ELSEIF  "$(CFG)" == "LSystem - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\Turtle.class" "$(OUTDIR)\TurtleFrame.class"\
 "$(OUTDIR)\LSystem.class"

CLEAN : 
	-@erase "$(INTDIR)\LSystem.class"
	-@erase "$(INTDIR)\Turtle.class"
	-@erase "$(INTDIR)\TurtleFrame.class"

# ADD BASE JAVA /g
# ADD JAVA /g

!ENDIF 

################################################################################
# Begin Target

# Name "LSystem - Java Virtual Machine Release"
# Name "LSystem - Java Virtual Machine Debug"

!IF  "$(CFG)" == "LSystem - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "LSystem - Java Virtual Machine Debug"

!ENDIF 

################################################################################
# Begin Source File

SOURCE=.\LSystem.java

!IF  "$(CFG)" == "LSystem - Java Virtual Machine Release"


"$(INTDIR)\Turtle.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\TurtleFrame.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\LSystem.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "LSystem - Java Virtual Machine Debug"


"$(INTDIR)\Turtle.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\TurtleFrame.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\LSystem.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
# End Target
# End Project
################################################################################
