package cs2018.ap.api.repository;

import cs2018.ap.api.entity.NamedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamedEntityRepository extends JpaRepository<NamedEntity, Long> {
}
