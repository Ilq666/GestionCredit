package com.Projet.Pr.security;

import jdk.jfr.Enabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource){
        JdbcUserDetailsManager jdbcUserDetailsManager=new JdbcUserDetailsManager(dataSource);
        return jdbcUserDetailsManager;
    }
    //executer au demmarage
    /*@Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
        String pwd=passwordEncoder.encode("123123");
        String pwd2=passwordEncoder.encode("admin");
        InMemoryUserDetailsManager inMemoryUserDetailsManager=new InMemoryUserDetailsManager(

                User.withUsername("ilyas").password(pwd).roles("USER").build(),
                User.withUsername("admin").password(pwd2).roles("USER","ADMIN").build()
        );
        return inMemoryUserDetailsManager;
    }*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //toute les requete necessite une authentificatiion
        httpSecurity.formLogin();
        //l action /delete autoriser pour le role ADMIN
        //httpSecurity.authorizeHttpRequests().requestMatchers("/deleteClient/**","/editClient/**").hasRole("ADMIN");
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        httpSecurity.exceptionHandling().accessDeniedPage("/notAuthorized");
        return httpSecurity.build();
    }
}
