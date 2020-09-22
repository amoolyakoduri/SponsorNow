package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;

import com.example.demo.repository.SponsorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SponsorService {

    @Resource
    private SponsorRepository sponsorRepository;

    @Resource
    PlayerService playerService;

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Sponsor> getSponsorById(long id){
       Optional<Sponsor> sponsor = sponsorRepository.getSponsorById(id);
       return sponsor;
    }

    /**
     * function to save sponsor
     * @param sponsor
     * @return sponsor object
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Sponsor addSponsor(Sponsor sponsor) {
        Sponsor s = sponsorRepository.saveAndFlush(sponsor);
        return s;
    }
    
    /**
     * function to update player details with new details
     * @param id
     * @param name
     * @param description
     * @param street
     * @param city
     * @param state
     * @param zip
     * @return updated object or empty
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Sponsor> updateSponsor(Long id, String name, String description, String street,
                                         String city, String state, String zip) {
        Optional<Sponsor> sponsor = sponsorRepository.findById(id);
        if(sponsor.isPresent()){
            sponsor.get().setName(name);
            sponsor.get().setDescription(description);
            Address address = new Address();
            if(sponsor.get().getAddress()!=null){
                address = sponsor.get().getAddress();
            }
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setStreet(street);
            sponsor.get().setAddress(address);
        }
        return sponsor;
    }
    
    /**
    * function to delete player with given id
    * @param id
    * @return deleted object or empty
    */
   @Transactional(propagation = Propagation.REQUIRED)
   public Optional<Sponsor> deleteSponsor(Long id){
	   Optional<Sponsor> sponsor = sponsorRepository.findById(id);
       if(sponsor.isPresent()){
    	   Sponsor sponsorEntity = sponsor.get();
           List<Player> players = new ArrayList<>();
           while(sponsorEntity.getPlayers().size()!=0) {
        	   players.add((sponsorEntity.getPlayers()).get(0));
               playerService.removeForeignKey(sponsorEntity.getPlayers().get(0).getId());
        	   sponsorEntity.removePlayer(sponsorEntity.getPlayers().get(0));
           }
           sponsorRepository.deleteById(sponsorEntity.getId());
           sponsor.get().setPlayers(players);;
       }
       return sponsor;
   }
}
