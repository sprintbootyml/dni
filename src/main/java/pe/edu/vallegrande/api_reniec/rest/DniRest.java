package pe.edu.vallegrande.api_reniec.rest;

import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.api_reniec.model.Dni;
import pe.edu.vallegrande.api_reniec.service.DniService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DniRest {

    private final DniService service;

    public DniRest(DniService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Dni> getAllIpQueries() {
        return service.getAll();
    }

    @GetMapping("/all")
    public Flux<Dni> getAllDniRecords() {
        return service.getAll();
    }

    @GetMapping("/dni/{dni}")
    public Mono<Dni> getDniInfo(@PathVariable String dni) {
        return service.getDniInfo(Long.valueOf(dni));
    }

    @GetMapping("/status")
    public Flux<Dni> getIpQueriesByStatus(@RequestParam String status) {
        return service.getByStatus(status);
    }

    @PutMapping("/restore/{id}")
    public Mono<String> restored(@PathVariable Long id) {
        return service.restoreDni(id);
    }

    @PutMapping("/update/{id}")
    public Mono<Dni> updateDni(@PathVariable Long id, @RequestParam String dni) {
        return service.updateDni(id, dni);
    }

    @PostMapping("/consultar")
    public Mono<Dni> consultDni(@RequestParam String dni) {
        return service.consultarYGuardarDni(dni);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteIp(@PathVariable Long id) {
        return service.deleteDni(id);
    }

    @DeleteMapping("/delete/fisical/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteFisical(id);
    }
}