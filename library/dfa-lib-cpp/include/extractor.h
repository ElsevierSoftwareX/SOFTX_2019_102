/* 
 * File:   extractor.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:45 AM
 */

#ifndef EXTRACTOR_H
#define EXTRACTOR_H

#include "extractor_enum.h"

#include <string>
#include <vector>
#include <iostream>

using namespace std;

class Extractor {
protected:
    string tag;
    string set_tag;
    method_type method;
    cartridge_type cartridge;
    vector<Attribute> attributes;

public:

    Extractor(string tag, string set_tag, method_type method, cartridge_type cartridge) {
        this->tag = tag;
        this->set_tag = set_tag;
        this->method = method;
        this->cartridge = cartridge;
    }

    void add_attribute(string name, attribute_type type);
    void add_attributes(vector<string> names, vector<attribute_type> types);

    string get_tag();
    string get_set_tag();
    Attribute& get_attribute_by_name(string name);
    string get_method();
    string get_cartridge();
    string get_specification();
};

#endif /* EXTRACTOR_H */