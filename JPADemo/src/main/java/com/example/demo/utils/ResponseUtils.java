package com.example.demo.utils;

import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResponseUtils {

    /**
     * util function to get shallow form of player object
     * @param player
     * @return JSONObject player object
     */
    public JSONObject shallowFormPlayer(Player player){
        JSONObject playerShallow = new JSONObject();
        if(player!=null) {
            playerShallow.put("id", player.getId());
            playerShallow.put("firstname", player.getFirstname());
            playerShallow.put("lastname", player.getLastname());
            playerShallow.put("email", player.getEmail());
            playerShallow.put("description", player.getDescription() != null ? player.getDescription() : "");
        }
        return playerShallow;
    }

    /**
     * util function to get shallow form of sponsor object
     * @param sponsor
     * @return JSONObject sponsor object
     */
    public JSONObject shallowFormSponsor(Sponsor sponsor){
        JSONObject sponsorShallow = new JSONObject();
        if(sponsor!=null) {
            sponsorShallow.put("id",sponsor.getId());
            sponsorShallow.put("name", sponsor.getName());
            sponsorShallow.put("address", addressJson(sponsor.getAddress()));
            sponsorShallow.put("description", sponsor.getDescription() != null ? sponsor.getDescription() : "");
        }
        return sponsorShallow;
    }

    /**
     * util function to get JSONObject of address object
     * @param address
     * @return JSONObject address object
     */
    public JSONObject addressJson(Address address){
        JSONObject addressJson = new JSONObject();
        if(address!=null){
            addressJson.put("street",address.getStreet()!=null ? address.getStreet() : "");
            addressJson.put("city",address.getCity()!=null ? address.getCity() : "");
            addressJson.put("zip",address.getZip()!=null ? address.getZip() : "");
            addressJson.put("state",address.getState()!=null ? address.getState() : "");
        }
        else {
            addressJson.put("street","");
            addressJson.put("city","");
            addressJson.put("zip","");
            addressJson.put("state","");
        }
        return addressJson;
    }

    /**
     * util function to get deep form of player object
     * @param player
     * @return JSONObject player object
     */
    public JSONObject deepFormPlayer(Player player){
        JSONObject playerDeep = shallowFormPlayer(player);
        List<JSONObject> opponents = new ArrayList<>();
        for(Player p : player.getOpponents()){
            opponents.add(shallowFormPlayer(p));
        }
        playerDeep.put("sponsor",shallowFormSponsor(player.getSponsor()));
        playerDeep.put("address",addressJson(player.getAddress()));
        playerDeep.put("opponents",opponents);
        return playerDeep;
    }

    /**
     * util function to get deep form of player object
     * @param sponsor
     * @return JSONObject player object
     */
    public JSONObject deepFormSponsor(Sponsor sponsor){
        JSONObject sponsorDeep = shallowFormSponsor(sponsor);
        List<JSONObject> players = new ArrayList<>();
        for(Player p : sponsor.getPlayers()){
            players.add(shallowFormPlayer(p));
        }
        sponsorDeep.put("players",players);
        return sponsorDeep;
    }
}
