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

/*!
 Class to represent a task (retrospective provenance).
 */
class Task {
protected:
    DfA_Config config; //!< DfAnalyzer configurations
    int id; //!< task identifier
    int sub_id; //!< task subidentifier
    string dataflow_tag; //!< dataflow tag
    string transformation_tag; //!< transformation tag
    string workspace; //!< workspace
    map<string, Dataset> datasets; //!< map of dataset tag with its object derived from Dataset
    string resource; //!< resource
    task_status status; //!< task status, which can be READY, RUNNING, or FINISHED
    Dependency dependency; //!< data dependency

    /*!
     Add a dependent transformation tag.
     \param transformation_tag a transformation tag
     */
    void add_dependent_transformation_tag(string transformation_tag);

    /*!
     Add a set of dependent transformation tags.
     \param transformation_tags a set of dependent transformation tags
     */
    void add_dependent_transformation_tags(vector<string> transformation_tags);

    /*!
     Add a dependent task identifier to the dependent transformation tag.
     \param task_id a task identifier
     */
    void add_dependent_transformation_id(int task_id);

    /*!
     Add a set of dependent task identifiers to the dependent transformation tags.
     \param task_ids a set of task identifiers
     */
    void add_dependent_transformation_ids(vector<int> task_ids);

    /*!
     Insert dataset consumed/produced by the task.
     \param dataset_tag a dataset tag
     */
    void insert_dataset(string dataset_tag);

    /*!
     Get message to be sent to DfAnalyzer server using an HTTP request with POST method.
     \return the message in HTTP request
     */
    string get_post_message();

    /*!
     Save a task in DfAnalyzer database.
     */
    void save();

public:

    /*!
     Constructor of a task.
     \param dataflow_tag a dataflow tag
     \param transformation_tag a transformation tag
     \param ID a task identifier
     \param sub_id a task subidentifier (optional)
     \param hostname a hostname to the DfAnalyzer server (optional)
     \param port a port to the DfAnalyzer server (optional)
     */
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

    /*!
     Save a task with the status RUNNING.
     \return the exit status code of the HTTP request sent using curl.
     */
    int begin();

    /*!
     Save a task with the status FINISHED.
     \return the exit status code of the HTTP request sent using curl.
     */
    int end();

    /*!
     Add a dataset to the task.
     \param dataset_tag a dataset tag
     */
    Dataset& add_dataset(string dataset_tag);

    /*!
     Add a dataset to the task with a single attribute value.
     \param dataset_tag a dataset tag
     \param value a single attribute value
     */
    Dataset& add_dataset_with_element_value(string dataset_tag, string value);

    /*!
     Add a dataset to the task with multiple attribute values.
     \param dataset_tag a dataset tag
     \param values a set of attribute values
     */
    Dataset& add_dataset_with_element_values(string dataset_tag, vector<string> values);

    /*!
     Add a dependent transformation to the task with a single dependent task.
     \param transformation_tag a transformation tag
     \param transformation_id a transformation identifier
     */
    void add_dependent_transformation(string transformation_tag, int transformation_id);

    /*!
     Add a dependent transformation to the task with multiple dependent tasks.
     \param transformation_tag a transformation tag
     \param transformation_ids a set of transformation identifiers
     */
    void add_dependent_transformation(string transformation_tag, vector<int> transformation_ids);

    /*!
     Add a set of dependent transformations to the task with a single dependent task.
     \param transformation_tags a set of transformation tags
     \param transformation_id a transformation identifier
     */
    void add_dependent_transformations(vector<string> transformation_tags, int transformation_id);

    /*!
     Add a set of dependent transformations to the task with multiple dependent tasks.
     \param transformation_tags a set of transformation tags
     \param transformation_ids a set of transformation identifiers
     */
    void add_dependent_transformations(vector<string> transformation_tags, vector<int> transformation_ids);

    /*!
     Set workspace.
     \param workspace a workspace
     */
    void set_workspace(string workspace);

    /*!
     Set resource.
     \param resource a resource
     */
    void set_resource(string resource);

    /*!
     Set task status.
     \param status a task status
     */
    void set_status(task_status status);

    /*!
     Set task subidentifier.
     \param sub_id a task subidentifier
     */
    void set_sub_id(int sub_id);

    /*!
     Get task identifier.
     \return the task identifier
     */
    int get_id();
    
    /*!
     Get task subidentifier.
     \return the task subidentifier
     */
    int get_sub_id();
    
    /*!
     Get dataflow tag.
     \return the dataflow tag
     */
    string get_dataflow_tag();
    
    /*!
     Get transformation tag.
     \return the transformation tag
     */
    string get_transformation_tag();
    
    /*!
     Get the workspace.
     \return the workspace
     */
    string get_workspace();
    
    /*!
     Get the resource
     \return the resource
     */
    string get_resource();
    
    /*!
     Get dataset given a tag.
     \param tag a dataset tag.
     \return the reference of the dataset given a tag
     */
    Dataset& get_dataset_by_tag(string tag);
    
    /*!
     Get the task status.
     \return the task status
     */
    string get_status();
    
    /*!
     Get the data dependency.
     \return the data dependency
     */
    Dependency& get_dependency();
    
    /*!
     Get task specification.
     \return the task specification as a string*/
    string get_specification();
};

