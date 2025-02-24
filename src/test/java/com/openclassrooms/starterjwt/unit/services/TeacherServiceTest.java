package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Teacher> teachers = new ArrayList<>();
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> foundTeachers = teacherService.findAll();

        assertNotNull(foundTeachers);
        assertEquals(teachers, foundTeachers);
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Teacher teacher = new Teacher();
        when(teacherRepository.findById(anyLong())).thenReturn(Optional.of(teacher));

        Teacher foundTeacher = teacherService.findById(1L);

        assertNotNull(foundTeacher);
        assertEquals(teacher, foundTeacher);
        verify(teacherRepository, times(1)).findById(1L);
    }
}