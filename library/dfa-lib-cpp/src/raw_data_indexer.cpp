#include "raw_data_indexer.h"

void RawDataIndexer::set_attribute_names(vector<string> attribute_names) {
    this->attribute_names.clear();
    this->attribute_names.assign(attribute_names.begin(), attribute_names.end());
}

void RawDataIndexer::set_attribute_types(vector<attribute_type> attribute_types) {
    this->attribute_types.clear();
    this->attribute_types.assign(attribute_types.begin(), attribute_types.end());
}

string RawDataIndexer::get_attributes_as_string() {
    stringstream attributes;
    if (this->attribute_names.size() == this->attribute_types.size()) {
        attributes << "[";
        for (int index = 0; index < this->attribute_names.size(); index++) {
            if (index != 0) {
                attributes << ",";
            }
            attributes << this->attribute_names.at(index) << ":" << this->values_of_attribute_types[this->attribute_types.at(index)];
        }
        attributes << "]";
    }
    return attributes.str();
}

string RawDataIndexer::get_command_line() {
    stringstream rde_command_line;
    rde_command_line << std::getenv("DFANALYZER_DIR")
            << "/bin/RDI " << values_of_cartridge_types[this->cartridge] << ":" << this->method
            << " " << this->extractor_tag
            << " " << this->path
            << " " << this->file_name_with_extracted_data
            << " " << this->get_attributes_as_string()
            << " " << this->extra_arguments;
    return rde_command_line.str();
}

int RawDataIndexer::run() {
    return std::system(this->get_command_line().c_str());
}

string get_indexing_method_as_string(){
    
}