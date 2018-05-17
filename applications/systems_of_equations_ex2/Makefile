######################################################################
#
# Template libMesh application Makefile
DFANALYZER_DIR 	?= ../../library/dfa-lib-cpp

# include the library options determined by configure
include $(LIBMESH)/Make.common

target     := ./example-$(METHOD)


###############################################################################
# File management.  This is where the source, header, and object files are
# defined

#
# source files
srcfiles 	:= $(wildcard *.C)
libmesh_CXXFLAGS += -w -I$(DFANALYZER_DIR)/include 
libmesh_LDFLAGS  += -lcurl -L$(DFANALYZER_DIR)/lib -ldfanalyzer

# libmesh_CXXFLAGS +=  -I$(DFANALYZER_DIR)/include 
#
# object files
objects		:= $(patsubst %.C, %.$(obj-suffix), $(srcfiles))
###############################################################################



.PHONY: dust clean distclean

###############################################################################
# Target:
#

all: clean $(notdir $(target))

# Production rules:  how to make the target - depends on library configuration
$(notdir $(target)): $(objects)
	@echo "Linking "$@"..."
	$(libmesh_LIBTOOL) --tag=CXX $(LIBTOOLFLAGS) --mode=link \
	  $(libmesh_CXX) $(libmesh_CXXFLAGS) $(objects) -o $@ $(libmesh_LIBS) $(libmesh_LDFLAGS) $(EXTERNAL_FLAGS)


# Useful rules.
dust:
	@echo "Deleting old output and runtime files"
	@rm -rf out*.m job_output.txt output.txt* *.gmv.* *.plt.* out*.xdr* out*.xda* PI* complete .depend .libs

clean: dust
	@rm -f *.$(obj-suffix) *.lo 

clobber: clean 
	@rm -f $(target)

distclean: clean
	@rm -rf *.o .libs .depend

echo:
	@echo srcfiles = $(srcfiles)
	@echo objects = $(objects)
	@echo target = $(target)

run: complete

complete: $(wildcard *.in)
#	@$(MAKE) dust
	@$(MAKE) -C $(dir $(target)) $(notdir $(target))
	@echo "***************************************************************"
	@echo "* Running App " $(notdir $(target))
	@echo "***************************************************************"
	@echo " "
	${LIBMESH_RUN} $(target) ${LIBMESH_OPTIONS} 2>&1 | tee output.txt
	@bzip2 -f output.txt
	@echo " "
	@echo "***************************************************************"
	@echo "* Done Running App " $(notdir $(target))
	@echo "***************************************************************"

gmv:
	@$(MAKE) -C $(LIBMESH)/roy/meshplot/ meshplot-$(METHOD)
	@for file in out.mesh.*; do ${LIBMESH_RUN} $(LIBMESH)/roy/meshplot/meshplot-$(METHOD) $$file out.soln.$${file##out.mesh.} out.gmv.$${file:9:4}; done

# include the dependency list
-include .depend

#
# Dependencies
#
.depend: $(srcfiles) $(LIBMESH)/include/libmesh/*.h
	@$(perl) $(LIBMESH)/contrib/bin/make_dependencies.pl -I. $(foreach i, $(LIBMESH)/include $(wildcard $(LIBMESH)/include/*), -I$(i)) "-S\$$(obj-suffix)" $(srcfiles) > .depend

###############################################################################
