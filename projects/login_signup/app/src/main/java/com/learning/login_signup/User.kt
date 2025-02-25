package com.learning.login_signup

class User (private var name : String, private var email : String, private var password : String) {
    override fun toString(): String {
        return "User(name='$name', email='$email', password='$password')"
    }
//    to get the attributes
    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getPassword(): String {
        return password
    }

//    setters
    fun setName(name: String) {
        this.name = name
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }

}

//    data class is a class that only contains data, and no functions. Data classes are immutable
//    => provides methods:
//    equals()
//    hashCode()
//    toString()
//    copy()
