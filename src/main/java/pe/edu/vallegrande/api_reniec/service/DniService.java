package pe.edu.vallegrande.api_reniec.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.api_reniec.exception.DniServiceException;
import pe.edu.vallegrande.api_reniec.model.Dni;
import pe.edu.vallegrande.api_reniec.repository.DniRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DniService {
    private final DniRepository repository;
    private final WebClient webClient;

    @Getter
    private final String token;

    public DniService(WebClient.Builder webClientBuilder, DniRepository repository, @Value("${name.token}") String token) {
        this.repository = repository;
        this.token = token;
        this.webClient = webClientBuilder.baseUrl("https://dniruc.apisperu.com/api/v1").build();
    }

    // Método para consultar y guardar el DNI
    public Mono<Dni> consultarYGuardarDni(String dni) {
        String url = "/dni/" + dni + "?token=" + token;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    JSONObject json = new JSONObject(responseBody);

                    if (json.getBoolean("success")) {
                        Dni dniModel = new Dni();
                        dniModel.setDni(json.getString("dni"));
                        dniModel.setNombres(json.getString("nombres"));
                        dniModel.setApellidoPaterno(json.getString("apellidoPaterno"));
                        dniModel.setApellidoMaterno(json.getString("apellidoMaterno"));
                        dniModel.setCodVerifica(json.getString("codVerifica"));
                        dniModel.setStatus("A"); // Establecer el estado como Activo

                        return repository.save(dniModel);
                    } else {
                        return Mono.error(new DniServiceException("No se pudo obtener información del DNI."));
                    }
                })
                .doOnError(e -> log.error("Error al consultar el DNI: ", e));
    }

    // Método para obtener la información del DNI y guardarla
    public Mono<Dni> getDniInfo(Long dni) {
        String url = "/dni/" + dni + "?token=" + token;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Dni.class)
                .flatMap(dniInfo -> {
                    dniInfo.setDni(String.valueOf(dni)); // Ajustar el campo `dni`
                    return repository.save(dniInfo);
                })
                .doOnError(e -> log.error("Error while fetching DNI info", e));
    }

    public Mono<Dni> updateDni(Long id, String dni) {
        return repository.findById(id)
                .flatMap(existingDni -> {
                    if (existingDni == null) {
                        // Si no se encuentra el DNI, devuelve un Mono vacío o un error, en lugar de un mensaje de texto.
                        return Mono.error(new RuntimeException("DNI no encontrado."));
                    }

                    String url = "/dni/" + dni + "?token=" + token;
                    System.out.println("Consultando URL: " + url); // Log para depurar

                    return webClient.get()
                            .uri(url)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(responseBody -> {
                                JSONObject json = new JSONObject(responseBody);

                                if (json.getBoolean("success")) {
                                    // Si el DNI es válido, actualiza los datos del objeto existingDni.
                                    existingDni.setDni(json.getString("dni"));
                                    existingDni.setNombres(json.getString("nombres"));
                                    existingDni.setApellidoPaterno(json.getString("apellidoPaterno"));
                                    existingDni.setApellidoMaterno(json.getString("apellidoMaterno"));
                                    existingDni.setCodVerifica(json.getString("codVerifica"));

                                    // Guarda los cambios y devuelve el objeto Dni actualizado.
                                    return repository.save(existingDni)
                                            .flatMap(updatedDni -> Mono.just(updatedDni));
                                } else {
                                    // Si el DNI no es válido, lanza un error con un mensaje adecuado.
                                    return Mono.error(new RuntimeException("DNI no válido."));
                                }
                            })
                            .onErrorResume(error -> {
                                // Maneja cualquier error en la llamada externa y lo transforma en un Mono de error.
                                return Mono.error(new RuntimeException("Error al consultar el servicio externo.", error));
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("DNI no encontrado.")));
    }


    // Método para obtener todos los DNIs con un estado específico
    public Flux<Dni> getByStatus(String status) {
        return repository.findByStatus(status);
    }

    // Método para obtener todos los DNIs
    public Flux<Dni> getAll() {
        return repository.findAll();
    }

    // Método para eliminar lógicamente un DNI
    public Mono<String> deleteDni(Long id) {
        return repository.findById(id)
                .flatMap(existingDni -> {
                    existingDni.setStatus("I");
                    return repository.save(existingDni)
                            .then(Mono.just("DNI eliminada lógicamente con éxito: " + existingDni.getDni()));
                })
                .switchIfEmpty(Mono.just("DNI no encontrada."));
    }

    // Método para eliminar físicamente un DNI
    public Mono<Void> deleteFisical(Long id) {
        return repository.findById(id)
                .flatMap(existingDni -> repository.delete(existingDni));
    }

    // Método para restaurar un DNI eliminado lógicamente
    public Mono<String> restoreDni(Long id) {
        return repository.findById(id)
                .flatMap(existingDni -> {
                    existingDni.setStatus("A");
                    return repository.save(existingDni)
                            .then(Mono.just("DNI restaurada con éxito: " + existingDni.getDni()));
                })
                .switchIfEmpty(Mono.just("DNI no encontrada."));
    }
}
