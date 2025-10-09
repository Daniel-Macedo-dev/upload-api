package com.daniel.s3api.upload_api.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "user_table")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Print> prints = new ArrayList<>();


    public User() {}

    public User(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public User(String nome, String email, List<Print> prints) {
        this.nome = nome;
        this.email = email;
        this.prints = prints;
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getNome() {return nome;}

    public void setNome(String nome) {this.nome = nome;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public List<Print> getPrints() {return prints;}

    public void setPrints(List<Print> prints) {this.prints = prints;}

    public void addPrint(Print print){
        prints.add(print);
        print.setUser(this);
    }
    public void removePrint(Print print){
        prints.remove(print);
        print.setUser(null);
    }
}
