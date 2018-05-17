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

/*!
 Class to represent a dataflow specification (prospective provenance).
 */
class Dataflow {
protected:
    DfA_Config config; //!< dataflow configuration
    string tag; //!< dataflow tag
    map<string, Transformation> transformations; //!< set of data transformations
    map<string, Set> sets; //!< set of datasets   

    /*!
     Get message to be sent to DfAnalyzer server using an HTTP request with POST method.
     \return the message in HTTP request
     */
    string get_post_message();

public:

    /*!
     Constructor of a dataflow specification
     \param tag a dataflow tag
     \param hostname an optional argument that corresponds to the hostname of DfAnalyzer RESTful server
     \param port an optional argument that corresponds to the port of DfAnalyzer RESTful server.
     */
    Dataflow(string tag, string hostname = dfa_hostname, int port = dfa_port) {
        this->config.hostname = hostname;
        this->config.port = port;
        this->tag = tag;
    }

    /*!
     Save dataflow specification.
     */
    void save();

    /*!
     Add a data transformation to the dataflow specification.
     \param tag a dataflow tag
     \param input_sets a set of input datasets
     \param output_sets a set of output datasets
     */
    Transformation& add_transformation(string tag, vector<Set> input_sets, vector<Set> output_sets);
    
    /*!
     Add a data transformation to the dataflow specification.
     \param tag a dataflow tag
     \param input_set an input dataset
     \param output_set an output dataset
     */
    Transformation& add_transformation(string tag, Set input_set, Set output_set);
    
    /*!
     Add a data transformation to the dataflow specification.
     \param tag a dataflow tag
     \param input_sets a set of input datasets
     \param output_set an output dataset
     */
    Transformation& add_transformation(string tag, vector<Set> input_sets, Set output_set);
    
    /*!
     Add a data transformation to the dataflow specification.
     \param tag a dataflow tag
     \param input_set an input dataset
     \param output_sets a set of output datasets
     */
    Transformation& add_transformation(string tag, Set input_set, vector<Set> output_sets);
    
    /*!
     Add a dataset to the dataflow specification.
     \param tag a dataset tag
     */
    Set& add_set(string tag);
    
    /*!
     Add a dataset to the dataflow specification.
     \param tag a dataset tag
     \param attribute_name an attribute name
     \param attribute_type an attribute types
     */
    Set& add_set(string tag, string attribute_name, attribute_type attribute_type);
    
    /*!
     Add a dataset to the dataflow specification.
     \param tag a dataset tag
     \param attribute_names a set of attribute names
     \param attribute_types a set of attribute types
     */
    Set& add_set(string tag, vector<string> attribute_names, vector<attribute_type> attribute_types);
    
    /*!
     Get dataflow tag
     \return the dataflow tag
     */
    string get_tag();
    
    /*!
     Get dataflow specification.
     \return the dataflow specification as a string
     */
    string get_specification();
    
    /*!
     Get sorted data transformation.
     \return the set of sorted data transformations
     */
    vector<Transformation> get_sorted_transformations();
    
    /*!
     Get dependent data transformation position based on a dataset tag.
     \vec_transformations a set of data transformations
     \current_transformation_position a data transformation position in vec_transformations
     \dataset_tags a set of dataset tags to get their dependencies
     \return the position of the dependent data transformation
     */
    int get_dependent_transformation_position_from_dataset_tag(vector<Transformation> vec_transformations, 
        int current_transformation_position, vector<string> dataset_tags);
};