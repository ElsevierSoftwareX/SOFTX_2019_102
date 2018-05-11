#include "attribute.h"
#include "extractor.h"

#include <exception>
#include <sstream>

void Extractor::add_attribute(string name, attribute_type type) {
    Attribute new_attribute = Attribute(name, type);
    this->attributes.push_back(new_attribute);
}

void Extractor::add_attributes(vector<string> names, vector<attribute_type> types) {
    if (names.size() == types.size()) {
        for (int index = 0; index < names.size(); index++) {
            string name = names.at(index);
            attribute_type type = types.at(index);
            this->add_attribute(name, type);
        }
    }
}

string Extractor::get_tag() {
    return this->tag;
}

string Extractor::get_set_tag() {
    return this->set_tag;
}

Attribute& Extractor::get_attribute_by_name(string name) {
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

string Extractor::get_method() {
    string method_str("unknown");
    switch (this->method) {
        case EXTRACTION:
        {
            method_str = "EXTRACTION";
        }
        break;
        case INDEXING:
        {
            method_str = "INDEXING";
        }
        break;
    }
    return method_str;
}

string Extractor::get_cartridge() {
    string cartridge_str("unknown");
    switch (this->cartridge) {
        case CSV:
        {
            cartridge_str = "CSV";
        }
        break;
        case PROGRAM:
        {
            cartridge_str = "PROGRAM";
        }
        break;
        case FASTBIT:
        {
            cartridge_str = "FASTBIT";
        }
        break;
        case OPTIMIZED_FASTBIT:
        {
            cartridge_str = "OPTIMIZED_FASTBIT";
        }
        break;
        case POSTGRES_RAW:
        {
            cartridge_str = "POSTGRES_RAW";
        }
        break;
    }
    return cartridge_str;
}

string Extractor::get_specification() {
    if (!this->attributes.empty()) {
        string attribute_names = "";
        string attribute_types = "";

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

        return "extractor(" + this->set_tag + "," + this->tag + "," +
                this->get_method() + "," + this->get_cartridge() + "," +
                attribute_names + "," + attribute_types + ")";
    }

    return "";
}


