package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;
    private Student testStudent;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";

        testFaculty = new Faculty();
        testFaculty.setName("Стихия");
        testFaculty.setColor("Ультро");
        testFaculty = facultyRepository.save(testFaculty);

        testStudent = new Student();
        testStudent.setName("Иван");
        testStudent.setAge(20);
        testStudent.setFaculty(testFaculty);
    }

    @Test
    void createStudent_Test() throws Exception {
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, testStudent, Student.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Иван");
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void findStudent_Test() throws Exception {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + created.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(created.getId());
        assertThat(response.getBody().getName()).isEqualTo("Иван");
    }

    @Test
    void findStudent_TestNotExist() throws Exception {
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/999", Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void editStudent_Test() throws Exception {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);
        created.setName("Марья");

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(created),
                Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Марья");
    }

    @Test
    void deleteStudent_Test() throws Exception {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);
        restTemplate.delete(baseUrl + "/" + created.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + created.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllStudent_Test() throws Exception {
        restTemplate.patchForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(baseUrl, Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getStudentsByAgeBetween_Test() throws Exception {
        restTemplate.patchForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                baseUrl + "/?minAge=19&maxAge=20", Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getFacultyByStudent_Test() throws Exception {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + created.getId() + "/faculty", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Стихия");
    }
}
