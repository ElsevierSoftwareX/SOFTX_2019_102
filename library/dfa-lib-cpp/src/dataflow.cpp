
#include "dataflow.h"
#include <curl/curl.h>

//#define DEBUG

Set& Dataflow::add_set(string tag) {
    Set set = Set(tag);
    this->sets.insert(make_pair(tag, set));
    return this->sets.find(tag)->second;
}

Set& Dataflow::add_set(string tag, string attribute_name, attribute_type attribute_type) {
    Set& set = this->add_set(tag);
    set.add_attribute(attribute_name, attribute_type);
    return set;
}

Set& Dataflow::add_set(string tag, vector<string> attribute_names, vector<attribute_type> attribute_types) {
    Set& set = this->add_set(tag);
    set.add_attributes(attribute_names, attribute_types);
    return set;
}

Transformation& Dataflow::add_transformation(string tag, vector<Set> input_sets, vector<Set> output_sets) {
    Transformation transformation = Transformation(tag);
    transformation.set_input_sets(input_sets);
    transformation.set_output_sets(output_sets);
    this->transformations.insert(make_pair(tag, transformation));
    return this->transformations.find(tag)->second;
}

Transformation& Dataflow::add_transformation(string tag, Set input_set, Set output_set) {
    vector<Set> input_sets = {input_set};
    vector<Set> output_sets = {output_set};
    return this->add_transformation(tag, input_sets, output_sets);
}

Transformation& Dataflow::add_transformation(string tag, vector<Set> input_sets, Set output_set) {
    vector<Set> output_sets = {output_set};
    return this->add_transformation(tag, input_sets, output_sets);
}

Transformation& Dataflow::add_transformation(string tag, Set input_set, vector<Set> output_sets) {
    vector<Set> input_sets = {input_set};
    return this->add_transformation(tag, input_sets, output_sets);
}

string Dataflow::get_tag() {
    return this->tag;
}

string Dataflow::get_specification() {
    return "dataflow(" + this->tag + ")";
}

string Dataflow::get_post_message() {
    stringstream message;
    message << this->get_specification();

    map<string, Set>::iterator it_set = this->sets.begin();
    while (it_set != this->sets.end()) {
        Set set = it_set->second;
        message << "\n" << set.get_specification();

        map<string, Extractor>::iterator it_extractor = set.get_extractors().begin();
        while (it_extractor != set.get_extractors().end()) {
            Extractor extractor = it_extractor->second;
            message << "\n" << extractor.get_specification();
            it_extractor++;
        }
        it_set++;
    }

    vector<Transformation> transformations = this->get_sorted_transformations();
    for (Transformation transformation : transformations) {
        message << "\n" << transformation.get_specification();
    }

    return message.str();
}

void Dataflow::save() {
#ifdef DEBUG
    cout << endl << "[DfAnalyzer] saving dataflow..." << endl;
#endif
    CURL *hnd = curl_easy_init();
    curl_easy_setopt(hnd, CURLOPT_CUSTOMREQUEST, "POST");

    string hostname = "http://" +
            this->config.hostname + ":" + to_string(this->config.port) +
            "/pde/dataflow";
    curl_easy_setopt(hnd, CURLOPT_URL, hostname.c_str());

    struct curl_slist *headers = NULL;
    headers = curl_slist_append(headers, "postman-token: 6afcae02-81cb-821f-379f-f66efb776d94");
    headers = curl_slist_append(headers, "cache-control: no-cache");
    headers = curl_slist_append(headers, "Content-Type: application/text");

    string message = this->get_post_message();

    curl_easy_setopt(hnd, CURLOPT_HTTPHEADER, headers);
#ifdef DEBUG
    cout << message.c_str() << endl << endl;
#endif
    curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, message.c_str());
    curl_easy_setopt(hnd, CURLOPT_VERBOSE, 0L); //0 disable messages

    curl_easy_perform(hnd); //send request
    curl_easy_cleanup(hnd);
    curl_global_cleanup();
}

vector<Transformation> Dataflow::get_sorted_transformations() {
    vector<Transformation> transformations;

    map<string, Transformation>::iterator it_transformation = this->transformations.begin();
    while (it_transformation != this->transformations.end()) {
        Transformation transformation = it_transformation->second;
        transformations.push_back(transformation);
        it_transformation++;
    }

    int index = 0;
    while (index < transformations.size()) {
        Transformation transformation = transformations.at(index);
        int dependent_transformation_position = 
            this->get_dependent_transformation_position_from_dataset_tag(
                transformations, index, transformation.get_input_sets());

        if (dependent_transformation_position != -1) {
            Transformation tmp_transformation = transformations.at(index);
            transformations.at(index) = transformations.at(dependent_transformation_position);
            transformations.at(dependent_transformation_position) = tmp_transformation;
        }else{
            index++;
        }
    }
    
    return transformations;
}

int Dataflow::get_dependent_transformation_position_from_dataset_tag(vector<Transformation> vec_transformations, int current_transformation_position, vector<string> dataset_tags) {
    for (int index = current_transformation_position + 1; index < vec_transformations.size(); index++) {
        Transformation transformation = vec_transformations.at(index);
        if (transformation.has_output_set(dataset_tags)) {
            return index;
        }
    }
    return -1;
}