package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {StudentService.class})
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents() {
        //when
        underTest.getAllStudents();
        //then
        verify(studentRepository).findAll();
    }

    @Test
    void addStudent() {
        //given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );

        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    /**
     * Method under test: {@link StudentService#deleteStudent(Long)}
     */
    @Test
    void testDeleteStudent() {
        doNothing().when(studentRepository).deleteById((Long) org.mockito.Mockito.any());
        when(studentRepository.existsById((Long) org.mockito.Mockito.any())).thenReturn(true);
        studentService.deleteStudent(123L);
        verify(studentRepository).existsById((Long) org.mockito.Mockito.any());
        verify(studentRepository).deleteById((Long) org.mockito.Mockito.any());
    }

    /**
     * Method under test: {@link StudentService#deleteStudent(Long)}
     */
    @Test
    void testDeleteStudent2() {
        doThrow(new BadRequestException("Msg")).when(studentRepository).deleteById((Long) org.mockito.Mockito.any());
        when(studentRepository.existsById((Long) org.mockito.Mockito.any())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> studentService.deleteStudent(123L));
        verify(studentRepository).existsById((Long) org.mockito.Mockito.any());
        verify(studentRepository).deleteById((Long) org.mockito.Mockito.any());
    }

    /**
     * Method under test: {@link StudentService#deleteStudent(Long)}
     */
    @Test
    void testDeleteStudent3() {
        doNothing().when(studentRepository).deleteById((Long) org.mockito.Mockito.any());
        when(studentRepository.existsById((Long) org.mockito.Mockito.any())).thenReturn(false);
        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(123L));
        verify(studentRepository).existsById((Long) org.mockito.Mockito.any());
    }

    @Test
    void throwExcepionIfEmailExist() {
        //given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @Disabled
    void deleteStudent() {
    }
}