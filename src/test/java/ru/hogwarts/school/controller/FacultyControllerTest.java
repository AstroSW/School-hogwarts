package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.core.ParameterizedTypeReference;
import ru.hogwarts.school.model.Student;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String baseUrl;
    private Faculty testFaculty;

    @BeforeEach
    void SetUp() {
        baseUrl = "http://localhost:" + port + "/faculty";

        testFaculty = new Faculty();
        testFaculty.setName("Стихия");
        testFaculty.setColor("Ультро");
    }

    @Test
    void createFaculty_Test() throws Exception {
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl, testFaculty, Faculty.class);

        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Стихия");
        assertThat(response.getBody().getColor()).isEqualTo("Ультро");
    }

    @Test
    void findFaculty_Test() throws Exception {
        Faculty create = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + create.getId(), Faculty.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(create.getId());
        assertThat(response.getBody().getName()).isEqualTo("Стихия");
        assertThat(response.getBody().getColor()).isEqualTo("Ультро");
    }

    @Test
    void findFaculty_TestNotFaculty() throws Exception {
        Faculty create = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/999", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllFaculties_Test() throws Exception {
        restTemplate.postForEntity(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(baseUrl, Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editFaculty_Test() throws Exception {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);
        created.setName("Металл");
        created.setColor("Золотой");

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(created),
                Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Металл");
        assertThat(response.getBody().getColor()).isEqualTo("Золотой");
    }

    @Test
    void deleteFaculty_Test() throws Exception {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        restTemplate.delete(baseUrl + "/" + created.getId());

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + created.getId(), Faculty.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByFacultyOrColor_Test() throws Exception {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Collection<Faculty>> response = restTemplate.exchange(
                baseUrl + "/?search=Металл",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .anySatisfy(f -> assertThat(f.getName()).contains("Металл"));
    }

    @Test
    void getStudentsByFaculty_Test() throws Exception {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);
        Student student = new Student();
        student.setName("Иван");
        student.setAge(19);
        student.setFaculty(created);
        restTemplate.postForObject("/student", student, Student.class);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(
                baseUrl + "/" + created.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });
        System.out.println("response: " + response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}

