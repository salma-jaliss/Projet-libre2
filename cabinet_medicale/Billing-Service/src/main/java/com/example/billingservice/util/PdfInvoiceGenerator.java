package com.example.billingservice.util;

import com.example.billingservice.entity.Facture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class PdfInvoiceGenerator {

    @Value("${app.pdf.output.dir:./generated-invoices}")
    private String outputDir;

    @Value("${app.signature.image.path:./signature/signature_medecin.png}")
    private String signatureImagePath;

    public String generateInvoicePdf(Facture facture) {
        // Créer le dossier de sortie s'il n'existe pas
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = outputDir + "/facture_" + facture.getNumeroFacture() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ========================= TITRE =========================
            Paragraph title = new Paragraph("FACTURE")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Espace

            // ========================= INFOS FACTURE =========================
            document.add(new Paragraph("Numéro de facture : " + facture.getNumeroFacture()).setBold());
            document.add(new Paragraph("Date de facture : " + facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("Patient ID : " + facture.getPatientId()));
            if (facture.getConsultationId() != null) {
                document.add(new Paragraph("Consultation ID : " + facture.getConsultationId()));
            }

            document.add(new Paragraph(" "));

            // ========================= DÉTAIL DES PRESTATIONS =========================
            document.add(new Paragraph("Détail des prestations :").setBold().setFontSize(14));

            Table table = new Table(2).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Montant").setBold()));

            if (facture.getLignesDescription() != null && facture.getLignesMontant() != null) {
                for (int i = 0; i < facture.getLignesDescription().size(); i++) {
                    table.addCell(new Cell().add(new Paragraph(facture.getLignesDescription().get(i))));
                    table.addCell(new Cell().add(new Paragraph(facture.getLignesMontant().get(i) + " €")));
                }
            } else {
                table.addCell(new Cell(1, 2).add(new Paragraph("Aucune prestation enregistrée")));
            }

            document.add(table);

            document.add(new Paragraph(" "));

            // ========================= TOTAUX =========================
            Paragraph total = new Paragraph("Montant total : " + facture.getMontantTotal() + " €")
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);

            Paragraph paye = new Paragraph("Montant payé : " + facture.getMontantPaye() + " €")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(paye);

            Paragraph restant = new Paragraph("Montant restant : " + facture.getMontantRestant() + " €")
                    .setBold()
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(restant);

            Paragraph statut = new Paragraph("Statut : " + facture.getStatut())
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(statut);

            document.add(new Paragraph(" "));

            // ========================= MODE DE PAIEMENT =========================
            if (facture.getModePaiement() != null) {
                document.add(new Paragraph("Mode de paiement : " + facture.getModePaiement()));
            }

            // ========================= SIGNATURE =========================
            try {
                ImageData imageData = ImageDataFactory.create(signatureImagePath);
                Image signatureImage = new Image(imageData);
                signatureImage.scaleToFit(150, 100);
                signatureImage.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                document.add(signatureImage);

                document.add(new Paragraph("Signature du médecin")
                        .setItalic()
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT));
            } catch (Exception e) {
                document.add(new Paragraph("Signature électronique du médecin")
                        .setItalic()
                        .setTextAlignment(TextAlignment.RIGHT));
            }

            // ========================= FIN =========================
            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF de la facture", e);
        }

        return filePath;
    }
}