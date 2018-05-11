#include "transformation.h"
#include "dataset.h"

string Transformation::get_tag() {
    return this->tag;
}

void Transformation::add_input_set(Set set) {
    this->input_sets.push_back(set);
}

void Transformation::add_output_set(Set set) {
    this->output_sets.push_back(set);
}

void Transformation::set_input_sets(vector<Set> input_sets) {
    this->input_sets = input_sets;
}

void Transformation::set_output_sets(vector<Set> output_sets) {
    this->output_sets = output_sets;
}

bool Transformation::has_output_set(vector<string> dataset_tags){
    for(Set dataset : this->output_sets){
        if(find(dataset_tags.begin(), dataset_tags.end(), dataset.get_tag()) != dataset_tags.end()){
            return true;
        }
    }
    return false;
}

string Transformation::get_specification() {
    string input_sets_str = "";
    string output_sets_str = "";

    if (!this->input_sets.empty()) {
        for (Set input_set : this->input_sets) {
            if (!input_sets_str.empty()) {
                input_sets_str += ";";
            }
            input_sets_str += input_set.get_tag();
        }
        input_sets_str = "{" + input_sets_str + "}";
    }

    if (!this->output_sets.empty()) {
        for (Set output_set : this->output_sets) {
            if (!output_sets_str.empty()) {
                output_sets_str += ";";
            }
            output_sets_str += output_set.get_tag();
        }
        output_sets_str = "{" + output_sets_str + "}";
    }

    return "transformation(" + this->tag + "," +
            input_sets_str + "," + output_sets_str + ", )";
}

vector<string> Transformation::get_input_sets(){
    vector<string> set_tags;
    for(Set set : this->input_sets){
        set_tags.push_back(set.get_tag());
    }
    return set_tags;
}

vector<string> Transformation::get_output_sets(){
    vector<string> set_tags;
    for(Set set : this->output_sets){
        set_tags.push_back(set.get_tag());
    }
    return set_tags;
}