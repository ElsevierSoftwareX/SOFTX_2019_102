# DfAnalyzer tool

## Overview

This repository presents DfAnalyzer, a dataflow tool for monitoring, debugging, steering, and analyzing dataflow paths of scientific applications.

## Software requirements

The following softwares have to be configured / installed for running this application.

### RESTful services

1. [Java](https://www.oracle.com/java/index.html) as the main programming language. (Please read the [tutorials](http://www.oracle.com/technetwork/java/javase/downloads/index.html) provided by Oracle Technology)
2. [Apache Maven](https://maven.apache.org/), the software project management and comprehension tool. 
3. [MonetDB](https://www.monetdb.org) as the open-source column-store database system. (Please follow their [user guide](https://www.monetdb.org/Documentation/UserGuide))

### C++ library

1. [GCC](https://gcc.gnu.org/), the GNU Compiler Collection. (Please consider their [installation guide](https://gcc.gnu.org/install/))
2. [curl](https://curl.haxx.se/), a command line tool and library for transferring data with URLs. (Please read their [installation instructions](https://curl.haxx.se/download.html))
3. [boost](https://www.boost.org/), a package of free peer-reviewed portable C++ source libraries.

## Installation

### RESTful services

DfAnalyzer project can be easily built by running the following command lines:

```bash
cd DfAnalyzer
mvn clean package
```

Then, the package file JAR is generated for DfAnalyzer project. It allows Java runtimes to efficiently deploy our tool. This JAR file is named as *DfAnalyzer-v2.jar* and it can be found in *./DfAnalyzer/target*.

### C++ library

The DfAnalyzer library for the programming language C++ can be built with the following command lines:

```bash
cd library/dfa-lib-cpp
make
```

Then, a static compiled library file, named as *libdfanalyzer.so*, is generated at the directory *./library/dfa-lib-cpp/lib*.

## Use in scientific applications

### C++ library

When computational specialist needs to configure the *Makefile* of his scientific application, he has to add the following set of directives used by a make build automation tool to generate a target/goal.

```bash
DFANALYZER_DIR 	?= $(DFANALYZER_REPOSITORY)/library/dfa-lib-cpp
LDFLAGS  += -lcurl -L$(DFANALYZER_DIR)/lib -ldfanalyzer
```

Then, he/she can run the command make for generating an executable file of his/her application.

# Docker image

Besides the source code of DfAnalyzer tool and its libraries, we also provide a Docker image, named as **dataflow_analyzer**, with DfAnalyzer tool; its libraries in C++ and Python, known as *dfa-lib*; and two Computational Science and Engineering (CSE) applications, named as *systems_of_equations_ex2* and *prototype_multi-physics*. 

The CSE application *systems_of_equations_ex2* was downloaded from [an example in libMesh website](https://libmesh.github.io/examples/systems_of_equations_ex2.html) and developed using *dfa-lib* in C++, while the application *prototype_multi-physics* used *dfa-lib* in Python. 

DfAnalyzer, its libraries, and CSE applications are stored in directories `DfAnalyzer`, `library`, and `applications` at the path `/dfanalyzer`. 

<a href="https://hub.docker.com/r/vitorss/dataflow_analyzer" target="_blank">
    <img src="./img/docker.png" width="120">
</a>

# CSE applications

Here we present the software requirements and how to run CSE applications *systems_of_equations_ex2* and *prototype_multi-physics*.

## Software requirements

### Systems of Equations - Example 2

1. [HDF5](https://support.hdfgroup.org/HDF5/), a data model, library, and file format for storing and managing data.
2. [PETSc](https://www.mcs.anl.gov/petsc/), the Portable, Extensible Toolkit for Scientific Computation.
3. [libMesh](http://libmesh.github.io/) as a framework for the numerical simulation of partial differential equations using arbitrary unstructured discretizations on serial and parallel platforms.

<a href="https://support.hdfgroup.org/HDF5/" target="_blank">
    <img src="./img/hdf5.jpeg" width="110" align="middle">
</a>
<a href="https://www.mcs.anl.gov/petsc/" target="_blank">
    <img src="./img/petsc.png" width="150" align="middle">
</a>
<a href="http://libmesh.github.io/" target="_blank">
    <img src="./img/libmesh.jpeg" width="150" align="middle">
</a>

There are also some optional software requirements, if raw data extraction and indexing capabilities are enabled, as follows.

4. [ParaView](https://www.paraview.org/), an open-source, multi-platform data analysis and visualization application.
5. [FastBit](https://sdm.lbl.gov/fastbit/), an efficient compressed bitmap index technology.

## How to run applications

### Systems of Equations - Example 2

#### Source code compilation

Application files are stored in directory `applications/systems_of_equations_ex2` and it has to be compiled. However, it is necessary to define the environment variable `LIBMESH` with the installation path of libMesh before to run the following command lines:

```bash
cd applications/systems_of_equations_ex2
make
```

#### Environment configuration for raw data extraction and visualization

If visualization and raw data extraction/indexing are enabled in the CSE application (according to the *#define* statements in `systems_of_equations_ex2.C`), it is necessary to configure some environment variables (`PARAVIEW`, `FASTBIT`), since this application will use ParaView tool for extracting raw data from files in [ExodusII](http://prod.sandia.gov/techlib/access-control.cgi/1992/922137.pdf) format and FastBit for applying bitmap indexing technique. Please find below the definition of these variables in our Docker image, besides the environment variable `LIBMESH`.

```bash
export LIBMESH=/program/libmesh
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