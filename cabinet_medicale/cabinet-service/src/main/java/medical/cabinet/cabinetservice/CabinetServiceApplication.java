package medical.cabinet.cabinetservice;

/**
 * Petit wrapper pour la configuration de lancement.
 * Certains run-configs cherchent la classe `medical.cabinet.cabinetservice.CabinetServiceApplication`.
 * Ce wrapper délègue l'exécution à la vraie classe définie dans `medical.cabinet.CabinetServiceApplication`.
 */
public class CabinetServiceApplication {
    public static void main(String[] args) {
        // Déléguer au vrai point d'entrée
        medical.cabinet.CabinetServiceApplication.main(args);
    }
}

