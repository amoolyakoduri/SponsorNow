package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="sponsor")
//@Data
public class Sponsor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @Getter
    @Setter
    private long id; // doc contains long type for id.

    @Column(name="name", nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(name="description")
    @Getter
    @Setter
    private String description;

    @Embedded
    @Getter
    @Setter
    private Address address;

    @OneToMany(mappedBy = "sponsor",fetch = FetchType.LAZY)
    @JsonIgnore
//    @JsonBackReference
    @JsonManagedReference
//    @JsonManagedReference("sponsor-player")
    @ToString.Exclude
    @Getter
    @Setter
    private List<Player> players;
    
	public void removePlayer(Player player) {
        this.players.remove(player);
        player.setSponsor(null);
    }
}
