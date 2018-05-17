/* 
 * File:   dataset.h
 * Author: vitor
 *
 * Created on March 31, 2018, 10:29 AM
 */

#include "element.h"

#include <string>
#include <vector>
#include <sstream>

using namespace std;

/*!
 * Class to represent a dataset (retrospective provenance).
 */
class Dataset{
protected:
    string tag; //!< dataset tag
    vector<Element> elements; //!< set of data elements
    
public:
    /*!
     Constructor of a dataset.
     \param tag a dataset tag
     */
    Dataset(string tag){
        this->tag = tag;
    }
    
    /*!
     Add a data element with a single value to dataset.
     \param value an attribute value
     */
    void add_element_with_value(string value);
    
    /*!
     Add a data element with multiple attribute values.
     \param values set of attribute values
     */
    void add_element_with_values(vector<string> values);
    
    /*!
     Clear the set of data elements.
     */
    void clear_elements();
    
    /*!
     Get the dataset tag.
     \return the dataset tag
     */
    string get_tag();
    
    /*!
     Get the data elements.
     \return the reference of the set of data elements
     */
    vector<Element>& get_elements();    
    
    /*!
     Get dataset specification.
     \return the dataset specification
     */
    string get_specification();
};