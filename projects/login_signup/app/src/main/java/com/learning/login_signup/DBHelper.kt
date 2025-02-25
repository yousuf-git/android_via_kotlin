package com.learning.login_signup

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

//        when extending SQLiteOpenHelper, we need to implement the following methods:
//        onCreate(SQLiteDatabase db) - called when the database is created for the first time
//        onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) - called when the database needs to be upgraded

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                EMAIL_COL + " TEXT, " +
                PASSWORD_COL + " TEXT)")

        // calling sqlite method for executing the query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // This method is for creating user in our database
    fun signup(name : String, email : String, password : String) : Boolean {
//        check if user exists already
        val cursor = getUserByEmail(email)
        if (cursor.count > 0) {
            return false
        }

        val values = ContentValues()    // to insert data into database

        // inserting our values in the form of key-value pair

//        generating id for primary key
//        val id = (Math.random() * 1000).toInt() // using Autoincrement instead
        //        construct user and then save
//        val user = User(name, email, password)

        // putting the values in the key-value pair
        values.put(NAME_COL, name)
        values.put(EMAIL_COL, email)
        values.put(PASSWORD_COL, password)

        // writable variable of our database as I want to  insert data
        val db = this.writableDatabase

//        db.insert(table_name, nullColumnHack, ContentValues)
//        the second parameter is used when you don't have any values to insert
        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // closing our database
        db.close()
        return true // user inserted successfully
    }

    // below method is to get all users from our database
    fun getUsers() : Cursor {
//        Q: what is cursor ?
//        A: A Cursor represents the result of a query and basically points to one row of the query result.
//        This way Android can buffer the query results efficiently; as it does not have to load all data into memory.
//        It has methods like:
//        methodName : return type - desc
//        moveToFirst() : boolean - move the cursor to the first row
//        moveToNext() : boolean - move the cursor to the next row
//        getColumnIndex(String columnName) : int - get the column index for a given column name
//        getString(int columnIndex) : String - get the value of the requested column as a String
//        getInt(int columnIndex) : int - get the value of the requested column as an int
//        count() : int - get the number of rows in the cursor

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
//        selectionArgs means the value of the column

    }

//    get user by email
    private fun getUserByEmail(email: String) : Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $EMAIL_COL = '$email'", null)
//        same thing by using selectionArgs
//        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $EMAIL_COL = ?", arrayOf(email))
    }

//    login method
    fun login(email: String, password: String) : User? {
        val cursor = getUserByEmail(email)
        if (cursor.count == 0) {
            return null
        }

        cursor.moveToFirst()
        try {
            val savedPassword = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_COL))
            if (savedPassword == password) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))
                return User(name, email, password)
            } else {
                return null
            }
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }


    companion object {
//        companion object is used to create static variables and methods in kotlin
        // variables for my database

        // below is variable for database name
        private const val DATABASE_NAME = "login_app"

        // below is the variable for database version
        private const val DATABASE_VERSION = 1

        // below is the variable for table name
        const val TABLE_NAME = "users"

        // below is the variable for id column
        const val ID_COL = "id"

        // below is the variable for name column
        const val EMAIL_COL = "email"

        // below is the variable for age column
        const val PASSWORD_COL = "password"

        // below is the variable for Name column
        const val NAME_COL = "name"

    }
}