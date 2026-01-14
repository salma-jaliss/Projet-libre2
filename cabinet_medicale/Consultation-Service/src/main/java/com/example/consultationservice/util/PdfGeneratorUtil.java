package com.example.consultationservice.util;

import com.example.consultationservice.entity.LigneMedicament;
import com.example.consultationservice.entity.Ordonnance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGeneratorUtil {

    // Chemin configurable via application.properties (recommandé)
    @Value("${app.pdf.output.dir:./generated-pdfs}")
    private String outputDir;

    @Value("${app.signature.image.path:./signature/signature_medecin.png}")
    private String signatureImagePath;

    public String generateOrdonnancePdf(Ordonnance ordonnance) {
        // Créer le dossier de sortie s'il n'existe pas
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = outputDir + "/ordonnance_" + ordonnance.getId() + ".pdf";

        try {
            // 1. Créer le PdfWriter et PdfDocument (iText 7)
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 2. Titre principal
            Paragraph title = new Paragraph("ORDONNANCE MÉDICALE")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Espace

            // 3. Infos générales
            document.add(new Paragraph("Type d'ordonnance : " + ordonnance.getType()));
            document.add(new Paragraph("Date : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Consultation ID : " + ordonnance.getConsultation().getId()));

            document.add(new Paragraph(" "));

            // 4. Liste des médicaments
            document.add(new Paragraph("Médicaments prescrits :").setBold());

            if (ordonnance.getLignesMedicaments() != null && !ordonnance.getLignesMedicaments().isEmpty()) {
                for (LigneMedicament ligne : ordonnance.getLignesMedicaments()) {
                    Paragraph med = new Paragraph()
                            .add(new Text("• Médicament ID : " + ligne.getMedicamentId()))
                            .add(new Text(" | Posologie : " + ligne.getPosologie()))
                            .add(new Text(" | Quantité : " + ligne.getQuantite()))
                            .add(ligne.getInstructions() != null ? new Text(" | Instructions : " + ligne.getInstructions()) : new Text(""));
                    document.add(med);
                }
            } else {
                document.add(new Paragraph("Aucun médicament prescrit."));
            }

            document.add(new Paragraph(" "));

            // 5. Signature automatique (image)
            try {
                ImageData imageData = ImageDataFactory.create(signatureImagePath);
                Image signatureImage = new Image(imageData);
                signatureImage.scaleToFit(150, 100);
                signatureImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
                document.add(signatureImage);

                document.add(new Paragraph("Signature du médecin").setFontSize(10).setItalic()
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            } catch (Exception e) {
                // Si l'image n'existe pas, on met un texte simple
                document.add(new Paragraph("Signature électronique du médecin")
                        .setItalic()
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            }

            // 6. Fermer le document
            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de l'ordonnance", e);
        }

        return filePath;
    }
}