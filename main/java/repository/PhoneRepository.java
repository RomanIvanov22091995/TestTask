package repository;

import model.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
}

