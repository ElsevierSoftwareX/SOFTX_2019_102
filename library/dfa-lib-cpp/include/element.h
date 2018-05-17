/* 
 * File:   element.h
 * Author: vitor
 *
 * Created on March 31, 2018, 10:32 AM
 */

#include <string>
#include <vector>
#include <sstream>
#include <boost/algorithm/string/join.hpp>

using namespace std;

/*!
 Class to represent a data element (retrospective provenance).
 */
class Element{
protected:
    vector<string> values; //!< set of attribute values
    
public:
    
    /*!
     Constructor of a data element.
     */
    Element(){};
    
    /*!
     Constructor of a data element.
     \param value a single attribute value
     */
    Element(string value){
        this->add_value(value);
    }
    
    /*!
     Constructor of a data element.
     \param values a set of attribute values
     */
    Element(vector<string> values){
        this->set_values(values);
    }
    
    /*!
     Add attribute value to a data element in an ordered manner. 
     Each invocation of this member function will add the attribute value at the end of the set of attribute values.
     \param value attribute value
     */
    void add_value(string value);
    
    /*!
     Set attribute values.
     \param a set of attribute values
     */
    void set_values(vector<string> values);
    
    /*!
     Get attribute values
     \return the attribute values
     */
    vector<string>& get_values();
    
    /*!
     Get attribute values.
     \return the attribute values as string.
     */
    string get_values_as_string();
    
};