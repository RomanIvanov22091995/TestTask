package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String password; // Хранить в зашифрованном виде

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EmailData> emailDataList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhoneData> phoneDataList;
}

