package com.example.wad_mid_exam.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "student.db"
        private const val DB_VERSION = 1

        const val TABLE_NAME = "students"
        const val COL_ID = "student_id"
        const val COL_NAME = "name"
        const val COL_MAJOR = "major"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSql = "CREATE TABLE $TABLE_NAME ($COL_ID TEXT PRIMARY KEY, $COL_NAME TEXT, $COL_MAJOR TEXT)"
        db.execSQL(createSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun isIdExists(id: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_NAME WHERE $COL_ID = ?", arrayOf(id.trim()))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun addStudent(student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, student.studentId.trim())
            put(COL_NAME, student.name.trim())
            put(COL_MAJOR, student.major.trim())
        }
        return try {
            db.insertOrThrow(TABLE_NAME, null, values) != -1L
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun updateStudent(oldId: String, student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, student.studentId.trim())
            put(COL_NAME, student.name.trim())
            put(COL_MAJOR, student.major.trim())
        }
        return try {
            db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(oldId.trim())) > 0
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun deleteStudent(id: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.trim())) > 0
    }

    fun getAllStudents(): List<Student> {
        val list = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_ID ASC", null)

        if (cursor.moveToFirst()) {
            val idCol = cursor.getColumnIndexOrThrow(COL_ID)
            val nameCol = cursor.getColumnIndexOrThrow(COL_NAME)
            val majorCol = cursor.getColumnIndexOrThrow(COL_MAJOR)

            do {
                list.add(
                    Student(
                        studentId = cursor.getString(idCol),
                        name = cursor.getString(nameCol),
                        major = cursor.getString(majorCol)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
