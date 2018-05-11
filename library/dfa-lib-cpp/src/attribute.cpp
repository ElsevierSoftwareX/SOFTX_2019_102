#include "attribute.h"

string Attribute::get_name(){
    return this->name;
}
    
string Attribute::get_type(){
    if(this->type == NUMERIC){
        return "numeric";
    }else if(this->type == TEXT){
        return "text";
    }else if(this->type == RDFILE){
        return "file";
    }    
}