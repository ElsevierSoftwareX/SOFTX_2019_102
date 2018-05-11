/* 
 * File:   extractor_enum.h
 * Author: vitor
 *
 * Created on April 3, 2018, 8:56 AM
 */

#ifndef EXTRACTOR_ENUM_H
#define EXTRACTOR_ENUM_H

#include <string>

using namespace std;

enum method_type {
    EXTRACTION,
    INDEXING
};

enum cartridge_type {
    CSV,
    PROGRAM,
    FASTBIT,
    OPTIMIZED_FASTBIT,
    POSTGRES_RAW
};

#endif /* EXTRACTOR_ENUM_H */

