#include "dependency.h"

void Dependency::add_transformation_tag(string transformation_tag) {
    this->transformation_tags.push_back(transformation_tag);
}

void Dependency::add_transformation_ids(vector<int> transformation_ids) {
    this->transformation_ids.push_back(transformation_ids);
}

vector<string>& Dependency::get_transformation_tags() {
    return this->transformation_tags;
}

vector<vector<int>>&Dependency::get_transformation_ids() {
    return this->transformation_ids;
}

void Dependency::set_transformation_tags(vector<string> transformation_tags) {
    this->transformation_tags.clear();
    this->transformation_tags.assign(transformation_tags.begin(), transformation_tags.end());
}

string Dependency::get_specification() {
    stringstream dependency_specification;

    if (!this->get_transformation_tags().empty() && !this->get_transformation_ids().empty()) {
        dependency_specification << "dependency({" <<
                this->get_transformation_tags_as_string() << "},{" <<
                this->get_transformation_ids_as_string() << "})";
    }

    return dependency_specification.str();
}

string Dependency::get_transformation_tags_as_string() {
    return boost::algorithm::join(this->transformation_tags, ";");
}

string Dependency::get_transformation_ids_as_string() {
    stringstream result;
    if(!this->get_transformation_ids().empty()){
        bool first_id = true;
        for(vector<int> transformation_id : this->get_transformation_ids()){
            if(first_id){
                first_id = false;
            }else{
                result << ";";
            }
            
            stringstream transformation_id_as_stream;
            copy(transformation_id.begin(), transformation_id.end(), ostream_iterator<int>(transformation_id_as_stream, ";"));
            
            string transformation_id_as_string = transformation_id_as_stream.str();
            transformation_id_as_string = transformation_id_as_string.substr(0, transformation_id_as_string.length()-1);
            result << "{" << transformation_id_as_string << "}";
        }
    }
    return result.str();
}