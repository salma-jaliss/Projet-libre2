package ma.cabinet.auth.client;

import ma.cabinet.auth.model.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cabinet-service", url = "${application.config.cabinet-url:}") 
// using url placeholder for easier local testing if needed, but relying on Eureka name mostly
public interface CabinetServiceClient {

    @GetMapping("/api/users/by-login/{login}")
    UserDTO getUserByLogin(@PathVariable("login") String login);
}
