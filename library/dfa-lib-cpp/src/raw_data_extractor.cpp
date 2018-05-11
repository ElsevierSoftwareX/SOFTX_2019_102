#include "raw_data_extractor.h"

void RawDataExtractor::set_attribute_names(vector<string> attribute_names) {
    this->attribute_names.clear();
    this->attribute_names.assign(attribute_names.begin(), attribute_names.end());
}

void RawDataExtractor::set_attribute_types(vector<attribute_type> attribute_types) {
    this->attribute_types.clear();
    this->attribute_types.assign(attribute_types.begin(), attribute_types.end());
}

string RawDataExtractor::get_attributes_as_string(){
    stringstream attributes;
    if(this->attribute_names.size() == this->attribute_types.size()){
        attributes << "[";
        for(int index=0; index < this->attribute_names.size(); index++){
            if(index != 0){
                attributes << ",";
            }
            attributes << this->attribute_names.at(index) << ":" << this->values_of_attribute_types[this->attribute_types.at(index)];
        }
        attributes << "]";
    }
    return attributes.str();
}

string RawDataExtractor::get_command_line(){
    stringstream rde_command_line;
    rde_command_line << std::getenv("DFANALYZER_DIR")
            << "/bin/RDE " << this->cartridge << ":" << this->method 
            << " " << this->extractor_tag 
            << " " << this->path
            << " \"" << this->command_line << "\""
            << " " << this->get_attributes_as_string();
    return rde_command_line.str();
}

int RawDataExtractor::run() {
    return std::system(this->get_command_line().c_str());
}