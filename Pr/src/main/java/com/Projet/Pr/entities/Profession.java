package com.Projet.Pr.entities;

import jakarta.persistence.*;
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

public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomE;
    private String fonction;
    private String matricule;
    @Temporal(TemporalType.DATE)
    private Date dateEntrer;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Client getClient() {
        return client;
    }


    public  void setClient(Client client) {
        this.client = client;
        client.setProfession(this);
    }
    @Override
    public String toString() {
        return "RenseignementProfessionels{" +
                "id=" + id +
                ", nom='" + nomE + '\'' +
                // Autres attributs sans inclure les relations bidirectionnelles
                '}';
    }




}
