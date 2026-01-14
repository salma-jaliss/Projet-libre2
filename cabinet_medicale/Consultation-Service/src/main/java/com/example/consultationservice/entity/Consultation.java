package com.example.consultationservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_consultation")
    private String type;

    private LocalDateTime dateConsultation;

    private String examenClinique;

    private String examenSupplementaire;

    private String diagnostic;

    private String traitement;

    private String observations;

    private Long patientId;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private DossierMedical dossierMedical;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordonnance> ordonnances = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDateTime dateConsultation) { this.dateConsultation = dateConsultation; }
    public String getExamenClinique() { return examenClinique; }
    public void setExamenClinique(String examenClinique) { this.examenClinique = examenClinique; }
    public String getExamenSupplementaire() { return examenSupplementaire; }
    public void setExamenSupplementaire(String examenSupplementaire) { this.examenSupplementaire = examenSupplementaire; }
    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }
    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) { this.traitement = traitement; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public DossierMedical getDossierMedical() { return dossierMedical; }
    public void setDossierMedical(DossierMedical dossierMedical) { this.dossierMedical = dossierMedical; }
    public List<Ordonnance> getOrdonnances() { return ordonnances; }
    public void setOrdonnances(List<Ordonnance> ordonnances) { this.ordonnances = ordonnances; }
}