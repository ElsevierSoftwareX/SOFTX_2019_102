/* 
 * File:   raw_data_extractor.h
 * Author: vitor
 *
 * Created on April 13, 2018, 7:16 AM
 */

#ifndef RAW_DATA_EXTRACTOR_H
#define RAW_DATA_EXTRACTOR_H

#include <string>
#include <iostream>
#include <vector>
#include <sstream>
#include <stdlib.h>

#include "attribute_enum.h"

using namespace std;

/*!
 Class to represent the component Raw Data Extractor or RDE (retrospective provenance).
 */
class RawDataExtractor {
protected:
    string cartridge = "PROGRAM"; //!< constant cartridge equals to PROGRAM
    string method = "EXTRACT"; //!< constant method to be performed by RDE, which is named as EXTRACT
    string extractor_tag = "extractor"; //!< default extractor tag
    string path = "."; //!< path of raw data extractor component
    string command_line; //!< command line to be executed by RDE
    vector<string> attribute_names; //!< set of attribute names
    vector<attribute_type> attribute_types; //!< set of attribute types
    vector<string> values_of_attribute_types = {"TEXT", "NUMERIC", "RDFILE"}; //!< set of attribute types as a vector of string

public:

    /*!
     Constructor of a RDE instance
     \param command_line a command line to be executed by RDE
     \param atrtibute_names a set of attribute names that will be extracted
     \param attribute_types a set of attribute types
     */
    RawDataExtractor(string command_line, vector<string> attribute_names, vector<attribute_type> attribute_types) {
        this->command_line = command_line;
        this->set_attribute_names(attribute_names);
        this->set_attribute_types(attribute_types);
    }

    /*!
     Set attribute names.
     \param attribute_names a set of attribute names
      */
    void set_attribute_names(vector<string> attribute_names);
    
    /*!
     Set attribute types.
     \param attribute_types a set of attribute types
      */
    void set_attribute_types(vector<attribute_type> attribute_types);

    /*!
     Run the component Raw Data Extractor.
     \return the exit status code of running RDE
     */
    int run();

    /*!
     Get command line.
     \return the command line.
     */
    string get_command_line();
    
    /*!
     Get attributes to be extracted.
     \return the attributes to be extracted as a string.
     */
    string get_attributes_as_string();
};

#endif /* RAW_DATA_EXTRACTOR_H */

