package com.Projet.Pr;

import com.Projet.Pr.entities.Client;
import com.Projet.Pr.repository.clientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PrApplication implements CommandLineRunner{

	@Autowired
	private clientRepository ClientRepository;
	public static void main(String[] args) {

		SpringApplication.run(PrApplication.class, args);
	}



	@Override

	public void run(String... args) throws Exception {



		//ClientRepository.save(new client(null,"PPR","QAFFOU","Ilyas",new Date(),
		//		"IC1223", new Date(),"0611223344","0511223344","0611223344",
		//		"hay el ouahda","marie",3,"locataire"));
		//ClientRepository.save(new client(null,"regroupement","MAZZEN","Ilias", new Date(),
		//		"IC111123",new Date(),"0644443344","0511278344","0623987654",
		//		"hay el oUZOUD","veuf",2,"Proprietaire"));
		//ClientRepository.save(new client(null,"regroupement","Boutakiot","aymane", new Date(),
		//		"IC111123",new Date(),"0644443344","0511278344","0623987654",
		//		"hay el oUZOUD","celebataire",5,"Proprietaire"));
		//ClientRepository.save(new client(null,"mazzen","ilias","I1233","ilias@gmail.com","222222"));
		//ClientRepository.save(new client(null,"boutakiout","aymane","IC1233","aymane@gmail.com","111111"));

		List<Client> Clients =ClientRepository.findAll();
		Clients.forEach(c->{
		System.out.println(c.toString());
		});
	}




}
