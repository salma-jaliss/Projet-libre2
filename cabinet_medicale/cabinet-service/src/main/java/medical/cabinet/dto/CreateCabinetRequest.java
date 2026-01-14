package medical.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête pour créer ou modifier un Cabinet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCabinetRequest {

    private String logo;

    @NotBlank
    @Size(max = 100)
    private String nom;

    @Size(max = 100)
    private String specialite;

    @Size(max = 255)
    private String adresse;

    @Size(max = 20)
    private String tel;

    @Size(max = 100)
    private String email;

    // Présent si on veut fixer l'état explicitement lors de la création/modification
    private Boolean actif;
}
