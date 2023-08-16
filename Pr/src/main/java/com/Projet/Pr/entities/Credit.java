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

public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer mantant;

    private String nbrEch;
    private String mensualite;
    @Temporal(TemporalType.DATE)
    private Date dateDemande;
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Client getClient() {
        return client;
    }


    public  void setClient(Client client) {
        this.client = client;
        client.setCredit(this);

    }

}


