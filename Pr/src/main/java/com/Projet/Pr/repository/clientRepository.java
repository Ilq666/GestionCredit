package com.Projet.Pr.repository;

import com.Projet.Pr.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface clientRepository  extends JpaRepository<Client,Long> {
    Page<Client> findByNomContains(String kw, Pageable pageable);


}
