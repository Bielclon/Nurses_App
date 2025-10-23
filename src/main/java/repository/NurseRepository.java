package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.Nurse;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {
	
}