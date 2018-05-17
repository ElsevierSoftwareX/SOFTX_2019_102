/* 
 * File:   attribute_enum.h
 * Author: vitor
 *
 * Created on April 3, 2018, 9:09 AM
 */

#ifndef ATTRIBUTE_ENUM_H
#define ATTRIBUTE_ENUM_H

/*!
 Attribute type, which can be a TEXT (e.g., string or text), NUMERIC (e.g., int, float, double) or RDFILE (e.g., file path).
 */
enum attribute_type {
    TEXT,
    NUMERIC,
    RDFILE
};

#endif /* ATTRIBUTE_ENUM_H */

