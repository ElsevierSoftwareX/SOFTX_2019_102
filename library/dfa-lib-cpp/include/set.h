/* 
 * File:   set.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:27 AM
 */

#include "attribute.h"
#include "extractor.h"

#include <string>
#include <vector>
#include <map>

using namespace std;

class Set {
protected:
    string tag;
    vector<Attribute> attributes;
    map<string, Extractor> extractors;

public:

    Set(string tag) {
        this->tag = tag;
    }

    void add_attribute(string name, attribute_type type);
    void add_attributes(vector<string> names, vector<attribute_type> types);

    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge);
    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
            string attribute_name, attribute_type attribute_type);
    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
            vector<string> attribute_names, vector<attribute_type> attribute_types);

    string get_tag();
    Attribute& get_attribute_by_name(string name);
    Extractor& get_extractor_by_tag(string tag);
    map<string, Extractor>& get_extractors();
    string get_specification();
};