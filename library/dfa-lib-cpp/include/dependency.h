/* 
 * File:   dependency.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:15 AM
 */

#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <iostream>
#include <boost/algorithm/string/join.hpp>

using namespace std;

/*!
 Class to represent a data dependency (retrospective provenance).
*/
class Dependency {
protected:
    vector<string> transformation_tags; //!< set of dependent data transformations
    vector<vector<int>> transformation_ids; //!< set of tasks identifiers to the dependent data transformations

public:

    /*!
     Constructor of a data dependency.
     */
    Dependency() {
    };

    /*!
     Add a data transformation tag.
     \param transformation_tag a data transformation tag
     */
    void add_transformation_tag(string transformation_tag);
    
    /*!
     Add task identifiers to the set of task identifiers.
     \param transformation_ids a set of data transformation identifiers
     */
    void add_transformation_ids(vector<int> transformation_ids);    
    
    /*!
     Set a set of data transformation tags.
     \param transformation_tags a set of data transformation_tags
     */
    void set_transformation_tags(vector<string> transformation_tags);

    /*!
     Get the data transformation tags.
     \return the reference of the set of data transformation tags
     */
    vector<string>& get_transformation_tags();
    
    /*!
     Get the tasks identifiers to the dependent data transformations.
     \return the reference of the set of task identifiers
     */
    vector<vector<int>>&get_transformation_ids();
    
    /*!
     Get the data transformation tags.
     \return the data transformation tags as a string
     */
    string get_transformation_tags_as_string();
    
    /*!
     Get the task identifiers to the dependent data transformation.
     \return the set of task identifiers as a string
     */
    string get_transformation_ids_as_string();
    
    /*!
     Get data dependency specification.
     \return the data dependency specification
     */
    string get_specification();
};