/* 
 * File:   attribute.h
 * Author: vitor
 *
 * Created on March 31, 2018, 11:30 AM
 */

#include<string>

using namespace std;

#ifndef ATTRIBUTE_H
#define ATTRIBUTE_H

#include "attribute_enum.h"

/*!
Class to represent an attribute specification of the dataset schema (prospective provenance).
*/ 

class Attribute {
protected:
    string name; //!< attribute name
    attribute_type type; //!< attribute type, which can be TEXT, NUMERIC or RDFILE

public:
    /*!
     Constructor of an attribute.
     \param name an attribute name
     \param type an attribute type
    */
    Attribute(string name, attribute_type type){
        this->name = name;
        this->type = type;
    } 
    
    /*!
     * Get the attribute name
     * \return the attribute name
     */
    string get_name();

    /*!
     * Get the attribute type
     * \return the attribute type
     */
    string get_type();
};

#endif /* ATTRIBUTE_H */