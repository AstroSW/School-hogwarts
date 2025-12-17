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
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FacultyControllerWebMvcTest {

    private final Long id = 1L;
    private final String name = "Стихия";
    private final String color = "Ультро";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoSpyBean
    private FacultyService facultyService;

    @Autowired
    private FacultyController facultyController;

    @Test
    void createFaculty_Test() throws Exception {

        JSONObject facultyJson = new JSONObject();
        facultyJson.put("name", name);
        facultyJson.put("color", color);

        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    void findFaculty_Test() throws Exception {
        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testFaculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    void findFaculty_TestNotExist() throws Exception {
        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testFaculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void editFaculty_Test() throws Exception {
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", id);
        facultyJson.put("name", name);
        facultyJson.put("color", color);

        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        when(facultyRepository.existsById(any(Long.class))).thenReturn(true);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    void editStudent_TestNotStudent() throws Exception {
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", id);
        facultyJson.put("name", name);
        facultyJson.put("color", color);

        when(facultyRepository.existsById(any(Long.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFaculty_Test() throws Exception {

        doNothing().when(facultyRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllFaculties_Test() throws Exception {
        Faculty f1 = new Faculty();
        f1.setId(1L);
        f1.setName(name);
        Faculty f2 = new Faculty();
        f2.setId(2L);
        f2.setName("Металл");

        when(facultyRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Металл"));
    }

    @Test
    void GetByFacultyOrColor() throws Exception {
        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(
                "Ультро", "Ультро")).thenReturn(Arrays.asList(testFaculty));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/faculty/")
                .param("search", "Ультро")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].color").value(color));
    }

    @Test
    void getStudentsByFaculty_Test() throws Exception {
        Faculty testFaculty = new Faculty();
        testFaculty.setId(id);
        testFaculty.setName(name);
        testFaculty.setColor(color);

        Student testStudent = new Student();
        testStudent.setId(2L);
        testStudent.setName("Иван");
        testStudent.setFaculty(testFaculty);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testFaculty));
        when(studentRepository.findByFaculty(testFaculty)).thenReturn(Arrays.asList(testStudent));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/faculty/1/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[0].faculty.id").value(id));
    }
}
