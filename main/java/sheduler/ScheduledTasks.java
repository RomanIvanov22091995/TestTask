package sheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.UserService;

import java.math.BigDecimal;

@Slf4j
@Component
public class ScheduledTasks {

    private final UserService userService;

    public ScheduledTasks(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 30000)
    public void increaseBalances() {
        userService.getAllUsers().forEach(user -> {
            BigDecimal currentBalance = user.getAccount().getBalance();
            log.debug("Current balance: {}", currentBalance);
            log.debug("For user: {}", user.getId());

            BigDecimal initialBalance = user.getAccount().getInitialBalance();



            // Проверяем, не превышает ли текущий баланс 207% от начального
            if (currentBalance.compareTo(initialBalance.multiply(new BigDecimal("2.07"))) < 0) {
                // Увеличиваем баланс на 10%
                BigDecimal newBalance = currentBalance.multiply(new BigDecimal("1.1"));
                log.debug("New balance: {}", currentBalance);

                // Устанавливаем новый баланс, не превышая 207% от начального
                user.getAccount().setBalance(newBalance.min(initialBalance.multiply(new BigDecimal("2.07"))));
                userService.save(user); // Сохранение пользователя с обновленным балансом
            }
        });
    }
}

