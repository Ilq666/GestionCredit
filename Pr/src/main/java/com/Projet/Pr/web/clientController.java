package com.Projet.Pr.web;
import com.Projet.Pr.entities.Credit;
import com.Projet.Pr.entities.Banque;
import com.Projet.Pr.entities.Profession;
import com.Projet.Pr.repository.clientRepository;
import com.Projet.Pr.repository.CreditRepository;
import com.Projet.Pr.repository.BanqueRepository;
import com.Projet.Pr.repository.ProfessionRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
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



    @GetMapping("/ilyas")
    public String ilyas(){
    return "ilyas";
    }

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "size",defaultValue = "20") int size,
                        @RequestParam(name = "keyword",defaultValue = "") String kw
    ){
        Page<Client> pageClient = clientRepository.findByNomContains(kw, PageRequest.of(page,size));
        model.addAttribute("listClient",pageClient.getContent());
        model.addAttribute("pages",new int[pageClient.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        return "client";
    }
    @GetMapping("/deleteClient")
    public String deleteClient(@RequestParam(name = "id") Long id, String keyword, int page){
        clientRepository.deleteById(id);
        //pour rediercter vers /index
        return "redirect:/index?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/formulaire")
    public String formulaire(Model model ){
        model.addAttribute("client",new Client());
        model.addAttribute("credit", new Credit());
        model.addAttribute("banque", new Banque());
        model.addAttribute("profession", new Profession());

        return "formulaire";
    }

    @PostMapping("/saveClient")
    public String saveClient(@Valid  Client client, @Valid  Credit credit, @Valid  Banque banque, @Valid  Profession profession,
                             BindingResult bindingResult){
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
    public String editClient(@RequestParam(name = "id") Long id, Model model){
        Client client=clientRepository.findById(id).get();


        model.addAttribute("client",client);




        return "editClient";
    }




}
