package com.openclassrooms.starterjwt.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    private final Date fixedDate = Date.from(LocalDate.of(2025, 2, 23).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @BeforeEach
    void setUp() throws Exception {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testCreateSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("A relaxing yoga session");
        sessionDto.setDate(fixedDate);
        sessionDto.setTeacher_id(teacher.getId());

        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"));
    }

    @Test
    void testFindById() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"));
    }

    @Test
    void testFindAll() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session1 = new Session();
        session1.setName("Yoga Session 1");
        session1.setDescription("A relaxing yoga session 1");
        session1.setDate(fixedDate);
        session1.setTeacher(teacher);
        sessionRepository.save(session1);

        Session session2 = new Session();
        session2.setName("Yoga Session 2");
        session2.setDescription("A relaxing yoga session 2");
        session2.setDate(fixedDate);
        session2.setTeacher(teacher);
        sessionRepository.save(session2);

        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yoga Session 1"))
                .andExpect(jsonPath("$[1].name").value("Yoga Session 2"));
    }

    @Test
    void testUpdateSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Yoga Session");
        sessionDto.setDescription("An updated relaxing yoga session");
        sessionDto.setDate(fixedDate);
        sessionDto.setTeacher_id(teacher.getId());

        mockMvc.perform(put("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"))
                .andExpect(jsonPath("$.description").value("An updated relaxing yoga session"));
    }

    @Test
    void testDeleteSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        Optional<Session> deletedSession = sessionRepository.findById(session.getId());
        assertFalse(deletedSession.isPresent());
    }

    @Test
    void testParticipateInSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("participant@test.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Participant");
        signupRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        User participant = userRepository.findByEmail("participant@test.com").orElseThrow();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + participant.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testNoLongerParticipateInSession() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session.setTeacher(teacher);
        session = sessionRepository.save(session);

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("participant@test.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Participant");
        signupRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        User participant = userRepository.findByEmail("participant@test.com").orElseThrow();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + participant.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + participant.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}