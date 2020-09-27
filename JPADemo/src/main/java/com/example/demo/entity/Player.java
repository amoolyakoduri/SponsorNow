package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Entity
@Table(name="player")
//@Data
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true)
    @Getter
    @Setter
    private long id; // doc contains long type for id.


    @Column(name="firstname", nullable = false)
    @Getter
    @Setter
    private String firstname;

    @Column(name="lastname", nullable = false)
    @Getter
    @Setter
    private String lastname;

    @Column(name="email", unique = true)
    @Getter
    @Setter
    private String email;

    @Column(name="description")
    @Getter
    @Setter
    private String description;

    @Embedded
    @Getter
    @Setter
    private Address address;


//    @Column(name="sponsor_id",nullable = true)
//    @Getter
//    @Setter
//    private long sponsor_id;

    @ManyToMany
    @JoinTable(
            name = "player_opponent",
            joinColumns = {@JoinColumn(name = "playerID1",referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "playerID2",referencedColumnName = "id")}
          )
    @Getter
    @Setter
    private List<Player> opponents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id",insertable = false,updatable = false,nullable = true)
//            foreignKey = @ForeignKey(name = "player_sponsor_id_fk",value = ConstraintMode.NO_CONSTRAINT))
    @Getter
    @Setter
//    @JsonManagedReference("sponsor-player")
    @ToString.Exclude
    @JsonIgnore
    @JsonBackReference
    private Sponsor sponsor;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(firstname, player.firstname) &&
                Objects.equals(lastname, player.lastname) &&
                Objects.equals(email, player.email) &&
                Objects.equals(description, player.description) &&
                Objects.equals(address, player.address) &&
                Objects.equals(opponents, player.opponents) &&
                Objects.equals(sponsor, player.sponsor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, description, address, opponents, sponsor);
    }

    public void addOpponent(Player player) {
        this.opponents.add(player);
        player.getOpponents().add(this);
    }

    public void removeOpponent(Player player) {
        this.opponents.remove(player);
        player.getOpponents().remove(this);
    }

}
