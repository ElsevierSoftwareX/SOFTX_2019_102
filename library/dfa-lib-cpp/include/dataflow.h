/* 
 * File:   dataflow.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:03 AM
 */

#include "dfa_configuration.h"
#include "dfa_configuration_struct.h"
#include "transformation.h"

#include <string>
#include <vector>
#include <map>
#include <iterator>
#include <sstream>

using namespace std;

class Dataflow {
protected:
    DfA_Config config;
    string tag;
    map<string, Transformation> transformations;
    map<string, Set> sets;    

    string get_post_message();

public:

    Dataflow(string tag, string hostname = dfa_hostname, int port = dfa_port) {
        this->config.hostname = hostname;
        this->config.port = port;
        this->tag = tag;
    }

    void save();

    Transformation& add_transformation(string tag, vector<Set> input_sets, vector<Set> output_sets);
    Transformation& add_transformation(string tag, Set input_sets, Set output_sets);
    Transformation& add_transformation(string tag, vector<Set> input_sets, Set output_sets);
    Transformation& add_transformation(string tag, Set input_sets, vector<Set> output_sets);
    Set& add_set(string tag);
    Set& add_set(string tag, string attribute_name, attribute_type attribute_type);
    Set& add_set(string tag, vector<string> attribute_names, vector<attribute_type> attribute_types);
    
    string get_tag();
    string get_specification();
    vector<Transformation> get_sorted_transformations();
    int get_dependent_transformation_position_from_dataset_tag(vector<Transformation> vec_transformations, int current_transformation_position, vector<string> dataset_tags);
};