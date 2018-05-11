#include "dataset.h"

void Dataset::add_element_with_value(string value){
    Element element = Element(value);
    this->elements.push_back(element);
}

void Dataset::add_element_with_values(vector<string> values){
    Element element = Element(values);
    this->elements.push_back(element);
}

void Dataset::clear_elements(){
    this->elements.clear();
}

string Dataset::get_tag() {
    return this->tag;
}

vector<Element>& Dataset::get_elements() {
    return this->elements;
}

string Dataset::get_specification() {
    stringstream dataset_specification;
    
    if (!this->get_elements().empty()) {
        dataset_specification << "collection(" << this->tag << ",{";

        for (Element element : this->get_elements()) {
            dataset_specification << "{" << element.get_values_as_string() << "}";
        }

        dataset_specification << "})";
    }
    
    return dataset_specification.str();
}