package com.Projet.Pr.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String demandeCredit;
    @NotEmpty
    @Size(min = 3, max=10)
    private String nom;
    private String prenom;
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
    private String cin;
    @Temporal(TemporalType.DATE)
    private Date dateDelevrance;
    private String gsm;
    private String teld;
    private String telp;
    private String adresse;
    private String situation_familiale;
    private int nbrEnfant ;
    private String habitation;
    private int status=1;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Profession profession;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Banque banque;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Credit credit;


    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                // Autres attributs sans inclure les relations bidirectionnelles
                '}';
    }


}
