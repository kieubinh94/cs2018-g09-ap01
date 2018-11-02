package cs2018.ap.api.repositories;

import cs2018.ap.api.entities.NamedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamedEntityRepository extends JpaRepository<NamedEntity, Long> {
}
