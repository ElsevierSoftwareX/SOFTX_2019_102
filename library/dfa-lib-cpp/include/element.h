/* 
 * File:   element.h
 * Author: vitor
 *
 * Created on March 31, 2018, 10:32 AM
 */

#include <string>
#include <vector>
#include <sstream>
#include <boost/algorithm/string/join.hpp>

using namespace std;

class Element{
protected:
    vector<string> values;
    
public:
    Element(){};
    
    Element(string value){
        this->add_value(value);
    }
    
    Element(vector<string> values){
        this->set_values(values);
    }
    
    void add_value(string value);
    void set_values(vector<string> values);
    
    vector<string>& get_values();
    string get_values_as_string();
    
};