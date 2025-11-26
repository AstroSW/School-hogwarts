package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerWebMvcTest {

    private final Long id = 1L;
    private final String name = "Иван";
    private final int age = 20;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoSpyBean
    private StudentService studentService;

    @Autowired
    private StudentController studentController;

    @Test
    void createStudent_Test() throws Exception {

        JSONObject studentJson = new JSONObject();
        studentJson.put("name", name);
        studentJson.put("age", age);

        Student teststudent = new Student();
        teststudent.setId(id);
        teststudent.setName(name);
        teststudent.setAge(age);

        when(studentRepository.save(any(Student.class))).thenReturn(teststudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    void findStudent_Test() throws Exception {
        Student testStudent = new Student();
        testStudent.setId(id);
        testStudent.setName(name);
        testStudent.setAge(age);

        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    void findStudent_TestNotExist() throws Exception {
        Student testStudent = new Student();
        testStudent.setId(id);
        testStudent.setName(name);
        testStudent.setAge(age);

        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void editStudent_Test() throws Exception {
        JSONObject studentJson = new JSONObject();
        studentJson.put("id", id);
        studentJson.put("name", name);
        studentJson.put("age", age);

        Student testStudent = new Student();
        testStudent.setId(id);
        testStudent.setName(name);
        testStudent.setAge(age);

        when(studentRepository.existsById(any(Long.class))).thenReturn(true);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    void editStudent_TestNotStudent() throws Exception {
        JSONObject studentJson = new JSONObject();
        studentJson.put("id", id);
        studentJson.put("name", name);
        studentJson.put("age", age);

        when(studentRepository.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_Test() throws Exception {
        doNothing().when(studentRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllStudents_Test() throws Exception {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName(name);
        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Марья");

        when(studentRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Марья"));
    }

    @Test
    void findStudentByAgeBetween_Test() throws Exception {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setAge(20);
        Student s2 = new Student();
        s2.setId(2L);
        s2.setAge(19);

        when(studentRepository.findByAgeBetween(any(int.class), any(int.class))).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/")
                        .param("minAge", "19")
                        .param("maxAge", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].age").value(19));
    }

    @Test
    void getFacultyByStudent_Test() throws Exception {
        Faculty testFaculty = new Faculty();
        testFaculty.setId(2L);
        testFaculty.setName("Стихия");

        Student testStudent = new Student();
        testStudent.setId(id);
        testStudent.setFaculty(testFaculty);

        when(studentRepository.findById(id)).thenReturn(Optional.of(testStudent));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Стихия"));
    }
}