/* 
 * File:   raw_data_indexer.h
 * Author: vitor
 *
 * Created on April 13, 2018, 8:51 AM
 */

#ifndef RAW_DATA_INDEXER_H
#define RAW_DATA_INDEXER_H

#include <string>
#include <iostream>
#include <vector>
#include <sstream>
#include <stdlib.h>

#include "extractor_enum.h"
#include "attribute_enum.h"

using namespace std;

/*!
 Class to represent the component Raw Data Indexer or RDI (retrospective provenance).
 */
class RawDataIndexer {
protected:
    cartridge_type cartridge; //!< indexer cartridge
    string method = "INDEX"; //!< constant method to be performed by RDI, which is named as INDEX
    string extractor_tag; //!< extractor tag, which is implemented with an indexing technique
    string path; //!< path of the file with extracted data
    string file_name_with_extracted_data; //!< name of file with extracted data
    vector<string> attribute_names; //!< set of attribute names
    vector<attribute_type> attribute_types; //!< set of attribute types
    string extra_arguments; //!< set of extra arguments (optional)

    vector<string> values_of_attribute_types = {"TEXT", "NUMERIC", "RDFILE"}; //!< set of attribute types
    vector<string> values_of_cartridge_types = {"CSV", "PROGRAM", "FASTBIT", "OPTIMIZED_FASTBIT", "POSTGRES_RAW"}; //!< set of indexing cartridges

public:

    /*!
     Constructor of a raw data indexer.
     \param cartridge indexer a cartridge
     \param extractor_tag a name to the extractor implemented with an indexing technique
     \param path a path of the file with extracted data
     \param file_name_with_extracted_data a name of the file with extracted data
     \param attribute_names a set of attribute names to be indexed
     \param attribute_types a set of attribute types
     \param extra_arguments a set of extra arguments
     */
    RawDataIndexer(cartridge_type cartridge, string extractor_tag,
            string path, string file_name_with_extracted_data,
            vector<string> attribute_names, vector<attribute_type> attribute_types,
            string extra_arguments) {
        this->cartridge = cartridge;
        this->extractor_tag = extractor_tag;
        this->path = path;
        this->file_name_with_extracted_data = file_name_with_extracted_data;
        this->set_attribute_names(attribute_names);
        this->set_attribute_types(attribute_types);
        this->extra_arguments = extra_arguments;
    }

    /*!
     Set attribute names.
     \param attribute_names a set of attribute names.
     */
    void set_attribute_names(vector<string> attribute_names);
    
    /*!
     Set attribute types.
     \param attribute_types a set of attribute types.
     */
    void set_attribute_types(vector<attribute_type> attribute_types);

    /*!
     Run the component Raw Data Indexer.
     \return the exit status code of running RDI
     */
    int run();
    
    /*!
     Get command line.
     \return the command line.
     */
    string get_command_line();
    
    /*!
     Get attributes to be indexer.
     \return the attributes to be indexed as a string.*/
    string get_attributes_as_string();
    
    /*!
     Get cartridge.
     \return the cartridge of raw data indexer as string
     */
    string get_cartridge_as_string();
};

#endif /* RAW_DATA_INDEXER_H */

