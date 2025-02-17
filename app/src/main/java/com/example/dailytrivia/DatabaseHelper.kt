package com.example.dailytrivia

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Step 1: Create DatabaseHelper class extending SQLiteOpenHelper
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Step 2: Define Database Name and Version
    companion object {
        private const val DATABASE_NAME = "MyDatabase.db"
        private const val DATABASE_VERSION = 1

        // Define Table and Columns
        const val TABLE_NAME = "Settings"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_QUESTION_TYPE = "question_type"
        const val COLUMN_DIFFICULTY_LEVEL = "difficulty"
        const val COLUMN_QUESTION_NUMBER = "question_number"
        const val COLUMN_USER = "user_email"

        const val TABLE_NAME2 = "Quiz"
        const val COLUMN_QUIZ_ID = "quiz_id"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_SCORE = "score"
        const val COLUMN_QUESTION_NUMBER2 = "question_number"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_USER2 = "user_email"

        const val TABLE_NAME3 = "Trending_Quiz"
        const val COLUMN_QUIZ_ID2 = "quiz_id"
        const val COLUMN_CATEGORY2 = "category"
        const val COLUMN_SCORE2 = "score"
        const val COLUMN_QUESTION_NUMBER3 = "question_number"
        const val COLUMN_TIMESTAMP2 = "timestamp"
        const val COLUMN_USER3 = "user_email"

    }

    // Step 3: Create Table SQL Query
    private val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_USER STRING PRIMARY KEY, " +
            "$COLUMN_QUESTION_TYPE TEXT, " +
            "$COLUMN_DIFFICULTY_LEVEL TEXT," +
            "$COLUMN_QUESTION_NUMBER INTEGER," +
            "$COLUMN_CATEGORY_ID INTEGER)"

    private val CREATE_TABLE_QUIZ = "CREATE TABLE $TABLE_NAME2 (" +
            "$COLUMN_QUIZ_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COLUMN_CATEGORY TEXT, " +
            "$COLUMN_SCORE DOUBLE, " +
            "$COLUMN_QUESTION_NUMBER2 INTEGER, " + // Question number column
            "$COLUMN_TIMESTAMP INTEGER," + // Add timestamp column (storing it as long integer)
            "$COLUMN_USER2 STRING)"

    private val CREATE_TABLE_TRENDING_QUIZ = "CREATE TABLE $TABLE_NAME3 (" +
            "$COLUMN_QUIZ_ID2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COLUMN_CATEGORY2 TEXT, " +
            "$COLUMN_SCORE2 DOUBLE, " +
            "$COLUMN_QUESTION_NUMBER3 INTEGER, " + // Question number column
            "$COLUMN_TIMESTAMP2 INTEGER," + // Add timestamp column (storing it as long integer)
            "$COLUMN_USER3 STRING)"

    // Step 4: Drop Table SQL Query
    private val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    private val DROP_TABLE2 = "DROP TABLE IF EXISTS $TABLE_NAME2"
    private val DROP_TABLE3 = "DROP TABLE IF EXISTS $TABLE_NAME3"

    // Step 5: Override onCreate method
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE) // Execute the table creation SQL
        db.execSQL(CREATE_TABLE_QUIZ) // Execute the table creation SQL
        db.execSQL(CREATE_TABLE_TRENDING_QUIZ) // Execute the table creation SQL
    }

    // Step 6: Override onUpgrade method
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE) // Drop older table if it exists
        db.execSQL(DROP_TABLE2) // Drop older table if it exists
        db.execSQL(DROP_TABLE3) // Drop older table if it exists
        onCreate(db)           // Create the table again
    }

    // Method to check if the table is empty
    fun isTableEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    fun isQuizTableEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME2", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    fun isTrendingQuizTableEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME3", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    // Method to delete all records from the table
    fun deleteAllRecords() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }

    fun deleteAllQuizRecords() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME2, null, null)
    }

    fun deleteAllTrendingQuizRecords() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME3, null, null)
    }

}
