package service;

import exception.InsufficientFundsException;
import exception.UserNotFoundException;
import jakarta.persistence.Cacheable;
import model.EmailData;
import model.PhoneData;
import model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.EmailRepository;
import repository.PhoneRepository;
import repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final EmailRepository emailRepository;

    private final PhoneRepository phoneRepository;

    private final AccountRepository accountRepository;

    public UserService(UserRepository userRepository, EmailRepository emailRepository, PhoneRepository phoneRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String name, LocalDate dateOfBirth, String email, String phone, int page, int size) {
        Pageable pageable =  PageRequest.of(page, size);
        List<User> users = userRepository.findAll(pageable).getContent();

        if (name != null) {
            users = users.stream()
                    .filter(user -> user.getName().startsWith(name))
                    .collect(Collectors.toList());
        }
        if (dateOfBirth != null) {
            users = users.stream()
                    .filter(user -> user.getDateOfBirth().isAfter(dateOfBirth))
                    .collect(Collectors.toList());
        }
        if (email != null) {
            users = users.stream()
                    .filter(user -> user.getEmailDataList().stream().anyMatch(emailData -> emailData.getEmail().equals(email)))
                    .collect(Collectors.toList());
        }
        if (phone != null) {
            users = users.stream()
                    .filter(user -> user.getPhoneDataList().stream().anyMatch(phoneData -> phoneData.getPhone().equals(phone)))
                    .collect(Collectors.toList());
        }

        return users;
    }


    @Transactional
    public User updateEmail(Long userId, String oldEmail, String newEmail) {
        if (emailRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = (User) userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        EmailData emailData = user.getEmailDataList().stream()
                .filter(data -> data.getEmail().equals(oldEmail))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Old email not found"));

        emailData.setEmail(newEmail);
        return userRepository.save(user);

    }

    @Transactional
    public User updatePhone(Long userId, String oldPhone, String newPhone) {
        if (phoneRepository.existsByPhone(newPhone)) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        User user = (User) userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        PhoneData phoneData = user.getPhoneDataList().stream()
                .filter(data -> data.getPhone().equals(oldPhone))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Old phone not found"));

        phoneData.setPhone(newPhone);
        return userRepository.save(user);

    }

    @Cacheable(value = "usersCache", key = "#userId")
    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Метод для обработки трансфера
    public boolean transferFunds(Long fromUserId, Long toUserId, BigDecimal value)
            throws UserNotFoundException, InsufficientFundsException {
        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (!fromUserOpt.isPresent()) {
            throw new UserNotFoundException("From user not found");
        }
        if (!toUserOpt.isPresent()) {
            throw new UserNotFoundException("To user not found");
        }

        User fromUser = fromUserOpt.get();
        User toUser = toUserOpt.get();

        // Используем compareTo для сравнения BigDecimal
        if (fromUser.getAccount().getBalance().compareTo(value) < 0 || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromUser.getAccount().setBalance(fromUser.getAccount().getBalance().subtract(value));
        toUser.getAccount().setBalance(toUser.getAccount().getBalance().add(value));

        userRepository.save(fromUser);
        userRepository.save(toUser);
        return false;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public List<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public boolean validatePassword(User user, String password) {
        // Здесь должна быть логика для проверки пароля, например, с использованием bcrypt
        return user.getPassword().equals(password); // Замените на актуальную валидацию
    }
}


