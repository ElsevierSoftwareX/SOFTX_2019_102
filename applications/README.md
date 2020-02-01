## Use in scientific applications

### C++ library

When computational specialist needs to configure the *Makefile* of his scientific application, he has to add the following set of directives used by a make build automation tool to generate a target/goal.

```bash
DFANALYZER_DIR 	?= $(DFANALYZER_REPOSITORY)/library/dfa-lib-cpp
LDFLAGS  += -lcurl -L$(DFANALYZER_DIR)/lib -ldfanalyzer
```

Then, he can run the command make for generating an executable file of his application.

# Docker image

Besides the source code of DfAnalyzer tool and its libraries, we also provide a Docker image, named as **dataflow_analyzer**, with DfAnalyzer tool; its libraries in C++ and Python, known as *dfa-lib*; and two Computational Science and Engineering (CSE) applications, named as *systems_of_equations_ex2* and *prototype_multi-physics*. 

The CSE application *systems_of_equations_ex2* was downloaded from [an example in libMesh website](https://libmesh.github.io/examples/systems_of_equations_ex2.html) and developed using *dfa-lib* in C++, while the application *prototype_multi-physics* used *dfa-lib* in Python. 

DfAnalyzer, its libraries, and CSE applications are stored in directories `DfAnalyzer`, `library`, and `applications` at the path `/dfanalyzer`. 

<a href="https://hub.docker.com/r/vitorss/dataflow_analyzer" target="_blank">
    <img src="../img/docker.png" width="120">
</a>

# Computational Science and Engineering (CSE) applications

Here we present the software requirements and how to run CSE applications *systems_of_equations_ex2* and *prototype_multi-physics*.

## Software requirements

### Systems of Equations - Example 2

1. [HDF5](https://support.hdfgroup.org/HDF5/), a data model, library, and file format for storing and managing data.
2. [PETSc](https://www.mcs.anl.gov/petsc/), the Portable, Extensible Toolkit for Scientific Computation.
3. [libMesh](http://libmesh.github.io/) as a framework for the numerical simulation of partial differential equations using arbitrary unstructured discretizations on serial and parallel platforms.

<a href="https://support.hdfgroup.org/HDF5/" target="_blank">
    <img src="../img/hdf5.jpeg" width="110" align="middle">
</a>
<a href="https://www.mcs.anl.gov/petsc/" target="_blank">
    <img src="../img/petsc.png" width="150" align="middle">
</a>
<a href="http://libmesh.github.io/" target="_blank">
    <img src="../img/libmesh.jpeg" width="150" align="middle">
</a>

There are also some optional software requirements, if raw data extraction and indexing capabilities are enabled, as follows.

1. [ParaView](https://www.paraview.org/), an open-source, multi-platform data analysis and visualization application.
2. [FastBit](https://sdm.lbl.gov/fastbit/), an efficient compressed bitmap index technology.

### Prototype of Multiphysics Application

1. [FEniCS](https://fenicsproject.org/) as a framework for the numerical simulation of partial differential equations using arbitrary unstructured discretizations on serial and parallel platforms.
2. [DfA-lib-Python](https://dfa-lib-python-docs.herokuapp.com/) A DfAnalyzer library implemented in Python for extracting provenance data, extracting raw data from data sources, and generating indexes of extracted data at runtime based on the usage of DfAnalyzer RESTful services. 
3. [Conda](https://conda.io/docs/index.html), a package dependency and environment management for Python.

<a href="https://fenicsproject.org/" target="_blank">
    <img src="../img/fenics.png" width="110" align="middle">
</a>
<a href="https://conda.io/docs/index.html" target="_blank">
    <img src="../img/conda.png" width="60" align="middle">
</a>

## How to run applications

### Systems of Equations - Example 2

#### Source code compilation

Application files are stored in directory `applications/systems_of_equations_ex2` and it has to be compiled. However, it is necessary to define the environment variable `LIBMESH_DIR` with the installation path of libMesh before to run the following command lines:

```bash
cd applications/systems_of_equations_ex2
make
```

#### Environment configuration for raw data extraction and visualization

If visualization and raw data extraction/indexing are enabled in the CSE application (according to the *#define* statements in `systems_of_equations_ex2.C`), it is necessary to configure some environment variables (`PARAVIEW`, `FASTBIT`), since this application will use ParaView tool for extracting raw data from files in [ExodusII](http://prod.sandia.gov/techlib/access-control.cgi/1992/922137.pdf) format and FastBit for applying bitmap indexing technique. Please find below the definition of these variables in our Docker image, besides the environment variable `LIBMESH_DIR`.

```bash
export LIBMESH_DIR=/program/libmesh
export PARAVIEW=/program/paraview
export FASTBIT=/program/fastbit
```

**Important note**: In our Docker image, it is not necessary to compile this application or configure the environment variables.

#### Run application

Then, the second example of Systems of Equations from libMesh website can be executed using DfAnalyzer. In this case, two terminal connections have to be initialized. In the first terminal connection, we will initialize DfAnalyzer RESTful services by running the script `start-dfanalyzer.sh` at `applications/dfanalyzer`. After that, we start the CSE application by running the script `run.sh` at `applications/systems_of_equations_ex2`.

Command lines to the first terminal connection (*DfAnalyzer*):

```bash
cd applications/dfanalyzer
./start-dfanalyzer.sh
```

Command lines to the second terminal connection (*CSE application*):

```bash
cd applications/systems_of_equations_ex2
./run.sh
```

### Prototype of Multiphysics Application

#### Source code

Application files are stored in directory `applications/prototype_multiphysics_application` divided in three folders each one specific for one execution type, are they: `baseline`and `dfa`. It's necessary to install conda and them execute the following commands.

```bash
cd applications/prototype_multiphysics_application
conda create -n prototype_multiphysics_application -c conda-forge fenics
source activate prototype_multiphysics_application
make init
```

**Important note**: In our Docker image, it is not necessary to install the dependencies above.

#### Run application

Then, the prototype multiphysics application application can be executed using DfAnalyzer as extraction provenance tool.

##### DfAnalyzer

In this case, two terminal connections have to be initialized. In the first terminal connection, we will initialize DfAnalyzer RESTful services by running the script `start-dfanalyzer.sh` at `applications/dfanalyzer`. After that, we start the CSE application by executing the command line `make run-dfa` at `applications/prototype_multiphysics_application`.

Command lines to the first terminal connection (*DfAnalyzer*):

```bash
cd applications/dfanalyzer
./start-dfanalyzer.sh
```

Command lines to the second terminal connection (*CSE application*):

```bash
cd applications/prototype_multiphysics_application
source activate prototype_multiphysics_application
make run-dfa
```
