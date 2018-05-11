#include "element.h"

void Element::add_value(string value){
    this->values.push_back(value);
}

void Element::set_values(vector<string> values){
    this->values.clear();
    this->values.assign(values.begin(), values.end());
}

vector<string>& Element::get_values(){
    return this->values;
}

string Element::get_values_as_string(){
    return boost::algorithm::join(this->values, ";");
}