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

/*!
 Class to represent a data extractor specification (prospective provenance).
 */
class Extractor {
protected:
    string tag; //!< extractor tag
    string set_tag; //!< dataset tag which extracted data will be loaded
    method_type method; //!< method of the raw data extractor, which can be EXTRACTION or INDEXING
    cartridge_type cartridge; //!< cartridge of the raw data extractor, which can be CSV (extraction or indexing), PROGRAM (extraction), FASTBIT, OPTIMIZED_FASTBIT, POSTGRES_RAW (indexing)
    vector<Attribute> attributes; //!< set of attributes

public:

    /*!
     Constructor of a raw data extractor.
     \param tag an extractor tag
     \param set_tag a dataset tag
     \param method_type an extraction or indexing method
     \param cartridge_type a cartridge to be executed by the extractor*/
    Extractor(string tag, string set_tag, method_type method, cartridge_type cartridge) {
        this->tag = tag;
        this->set_tag = set_tag;
        this->method = method;
        this->cartridge = cartridge;
    }

    /*!
     Add an attribute to be extracted.
     \param name an attribute name
     \param type an attribute type
     */
    void add_attribute(string name, attribute_type type);
    
    /*!
     Add a set of attributes
     \param names a set of attribute names
     \param types a set of attribute types
    */
    void add_attributes(vector<string> names, vector<attribute_type> types);

    /*!
     Get the raw data extractor tag.
     \return the extractor tag
     */
    string get_tag();
    
    /*!
     Get the dataset tag.
     \return the dataset tag
     */
    string get_set_tag();
    
    /*!
     Get the attribute given its name.
     \param name an attribute name
     \return the reference of the attribute with the given name
     */
    Attribute& get_attribute_by_name(string name);
    
    /*!
     Get the method of raw data extractor.
     \return the extraction or indexing method
     */
    string get_method();
    
    /*!
     Get the cartridge implemented by the raw data extractor.
     \return the extractor cartridge
     */
    string get_cartridge();
    
    /*!
     Get the specification of raw data extractor.
     \return the extractor specification.
     */
    string get_specification();
};

#endif /* EXTRACTOR_H */