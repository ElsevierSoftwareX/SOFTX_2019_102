/* 
 * File:   task.h
 * Author: vitor
 *
 * Created on March 31, 2018, 9:43 AM
 */

#include "dataflow.h"
#include "dataset.h"
#include "dependency.h"
#include "task_status_enum.h"

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <sstream>

using namespace std;

class Task {
protected:
    DfA_Config config;
    int id;
    int sub_id;
    string dataflow_tag;
    string transformation_tag;
    string workspace;
    map<string, Dataset> datasets;
    string resource;
    task_status status;
    Dependency dependency;
    
    void add_dependent_transformation_tag(string transformation_tag);
    void add_dependent_transformation_tags(vector<string> transformation_tags);
    void add_dependent_transformation_id(int task_id);
    void add_dependent_transformation_ids(vector<int> transformation_ids);

    void insert_dataset(string dataset_tag);
    string get_post_message();
    void save();

public:

    Task(string dataflow_tag, string transformation_tag, int ID, int sub_id = 0, 
            string hostname = dfa_hostname, int port = dfa_port) {
        this->dataflow_tag = dataflow_tag;
        this->transformation_tag = transformation_tag;
        this->id = ID;
        this->sub_id = sub_id;
        this->dependency = Dependency();
        this->config.hostname = hostname;
        this->config.port = port;
    };

    int begin();
    int end();
    
    Dataset& add_dataset(string dataset_tag);
    Dataset& add_dataset_with_element_value(string dataset_tag, string value);
    Dataset& add_dataset_with_element_values(string dataset_tag, vector<string> values);
    
    void add_dependent_transformation(string transformation_tag, int transformation_id);
    void add_dependent_transformation(string transformation_tag, vector<int> transformation_ids);
    void add_dependent_transformations(vector<string> transformation_tags, int transformation_id);
    void add_dependent_transformations(vector<string> transformation_tags, vector<int> transformation_ids);

    void set_workspace(string workspace);
    void set_resource(string resource);
    void set_status(task_status status);
    void set_sub_id(int sub_id);

    int get_id();
    int get_sub_id();
    string get_dataflow_tag();
    string get_transformation_tag();
    string get_workspace();
    string get_resource();
    Dataset& get_dataset_by_tag(string tag);
    string get_status();
    Dependency& get_dependency();
    string get_specification();
};

