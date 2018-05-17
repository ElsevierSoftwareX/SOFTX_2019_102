#!/bin/bash

#set -x

source $LIBMESH/examples/run_common.sh

example_name=systems_of_equations_ex2

export DFA_LIB=../../library/dfa-lib-cpp
export LD_LIBRARY_PATH=$DFA_LIB/lib:$LD_LIBRARY_PATH

echo $LD_LIBRARY_PATH

./delete.sh
run_example "$example_name" 
