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

class RawDataExtractor {
protected:
    string cartridge = "PROGRAM";
    string method = "EXTRACT";
    string extractor_tag = "extractor";
    string path = ".";
    string command_line;
    vector<string> attribute_names;
    vector<attribute_type> attribute_types;

    vector<string> values_of_attribute_types = {"TEXT", "NUMERIC", "RDFILE"};

public:

    RawDataExtractor(string command_line, vector<string> attribute_names, vector<attribute_type> attribute_types) {
        this->command_line = command_line;
        this->set_attribute_names(attribute_names);
        this->set_attribute_types(attribute_types);
    }

    void set_attribute_names(vector<string> attribute_names);
    void set_attribute_types(vector<attribute_type> attribute_types);

    int run();

    string get_command_line();
    string get_attributes_as_string();
};

#endif /* RAW_DATA_EXTRACTOR_H */

