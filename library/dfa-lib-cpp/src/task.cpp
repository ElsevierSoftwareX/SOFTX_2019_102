#include "task.h"
#include <curl/curl.h>

//#define DEBUG

void Task::set_workspace(string workspace) {
    this->workspace = workspace;
}

void Task::set_resource(string resource) {
    this->resource = resource;
}

void Task::set_status(task_status status) {
    this->status = status;
}

void Task::set_sub_id(int sub_id){
    if(sub_id >= 0){
        this->sub_id = sub_id;
    }
}

int Task::get_id() {
    return this->id;
}

int Task::get_sub_id() {
    return this->sub_id;
}

string Task::get_dataflow_tag() {
    return this->dataflow_tag;
}

string Task::get_transformation_tag() {
    return this->transformation_tag;
}

string Task::get_workspace() {
    return this->workspace;
}

string Task::get_resource() {
    return this->resource;
}

Dataset& Task::get_dataset_by_tag(string tag) {
    if (this->datasets.find(tag) == this->datasets.end()) {
        this->insert_dataset(tag);
    }
    return this->datasets.find(tag)->second;
}

Dataset& Task::add_dataset(string dataset_tag) {
    return this->get_dataset_by_tag(dataset_tag);
}

Dataset& Task::add_dataset_with_element_value(string dataset_tag, string value){
    Dataset& dataset = this->add_dataset(dataset_tag);
    dataset.add_element_with_value(value);
    return dataset;
}

Dataset& Task::add_dataset_with_element_values(string dataset_tag, vector<string> values){
    Dataset& dataset = this->add_dataset(dataset_tag);
    dataset.add_element_with_values(values);
    return dataset;
}

void Task::insert_dataset(string dataset_tag) {
    Dataset dataset = Dataset(dataset_tag);
    this->datasets.insert(make_pair(dataset_tag, dataset));
}

string Task::get_status() {
    string status_str("unknown");
    switch (this->status) {
        case READY:
        {
            status_str = "READY";
        }
            break;
        case RUNNING:
        {
            status_str = "RUNNING";
        }
            break;
        case FINISHED:
        {
            status_str = "FINISHED";
        }
            break;
    }
    return status_str;
}

Dependency& Task::get_dependency() {
    return this->dependency;
}

string Task::get_specification() {
    stringstream message;
    message << "task(" << this->dataflow_tag << "," << this->transformation_tag
            << "," << this->id << ",";

    if (this->sub_id > 0) {
        message << this->sub_id << ",";
    } else {
        message << " ,";
    }

    message << this->get_status();

    if (!this->workspace.empty()) {
        message << "," << this->workspace;
    }

    if (!this->resource.empty()) {
        message << "," << this->resource;
    }

    message << ")";
    return message.str();
}

string Task::get_post_message() {
    stringstream message;
    message << this->get_specification();

    map<string, Dataset>::iterator it_dataset = this->datasets.begin();
    while (it_dataset != this->datasets.end()) {
        Dataset dataset = it_dataset->second;
        message << "\n" << dataset.get_specification();
        it_dataset++;
    }
    
    if(!this->get_dependency().get_transformation_ids().empty() && !this->get_dependency().get_transformation_tags().empty()){
        message << "\n" << this->dependency.get_specification();
    }

    return message.str();
}

void Task::save() {
#ifdef DEBUG
    cout << endl << "[DfAnalyzer] saving task..." << endl;
#endif
    CURL *hnd = curl_easy_init();
    curl_easy_setopt(hnd, CURLOPT_CUSTOMREQUEST, "POST");

    string hostname = "http://" +
            this->config.hostname + ":" + to_string(this->config.port) +
            "/pde/task";
    curl_easy_setopt(hnd, CURLOPT_URL, hostname.c_str());

    struct curl_slist *headers = NULL;
    headers = curl_slist_append(headers, "postman-token: 6afcae02-81cb-821f-379f-f66efb776d94");
    headers = curl_slist_append(headers, "cache-control: no-cache");
    headers = curl_slist_append(headers, "Content-Type: application/text");

    string message = this->get_post_message();
#ifdef DEBUG
    cout << message << endl << endl;
#endif

    curl_easy_setopt(hnd, CURLOPT_HTTPHEADER, headers);
    curl_easy_setopt(hnd, CURLOPT_POSTFIELDS, message.c_str());
    curl_easy_setopt(hnd, CURLOPT_VERBOSE, 0L); //0 disable messages
    curl_easy_setopt(hnd, CURLOPT_WRITEFUNCTION, 0L);

    curl_easy_perform(hnd); //send request
    curl_easy_cleanup(hnd);
    curl_global_cleanup();
}

int Task::begin() {
    this->set_status(RUNNING);
    this->save();
    this->datasets.clear();
}

int Task::end() {
    this->set_status(FINISHED);
    this->save();
}

void Task::add_dependent_transformation_tag(string transformation_tag){
    this->dependency.add_transformation_tag(transformation_tag);
}

void Task::add_dependent_transformation_tags(vector<string> transformation_tags){
    this->dependency.set_transformation_tags(transformation_tags);
}

void Task::add_dependent_transformation_id(int task_id){
    this->dependency.add_transformation_ids({task_id});
}

void Task::add_dependent_transformation_ids(vector<int> task_ids){
    this->dependency.add_transformation_ids(task_ids);
}

void Task::add_dependent_transformation(string transformation_tag, int transformation_id){
    this->add_dependent_transformation_tag(transformation_tag);
    this->add_dependent_transformation_id(transformation_id);
}

void Task::add_dependent_transformation(string transformation_tag, vector<int> transformation_ids){
    this->add_dependent_transformation_tag(transformation_tag);
    this->add_dependent_transformation_ids(transformation_ids);
}

void Task::add_dependent_transformations(vector<string> transformation_tags, int transformation_id){
    this->add_dependent_transformation_tags(transformation_tags);
    this->add_dependent_transformation_id(transformation_id);
}

void Task::add_dependent_transformations(vector<string> transformation_tags, vector<int> transformation_ids){
    this->add_dependent_transformation_tags(transformation_tags);
    this->add_dependent_transformation_ids(transformation_ids);
}