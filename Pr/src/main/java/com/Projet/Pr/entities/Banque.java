package com.Projet.Pr.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nrib;
    private String banqueN;
    private String telAgence;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


    public Client getClient() {
        return client;
    }


    public  void setClient(Client client) {
        this.client = client;
        client.setBanque(this);
    }
}
