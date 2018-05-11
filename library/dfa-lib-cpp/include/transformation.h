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

class Transformation{
protected:
    string tag;
    vector<Set> input_sets;
    vector<Set> output_sets;
    
public:
    Transformation(string tag){
        this->tag = tag;
    }
    
    void add_input_set(Set set);
    void add_output_set(Set set);
    
    string get_tag();    
    string get_specification();
    vector<string> get_input_sets();
    vector<string> get_output_sets();
    
    void set_input_sets(vector<Set> input_sets);
    void set_output_sets(vector<Set> output_sets);
    
    bool has_output_set(vector<string> dataset_tags);
};