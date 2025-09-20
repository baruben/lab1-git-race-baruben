package es.unizar.webeng.hello.enum


/**
 * Represents roles assigned to a user for access control.
 */
enum class Role { 
    
    /** Guest role with minimal access */
    GUEST, 
    
    /** Regular user role */
    USER
}