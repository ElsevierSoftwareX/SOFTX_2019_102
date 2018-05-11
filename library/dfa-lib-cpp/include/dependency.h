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

class Dependency {
protected:
    vector<string> transformation_tags;
    vector<vector<int>> transformation_ids;

public:

    Dependency() {
    };

    void add_transformation_tag(string transformation_tag);
    void add_transformation_ids(vector<int> transformation_ids);    
    
    void set_transformation_tags(vector<string> transformation_tags);

    vector<string>& get_transformation_tags();
    vector<vector<int>>&get_transformation_ids();
    string get_transformation_tags_as_string();
    string get_transformation_ids_as_string();
    string get_specification();
};