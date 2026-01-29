package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Nurse;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {
    // Find a nurse by exact username
    Optional<Nurse> findByUsername(String username);
    
    Optional<Nurse> findByEmail(String email);

    // Find nurses whose name contains the provided value (case-insensitive)
    List<Nurse> findByNameContainingIgnoreCase(String name);
}