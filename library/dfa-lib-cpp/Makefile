######################################################################
# Environment variables

MACOS 		   ?= 1
DFANALYZER_DIR ?= ./dfanalyzer
target     := lib/libdfanalyzer.so

###############################################################################
# File management.  This is where the source, header, and object files are
# defined

#
# source files
srcfiles :=  $(wildcard src/*.cpp)

CXXFLAGS := -I./include -g -fPIC 
ifeq (${MACOS}, 1) 
	CXXFLAGS += -w -std=c++11
endif

LDFLAGS  := -lcurl -shared -fPIC

CXX      := g++

#
# object files
objects		:= $(patsubst %.cpp, %.o, $(srcfiles))
###############################################################################


.PHONY: dust clean distclean

###############################################################################
# Target:
#


if($(PREFIX)x == x)

all: clean $(target)

# Production rules:  how to make the target - depends on library configuration
$(target): $(objects)
	@echo "Linking "$@"..."
	@$(CXX) $(CXXFLAGS) $(objects) -o $@ $(LDFLAGS)


# Useful rules.
dust:
	@echo "Deleting old objects files"
	@rm -rf out*.m job_output.txt output.txt* *.gmv.* *.plt.* out*.xdr* out*.xda* PI* complete .depend .libs

clean: dust
	@rm -f $(objects) $(target)

clobber: clean 
	@rm -f $(target)

distclean: clean
	@rm -rf *.o .libs .depend

echo:
	@echo srcfiles = $(srcfiles)
	@echo objects = $(objects)
	@echo target = $(target)


%.o: %.cpp $(DEPS)
	@echo "Compiling "$<"..."
	@$(CXX) -c $< -o $@ $(CXXFLAGS)

###############################################################################

