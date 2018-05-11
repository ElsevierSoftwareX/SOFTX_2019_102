# DfAnalyzer tool

## Overview

This repository presents DfAnalyzer, a dataflow tool for monitoring, debugging, steering, and analyzing dataflow paths of scientific applications.

## Software requirements

The following softwares have to be configured / installed for running this application.

1. [Java](https://www.oracle.com/java/index.html) as the main programming language.
2. [Apache Maven](https://maven.apache.org/), the software project management and comprehension tool.
3. [MonetDB](https://www.monetdb.org) as the open-source column-store database system.

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