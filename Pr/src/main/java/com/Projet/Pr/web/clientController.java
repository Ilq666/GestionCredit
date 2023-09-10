package com.Projet.Pr.web;
import com.Projet.Pr.entities.Credit;
import com.Projet.Pr.entities.Banque;
import com.Projet.Pr.entities.Profession;
import com.Projet.Pr.repository.clientRepository;
import com.Projet.Pr.repository.CreditRepository;
import com.Projet.Pr.repository.BanqueRepository;
import com.Projet.Pr.repository.ProfessionRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import com.Projet.Pr.entities.Client;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.thymeleaf.TemplateEngine;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import org.thymeleaf.context.Context;

import org.thymeleaf.TemplateEngine;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;




@Controller
@AllArgsConstructor
public class clientController {
    @Autowired
    private clientRepository clientRepository;
    @Autowired
    private ProfessionRepository ProfessionRepository;
    @Autowired
    private BanqueRepository BanqueRepository;
    @Autowired
    private CreditRepository CreditRepository;

    private final TemplateEngine templateEngine;


    @GetMapping("/")
    @PreAuthorize("hasRole('USER')")
    public String home() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {
        Page<Client> pageClient = clientRepository.findByNomContains(kw, PageRequest.of(page, size));
        model.addAttribute("listClient", pageClient.getContent());
        model.addAttribute("pages", new int[pageClient.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
        return "client";
    }

    @GetMapping("/deleteClient")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteClient(@RequestParam(name = "id") Long id, String keyword, int page) {
        clientRepository.deleteById(id);
        //pour rediercter vers /index
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/formulaire")
    public String formulaire(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("credit", new Credit());
        model.addAttribute("banque", new Banque());
        model.addAttribute("profession", new Profession());

        return "formulaire";
    }

    @PostMapping("/saveClient")
    public String saveClient(@Valid Client client, @Valid Credit credit, @Valid Banque banque, @Valid Profession profession,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "formulaire";
        }
        clientRepository.save(client);


        profession.setClient(client);
        ProfessionRepository.save(profession);


        banque.setClient(client);
        BanqueRepository.save(banque);


        credit.setClient(client);
        CreditRepository.save(credit);


        return "formulaire";
    }

    @GetMapping("/generate-pdf/{clientId}")
    public void generatePDF(@PathVariable Long clientId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=client_data.pdf");

        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("client", client);


            // Process Thymeleaf template
            String processedHtml = templateEngine.process("pdf", context); // Use the TemplateEngine


            // Generate PDF from processed HTML
            try (OutputStream outputStream = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withHtmlContent(processedHtml, "/");
                builder.toStream(outputStream);
                builder.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ... (use OpenHTMLToPDF or other PDF generation library)
        }
    }


    @GetMapping("/editClient")
    public String editClient(@RequestParam(name = "id") Long id, Model model) {
        Client client = clientRepository.findById(id).get();
        model.addAttribute("client", client);
        return "editClient";

    }

    @PostMapping("/saveEditedClient")
    public String saveEditedClient(@ModelAttribute("client") Client editedClient) {
        // Chargez le client existant depuis la base de données
        Client existingClient = clientRepository.findById(editedClient.getId()).orElse(null);

        if (existingClient != null) {
            // Mettez à jour tous les attributs du client avec les données du formulaire
            existingClient.setNom(editedClient.getNom());
            existingClient.setPrenom(editedClient.getPrenom());
            existingClient.setDateNaissance(editedClient.getDateNaissance());
            existingClient.setCin(editedClient.getCin());
            existingClient.setDateDelevrance(editedClient.getDateDelevrance());
            existingClient.setGsm(editedClient.getGsm());
            existingClient.setTeld(editedClient.getTeld());
            existingClient.setTelp(editedClient.getTelp());
            existingClient.setAdresse(editedClient.getAdresse());
            existingClient.setSituation_familiale(editedClient.getSituation_familiale());
            existingClient.setNbrEnfant(editedClient.getNbrEnfant());
            existingClient.setHabitation(editedClient.getHabitation());

            // Mettez à jour d'autres attributs du client

            // Récupérez la profession associée au client
            Profession existingProfession = existingClient.getProfession();
            if (existingProfession != null) {
                // Mettez à jour tous les attributs de la profession avec les données du formulaire
                existingProfession.setNomE(editedClient.getProfession().getNomE());
                existingProfession.setFonction(editedClient.getProfession().getFonction());
                existingProfession.setMatricule(editedClient.getProfession().getMatricule());
                existingProfession.setDateEntrer(editedClient.getProfession().getDateEntrer());
                // Mettez à jour d'autres attributs de la profession

                // Enregistrez les changements de la profession dans la base de données
                ProfessionRepository.save(existingProfession);
            }
            Banque existingBanque = existingClient.getBanque();
            if (existingBanque != null) {
                // Mettez à jour tous les attributs de la banque avec les données du formulaire
                existingBanque.setNrib(editedClient.getBanque().getNrib());
                existingBanque.setBanqueN(editedClient.getBanque().getBanqueN());
                existingBanque.setTelAgence(editedClient.getBanque().getTelAgence());
                // Mettez à jour d'autres attributs de la banque

                // Enregistrez les changements de la banque dans la base de données
                BanqueRepository.save(existingBanque);
            }

            Credit existingCredit = existingClient.getCredit();
            if (existingCredit != null) {
                // Mettez à jour tous les attributs du crédit avec les données du formulaire
                existingCredit.setMantant(editedClient.getCredit().getMantant());
                existingCredit.setNbrEch(editedClient.getCredit().getNbrEch());
                existingCredit.setMensualite(editedClient.getCredit().getMensualite());
                existingCredit.setDateDemande(editedClient.getCredit().getDateDemande());
                // Mettez à jour d'autres attributs du crédit

                // Enregistrez les changements du crédit dans la base de données
                CreditRepository.save(existingCredit);
            }

            // Enregistrez les changements du client dans la base de données
            clientRepository.save(existingClient);
        }

        return "editClient"; // Redirige vers la liste des clients (ou une autre page)
    }


    @GetMapping("/pdf-Engagement/{clientId}")
    public void EngagementPDF(@PathVariable Long clientId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=client_data.pdf");

        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("client", client);


            // Process Thymeleaf template
            String processedHtml = templateEngine.process("engagement", context); // Use the TemplateEngine


            // Generate PDF from processed HTML
            try (OutputStream outputStream = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withHtmlContent(processedHtml, "/");
                builder.toStream(outputStream);
                builder.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ... (use OpenHTMLToPDF or other PDF generation library)
        }
    }

    @GetMapping("/excel")
    public void generateExcel(HttpServletResponse response) throws IOException {
        // Créez un nouveau classeur Excel au format XLSX
        Workbook workbook = new XSSFWorkbook();

        // Créez une feuille dans le classeur
        Sheet sheet = workbook.createSheet("Clients");

        // Créez une ligne (ligne d'en-tête)
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Demande de crédit");
        headerRow.createCell(2).setCellValue("Nom");
        headerRow.createCell(3).setCellValue("Prénom");
        headerRow.createCell(4).setCellValue("Date de naissance");
        headerRow.createCell(5).setCellValue("CIN");
        headerRow.createCell(6).setCellValue("Date de délivrance");
        headerRow.createCell(7).setCellValue("GSM");
        headerRow.createCell(8).setCellValue("Tél. domicile");
        headerRow.createCell(9).setCellValue("Tél. professionnel");
        headerRow.createCell(10).setCellValue("Adresse");
        headerRow.createCell(11).setCellValue("Situation familiale");
        headerRow.createCell(12).setCellValue("Nombre d'enfants");
        headerRow.createCell(13).setCellValue("Habitation");

        headerRow.createCell(14).setCellValue("NRIB");
        headerRow.createCell(15).setCellValue("Banque");
        headerRow.createCell(16).setCellValue("Tél. Agence");

        headerRow.createCell(17).setCellValue("Montant");
        headerRow.createCell(18).setCellValue("Nombre d'échéances");
        headerRow.createCell(19).setCellValue("Mensualité");
        headerRow.createCell(20).setCellValue("Date de demande");

        headerRow.createCell(21).setCellValue("Nom de l'entreprise");
        headerRow.createCell(22).setCellValue("Fonction");
        headerRow.createCell(23).setCellValue("Matricule");
        headerRow.createCell(24).setCellValue("Date d'entrée");


        // Récupérez la liste des clients depuis la base de données
        List<Client> clients = clientRepository.findAll();

        // Remplissez les données des clients dans les lignes suivantes
        int rowNum = 1;
        for (Client client : clients) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(client.getId());
            row.createCell(1).setCellValue(client.getDemandeCredit());
            row.createCell(2).setCellValue(client.getNom());
            row.createCell(3).setCellValue(client.getPrenom());
            row.createCell(4).setCellValue(client.getDateNaissance().toString());
            row.createCell(5).setCellValue(client.getCin());
            row.createCell(6).setCellValue(client.getDateDelevrance().toString());
            row.createCell(7).setCellValue(client.getGsm());
            row.createCell(8).setCellValue(client.getTeld());
            row.createCell(9).setCellValue(client.getTelp());
            row.createCell(10).setCellValue(client.getAdresse());
            row.createCell(11).setCellValue(client.getSituation_familiale());
            row.createCell(12).setCellValue(client.getNbrEnfant());
            row.createCell(13).setCellValue(client.getHabitation());

            if (client.getBanque() != null) {
                row.createCell(14).setCellValue(client.getBanque().getNrib());
                row.createCell(15).setCellValue(client.getBanque().getBanqueN());
                row.createCell(16).setCellValue(client.getBanque().getTelAgence());
            }

            if (client.getCredit() != null) {
                row.createCell(17).setCellValue(client.getCredit().getMantant());
                row.createCell(18).setCellValue(client.getCredit().getNbrEch());
                row.createCell(19).setCellValue(client.getCredit().getMensualite());
                row.createCell(20).setCellValue(client.getCredit().getDateDemande().toString());
            }

            if (client.getProfession() != null) {
                row.createCell(21).setCellValue(client.getProfession().getNomE());
                row.createCell(22).setCellValue(client.getProfession().getFonction());
                row.createCell(23).setCellValue(client.getProfession().getMatricule());
                row.createCell(24).setCellValue(client.getProfession().getDateEntrer().toString());
            }


        }

        // Préparez la réponse HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=clients.xlsx");

        // Écrivez le classeur dans la réponse HTTP
        workbook.write(response.getOutputStream());

        // Fermez le classeur Excel
        workbook.close();
    }

    @PostMapping("/importExcel")
    public String importExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Veuillez sélectionner un fichier.");
            return "redirect:/index";
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Supposons que le fichier Excel a une seule feuille de calcul

            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();

                if (currentRow.getRowNum() == 1) {
                    // Ignore la première ligne (en-têtes)
                    continue;
                }

                // Créer un nouvel objet Client à partir des données de la ligne
                Client client = new Client();
                client.setDemandeCredit(currentRow.getCell(0).getStringCellValue());
                client.setNom(currentRow.getCell(1).getStringCellValue());
                client.setPrenom(currentRow.getCell(2).getStringCellValue());
                client.setDateNaissance(currentRow.getCell(3).getDateCellValue());
                client.setCin(currentRow.getCell(4).getStringCellValue());
                client.setDateDelevrance(currentRow.getCell(5).getDateCellValue());
                client.setGsm(currentRow.getCell(6).getStringCellValue());
                client.setTeld(currentRow.getCell(7).getStringCellValue());
                client.setTelp(currentRow.getCell(8).getStringCellValue());
                client.setAdresse(currentRow.getCell(9).getStringCellValue());
                client.setSituation_familiale(currentRow.getCell(10).getStringCellValue());
                client.setNbrEnfant((int) currentRow.getCell(11).getNumericCellValue());
                client.setHabitation(currentRow.getCell(12  ).getStringCellValue());
                // Sauvegarder le Client dans la base de données
                clientRepository.save(client);

               /* Banque banque = new Banque();

                // Extraire les données de la feuille Excel
                banque.setNrib(currentRow.getCell(13).getStringCellValue());
                banque.setBanqueN(currentRow.getCell(14).getStringCellValue());
                banque.setTelAgence(currentRow.getCell(15).getStringCellValue());
                BanqueRepository.save(banque);


                Credit credit = new Credit();

                // Extraire les données de la feuille Excel
                credit.setMantant((int) currentRow.getCell(16).getNumericCellValue());
                credit.setNbrEch(currentRow.getCell(17).getStringCellValue());
                credit.setMensualite(currentRow.getCell(18).getStringCellValue());
                // Si la dateDemande est stockée sous forme de date dans le fichier Excel
                Date dateDemande = currentRow.getCell(19).getDateCellValue();
                credit.setDateDemande(dateDemande);
                CreditRepository.save(credit);

                Profession profession = new Profession();
                profession.setNomE(currentRow.getCell(20).getStringCellValue());
                profession.setFonction(currentRow.getCell(21).getStringCellValue());
                profession.setMatricule(currentRow.getCell(22).getStringCellValue());
                Date dateEntrer = currentRow.getCell(23).getDateCellValue();
                profession.setDateEntrer(dateEntrer);
                ProfessionRepository.save(profession);*/

            }

            redirectAttributes.addFlashAttribute("message", "Importation réussie.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Une erreur s'est produite lors de l'importation.");
        }

        return "redirect:/index"; // Redirigez l'utilisateur vers la page d'accueil ou une autre page appropriée.
    }

}