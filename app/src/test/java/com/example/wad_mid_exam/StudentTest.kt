package com.example.wad_mid_exam

import com.example.wad_mid_exam.data.Student
import com.example.wad_mid_exam.ui.MAJORS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentTest {

    @Test
    fun studentCreation_isCorrect() {
        val student = Student("2025001", "John Doe", "Informatics")
        assertEquals("2025001", student.studentId)
        assertEquals("John Doe", student.name)
        assertEquals("Informatics", student.major)
    }

    @Test
    fun majorOptions_containsCase1Majors() {
        assertTrue(MAJORS.contains("Informatics"))
        assertTrue(MAJORS.contains("Information Systems"))
        assertEquals(2, MAJORS.size)
    }
}
