package com.example.wad_mid_exam.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wad_mid_exam.data.DatabaseHelper
import com.example.wad_mid_exam.data.Student

val MAJORS = listOf("Informatics", "Information Systems")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(dbHelper: DatabaseHelper) {
    val context = LocalContext.current

    var students by remember { mutableStateOf(emptyList<Student>()) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var major by remember { mutableStateOf(MAJORS[0]) }
    var isMajorExpanded by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    fun loadStudents() {
        students = dbHelper.getAllStudents()
    }

    LaunchedEffect(Unit) {
        loadStudents()
    }

    fun clearInputs() {
        studentId = ""
        name = ""
        major = MAJORS[0]
        selectedStudent = null
    }

    fun saveStudent() {
        val idInput = studentId.trim()
        val nameInput = name.trim()

        if (idInput.isEmpty() || nameInput.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStudent == null) {
            if (dbHelper.isIdExists(idInput)) {
                Toast.makeText(context, "Student ID must be unique!", Toast.LENGTH_SHORT).show()
                return
            }
            val added = dbHelper.addStudent(Student(idInput, nameInput, major))
            if (added) {
                Toast.makeText(context, "Student added", Toast.LENGTH_SHORT).show()
                clearInputs()
                loadStudents()
            } else {
                Toast.makeText(context, "Failed to add student", Toast.LENGTH_SHORT).show()
            }
        } else {
            val oldId = selectedStudent!!.studentId
            if (idInput != oldId && dbHelper.isIdExists(idInput)) {
                Toast.makeText(context, "Student ID already exists!", Toast.LENGTH_SHORT).show()
                return
            }
            val updated = dbHelper.updateStudent(oldId, Student(idInput, nameInput, major))
            if (updated) {
                Toast.makeText(context, "Student updated", Toast.LENGTH_SHORT).show()
                clearInputs()
                loadStudents()
            } else {
                Toast.makeText(context, "Failed to update student", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteStudent(s: Student) {
        if (dbHelper.deleteStudent(s.studentId)) {
            Toast.makeText(context, "Deleted ${s.name}", Toast.LENGTH_SHORT).show()
            if (selectedStudent?.studentId == s.studentId) {
                clearInputs()
            }
            loadStudents()
        } else {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
        studentToDelete = null
    }

    val filteredList = remember(students, searchQuery) {
        if (searchQuery.isBlank()) students
        else students.filter {
            it.studentId.contains(searchQuery, true) ||
                    it.name.contains(searchQuery, true) ||
                    it.major.contains(searchQuery, true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Database", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (selectedStudent == null) "Add Student" else "Edit Student",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (studentId.isNotEmpty()) {
                                IconButton(onClick = { studentId = "" }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        }
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (name.isNotEmpty()) {
                                IconButton(onClick = { name = "" }) {
                                    Icon(Icons.Default.Clear, "Clear")
                                }
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = major,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Major") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, "Select Major")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { isMajorExpanded = true }
                        )
                        DropdownMenu(
                            expanded = isMajorExpanded,
                            onDismissRequest = { isMajorExpanded = false }
                        ) {
                            MAJORS.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        major = option
                                        isMajorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { saveStudent() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (selectedStudent == null) "Add" else "Update")
                        }

                        OutlinedButton(
                            onClick = { clearInputs() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No students found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList, key = { it.studentId }) { item ->
                        StudentCard(
                            student = item,
                            isSelected = selectedStudent?.studentId == item.studentId,
                            onEdit = {
                                selectedStudent = item
                                studentId = item.studentId
                                name = item.name
                                major = item.major
                            },
                            onDelete = { studentToDelete = item }
                        )
                    }
                }
            }
        }
    }

    studentToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Delete Student") },
            text = { Text("Delete ${item.name} (${item.studentId})?") },
            confirmButton = {
                TextButton(onClick = { deleteStudent(item) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    isSelected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${student.name} (${student.studentId})", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(student.major, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
