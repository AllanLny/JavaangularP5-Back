package com.openclassrooms.starterjwt.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    private final Date fixedDate = Date.from(LocalDate.of(2025, 2, 23).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
    }

    @Test
    void testCreateSession() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("A relaxing yoga session");
        sessionDto.setDate(fixedDate);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"));
    }

    @Test
    void testFindById() throws Exception {
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session = sessionRepository.save(session);

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"));
    }

    @Test
    void testFindAll() throws Exception {
        Session session1 = new Session();
        session1.setName("Yoga Session 1");
        session1.setDescription("A relaxing yoga session 1");
        session1.setDate(fixedDate);
        sessionRepository.save(session1);

        Session session2 = new Session();
        session2.setName("Yoga Session 2");
        session2.setDescription("A relaxing yoga session 2");
        session2.setDate(fixedDate);
        sessionRepository.save(session2);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yoga Session 1"))
                .andExpect(jsonPath("$[1].name").value("Yoga Session 2"));
    }

    @Test
    void testUpdateSession() throws Exception {
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session = sessionRepository.save(session);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Updated Yoga Session");
        sessionDto.setDescription("An updated relaxing yoga session");
        sessionDto.setDate(fixedDate);

        mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"))
                .andExpect(jsonPath("$.description").value("An updated relaxing yoga session"));
    }

    @Test
    void testDeleteSession() throws Exception {
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        Optional<Session> deletedSession = sessionRepository.findById(session.getId());
        assertFalse(deletedSession.isPresent());
    }

    @Test
    void testParticipateInSession() throws Exception {
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session = sessionRepository.save(session);

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testNoLongerParticipateInSession() throws Exception {
        Session session = new Session();
        session.setName("Yoga Session");
        session.setDescription("A relaxing yoga session");
        session.setDate(fixedDate);
        session = sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/1"))
                .andExpect(status().isOk());
    }
}