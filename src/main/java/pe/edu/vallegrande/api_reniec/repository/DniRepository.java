package pe.edu.vallegrande.api_reniec.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.api_reniec.model.Dni;
import reactor.core.publisher.Flux;

@Repository
public interface DniRepository extends ReactiveCrudRepository<Dni, Long> {
    Flux<Dni> findByStatus(String status);
}