package ma.cabinet.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String login;
    private String pwd;
    private String nom;
    private String prenom;
    private String role; // "MEDECIN", "SECRETAIRE", "ADMINISTRATEUR"
    private Long cabinetId;
}
