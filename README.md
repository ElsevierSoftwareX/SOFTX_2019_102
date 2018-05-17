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