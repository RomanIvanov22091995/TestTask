package repository;

import jakarta.persistence.Cacheable;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable(value = "usersCache", key = "#email")
    List<User> findByEmail(String email);
    @Cacheable(value = "usersCache", key = "#phone")
    List<User> findByPhone(String phone);

}

