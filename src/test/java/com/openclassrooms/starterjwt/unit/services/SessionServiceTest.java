package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSession() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session createdSession = sessionService.create(session);

        assertNotNull(createdSession);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testDeleteSession() {
        doNothing().when(sessionRepository).deleteById(anyLong());

        sessionService.delete(1L);

        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAllSessions() {
        List<Session> sessions = new ArrayList<>();
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> foundSessions = sessionService.findAll();

        assertNotNull(foundSessions);
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetSessionById() {
        Session session = new Session();
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        Session foundSession = sessionService.getById(1L);

        assertNotNull(foundSession);
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateSession() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session updatedSession = sessionService.update(1L, session);

        assertNotNull(updatedSession);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateInSession() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        User user = new User();
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateInSession() {
        User user = new User();
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        Session session = new Session();
        session.setUsers(users);
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertFalse(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).findById(1L);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateInSessionThrowsNotFoundException() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void testNoLongerParticipateInSessionThrowsNotFoundException() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }

    @Test
    void testParticipateInSessionThrowsBadRequestException() {
        User user = new User();
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        Session session = new Session();
        session.setUsers(users);
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void testNoLongerParticipateInSessionThrowsBadRequestException() {
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }
}