package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 19, scale = 4) // Указываем точность и масштаб для DECIMAL
    private BigDecimal balance;

    @Column(precision = 19, scale = 4) // Указываем точность и масштаб для DECIMAL
    private BigDecimal initialBalance;

}

