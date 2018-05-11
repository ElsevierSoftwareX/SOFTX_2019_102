
#include "set.h"

void Set::add_attribute(string name, attribute_type type) {
    Attribute new_attribute = Attribute(name, type);
    this->attributes.push_back(new_attribute);
}

void Set::add_attributes(vector<string> names, vector<attribute_type> types) {
    if (names.size() == types.size()) {
        for (int index = 0; index < names.size(); index++) {
            string name = names.at(index);
            attribute_type type = types.at(index);
            this->add_attribute(name, type);
        }
    }
}

Extractor& Set::add_extractor(string extractor_tag, method_type method, cartridge_type cartridge) {
    Extractor new_extractor = Extractor(extractor_tag, this->tag, method, cartridge);
    this->extractors.insert(make_pair(extractor_tag, new_extractor));
    return this->get_extractor_by_tag(extractor_tag);
}

Extractor& Set::add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
        string attribute_name, attribute_type attribute_type) {
    Extractor& new_extractor = this->add_extractor(extractor_tag, method, cartridge);
    new_extractor.add_attribute(attribute_name, attribute_type);
    return new_extractor;
}

Extractor& Set::add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
        vector<string> attribute_names, vector<attribute_type> attribute_types) {
    Extractor& new_extractor = this->add_extractor(extractor_tag, method, cartridge);
    new_extractor.add_attributes(attribute_names, attribute_types);
    return new_extractor;
}

string Set::get_tag() {
    return this->tag;
}

Attribute& Set::get_attribute_by_name(string name) {
    try {
        for (int index = 0; index< this->attributes.size(); index++) {
            Attribute& attribute = this->attributes.at(index);
            if (attribute.get_name() == name) {
                return attribute;
            }
        }
    } catch (exception e) {
        cout << "Attribute " + name + " was not captured by extractor " + this->get_tag() << endl;
    }
}

Extractor& Set::get_extractor_by_tag(string tag) {
    try {
        return this->extractors.find(tag)->second;
    } catch (exception e) {
        cout << "Dataset " + this->get_tag() + " does not have the extractor named as " + tag << endl;
    }
}

map<string, Extractor>& Set::get_extractors(){
    return this->extractors;
}

string Set::get_specification() {
    string attribute_names = "";
    string attribute_types = "";

    if (!this->attributes.empty()) {
        for (Attribute attribute : this->attributes) {
            if (!attribute_names.empty() and !attribute_types.empty()) {
                attribute_names += ";";
                attribute_types += ";";
            }
            attribute_names += attribute.get_name();
            attribute_types += attribute.get_type();
        }

        attribute_names = "{" + attribute_names + "}";
        attribute_types = "{" + attribute_types + "}";
    } else {
        attribute_names = " ";
        attribute_types = " ";
    }

    return "dataset(" + this->tag + "," +
            attribute_names + "," + attribute_types + ")";
}