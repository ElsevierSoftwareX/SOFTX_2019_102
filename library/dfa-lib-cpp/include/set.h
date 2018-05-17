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

/*!
 Class to represent a dataset specification (prospective provenance).
 */
class Set {
protected:
    string tag; //!< dataset tag
    vector<Attribute> attributes; //!< set of attributes
    map<string, Extractor> extractors; //!< map of extractor tag and its object (i.e., Extractor)

public:

    /*!
     Constructor of a dataset specification.
     \param tag a dataset tag
     */
    Set(string tag) {
        this->tag = tag;
    }

    /*!
     Add a single attribute to the dataset specification.
     \param name an attribute name
     \param type an attribute type
     */
    void add_attribute(string name, attribute_type type);
    
    /*!
     Add multiple attributes to the dataset specification.
     \param names a set of attribute names
     \param types a set of attribute types
     */
    void add_attributes(vector<string> names, vector<attribute_type> types);

    /*!
     Add extractor to the dataset specification.
     \param extractor_tag an extractor tag
     \param method an extractor method
     \param cartridge an extractor cartridge
     */
    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge);
    
    /*!
     Add extractor to the dataset specification with a single attribute.
     \param extractor_tag an extractor tag
     \param method an extractor method
     \param cartridge an extractor cartridge
     \param attribute_name an attribute name
     \param attribute_type an attribute type
     */
    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
            string attribute_name, attribute_type attribute_type);
    
    /*!
     Add extractor to the dataset specification with multiple attributes.
     \param extractor_tag an extractor tag
     \param method an extractor method
     \param cartridge an extractor cartridge
     \param attribute_names a set of attribute names
     \param attribute_types a set of attribute types
     */
    Extractor& add_extractor(string extractor_tag, method_type method, cartridge_type cartridge,
            vector<string> attribute_names, vector<attribute_type> attribute_types);

    /*!
     Get dataset tag.
     \return the dataset tag
     */
    string get_tag();
    
    /*!
     Get attribute given a name.
     \name an attribute name
     \return the reference of the attribute with the given name
     */
    Attribute& get_attribute_by_name(string name);
    
    /*!
     Get extractor given a tag.
     \tag an extractor tag
     \return the reference of the extractor with the given tag
     */
    Extractor& get_extractor_by_tag(string tag);
    
    /*!
     Get data extractors.
     \return the map with extractor name and its object derived from Extractor
     */
    map<string, Extractor>& get_extractors();
    
    /*!
     Get dataset specification.
     \return the dataset specification
     */
    string get_specification();
};