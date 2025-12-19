package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method for find student");
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        if (studentRepository.existsById(student.getId())) {
            return studentRepository.save(student);
        }
        logger.debug("Student id = {} is not exist", student.getId());
        return null;
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(id);
    }

    public List<Student> findAllStudent() {
        logger.info("Was invoked method for find all students");
        return studentRepository.findAll();
    }

    public List<Student> findStudentAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for find student age between");
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public List<Student> findStudentsByFaculty(Faculty faculty) {
        logger.info("Was invoked method for find student by faculty");
        return studentRepository.findByFaculty(faculty);
    }

    public int countAllStudents() {
        logger.info("Was invoked method for count all students");
        return studentRepository.countAllStudents();
    }

    public double countAvgAllStudents() {
        logger.info("Was invoked method for count avg all students");
        return studentRepository.countAvgAllStudents();
    }

    public List<Student> getLastStudent() {
        logger.info("Was invoked method for get last student");
        return studentRepository.getLastStudent();
    }
}