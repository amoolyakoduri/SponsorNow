package com.example.demo.service;

import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;
import com.example.demo.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PlayerService {

    @Resource
    PlayerRepository playRepository;

    @Resource
    SponsorService sponsorService;


    @Transactional(propagation = Propagation.REQUIRED)
    public Player addPlayer(Player player) {
        Player p = playRepository.saveAndFlush(player);
        return p;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addForeignKey(long sponsor_id,long player_id){
        playRepository.addForeignKey(sponsor_id,player_id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeForeignKey(long player_id){
        playRepository.removeForeignKey(player_id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Player> getPlayerByEmail(String email){
       List<Player> p = playRepository.getPlayerByEmail(email);
       return p;
    }

    /**
     * function to update player details with new details
     * @param id
     * @param email
     * @param firstname
     * @param lastname
     * @param description
     * @param street
     * @param city
     * @param state
     * @param zip
     * @param sponsor
     * @return updated object or empty
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Player> updatePlayer(Long id, String email, String firstname, String lastname, String description, String street,
                                         String city, String state, String zip,Sponsor sponsor) {
        Optional<Player> player = playRepository.findById(id);
        if(player.isPresent()) {
//            Optional<Sponsor> sponsor = sponsorService.getSponsorById(sponsor_id);
//            if(!sponsor.isPresent()){
//                throw new BadRequestException("Bad Request - Invalid Sponsor Id"+sponsor_id);
//            }
//            else
//                player.get().setSponsor(sponsor.get());
//            player.get().setSponsor(sponsor);
            player.get().setFirstname(firstname);
            player.get().setLastname(lastname);
            player.get().setDescription(description);
            Address address = new Address();
            if (player.get().getAddress() != null) {
                address = player.get().getAddress();
            }
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setStreet(street);
            player.get().setAddress(address);
            if (sponsor != null) {
                playRepository.addForeignKey(sponsor.getId(), id);
                player.get().setSponsor(sponsor);
            }
            else {
                playRepository.removeForeignKey(id);
                player.get().setSponsor(null);
            }
        }
        return player;
    }

    /**
     * function to delete player with given id
     * @param id
     * @return deleted object or empty
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Player> deletePlayer(Long id){
        Optional<Player> player = playRepository.findById(id);
        if(player.isPresent()){
            Player playerEntity = player.get();
            List<Player> opponents = new ArrayList<>();
            while(playerEntity.getOpponents().size()!=0) {
                opponents.add(playerEntity.getOpponents().get(0));
                playerEntity.removeOpponent(playerEntity.getOpponents().get(0));
            }
            playRepository.deleteById(playerEntity.getId());
            player.get().setOpponents(opponents);

        }
        return player;
    }

    /**
     * function to get player object by id
     * @param id
     * @return player object if exists or empty
     */
    public Optional<Player> findPlayerById(long id){
        Optional<Player> player = playRepository.findById(id);
        return player;
//          Player player = playRepository.getPlayerById(id);
//          return player;


    }

    /**
     * function to add opponent relation between id1 and id2
     * @param id1
     * @param id2
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addOpponent(Long id1,Long id2){
        playRepository.addOpponents(id1,id2);
    }

    /**
     * function to remove opponent relation between id1 and id2
     * @param id1
     * @param id2
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOpponent(Long id1,Long id2){
        playRepository.removeOpponents(id1,id2);
    }
}
