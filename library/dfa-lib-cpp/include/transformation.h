/* 
 * File:   transformation.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:07 AM
 */

#include "set.h"

#include <string>
#include <vector>

using namespace std;

/*!
 Class to represent a data transformation specification (prospective provenance).
 */
class Transformation{
protected:
    string tag; //!< data transformation tag
    vector<Set> input_sets; //!< input datasets
    vector<Set> output_sets; //!< output datasets
    
public:
    
    /*!
     Constructor of a data transformation specification.
     \param tag a data transformation specification.
     */
    Transformation(string tag){
        this->tag = tag;
    }
    
    /*!
     Add an input dataset to data transformation specification.
     \param set an input dataset specification.
     */
    void add_input_set(Set set);
    
    /*!
     Add an output dataset to data transformation specification.
     \param set an output dataset specification.
     */
    void add_output_set(Set set);
    
    /*!
     Get the data transformation tag.
     \return the transformation tag
     */
    string get_tag();    
    
    /*!
     Get the data transformation specification.
     \return the transformation specification
     */
    string get_specification();
    
    /*!
     Get the input datasets (specifications).
     \return the input datasets
     */
    vector<string> get_input_sets();
    
    /*!
     Get the output datasets (specification).
     \return the output datasets
     */
    vector<string> get_output_sets();
    
    /*!
     Set a set of input datasets.
     \input_sets a set of input datasets
     */
    void set_input_sets(vector<Set> input_sets);
    
    /*!
     Set a set of output datasets.
     \output_sets a set of output datasets
     */
    void set_output_sets(vector<Set> output_sets);
    
    /*!
     Verify if we have output datasets with the specified dataset tags.
     \param dataset_tags a list of dataset tags
     \return a boolean to inform if we have or not the specified dataset tags.
     */
    bool has_output_set(vector<string> dataset_tags);
};