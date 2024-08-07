package com.example.testtask.service;

import exception.InsufficientFundsException;
import exception.UserNotFoundException;
import model.Account;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.UserRepository;
import service.UserService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User userFrom;
    private User userTo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем пользователей для тестирования
        userFrom = new User();
        userFrom.setId(1L);
        userFrom.setAccount(new Account());
        userFrom.getAccount().setBalance(new BigDecimal("100.00"));

        userTo = new User();
        userTo.setId(2L);
        userTo.setAccount(new Account());
        userTo.getAccount().setBalance(new BigDecimal("50.00"));
    }

    @Test
    public void transferFunds_Success() throws UserNotFoundException, InsufficientFundsException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFrom));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTo));
        when(userRepository.save(any(User.class))).thenReturn(userFrom); // Мокируем сохранение

        boolean result = userService.transferFunds(1L, 2L, new BigDecimal("50.00"));

        assertEquals(true, result);
        assertEquals(new BigDecimal("50.00"), userFrom.getAccount().getBalance());
        assertEquals(new BigDecimal("100.00"), userTo.getAccount().getBalance());
    }

    @Test
    public void transferFunds_InsufficientFunds() throws UserNotFoundException, InsufficientFundsException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFrom));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTo));

        boolean result = userService.transferFunds(1L, 2L, new BigDecimal("150.00"));

        assertEquals(false, result);
        assertEquals(new BigDecimal("100.00"), userFrom.getAccount().getBalance());
        assertEquals(new BigDecimal("50.00"), userTo.getAccount().getBalance());
    }

    @Test
    public void transferFunds_UserNotFound() throws UserNotFoundException, InsufficientFundsException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = userService.transferFunds(1L, 2L, new BigDecimal("50.00"));

        assertEquals(false, result);
    }
}

