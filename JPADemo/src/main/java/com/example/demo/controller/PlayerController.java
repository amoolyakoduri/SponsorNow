package com.example.demo.controller;


import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;
import com.example.demo.service.PlayerService;

import com.example.demo.utils.ResponseUtils;

import com.example.demo.service.SponsorService;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/player")
public class PlayerController {

    @Resource
    private PlayerService playerService;

    @Resource
    ResponseUtils responseUtils;

    @Resource
    private SponsorService sponsorService;

    /**
     * API to create Player
     * @param req
     * @param res
     * @return JSONObject containing created player details
     */
    @RequestMapping(method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional
    public JSONObject createPlayer(HttpServletRequest req, HttpServletResponse res) {
        String firstname = "",lastname = "",email="",street="",city="";
        int sponsorId;
        int code = 200;
        Address address = new Address();
        JSONObject payload = new JSONObject();
        JSONObject returnData = new JSONObject();
        JSONObject sponsor_json = new JSONObject();
        boolean flag = false;

        if(req.getParameter("opponents")!=null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Opponents not allowed in the parameters: "
            );
        }
        if(req.getParameter("firstname") != null && req.getParameter("lastname") != null && req.getParameter("email") != null) {
            firstname = req.getParameter("firstname");
            lastname = req.getParameter("lastname");
            email = req.getParameter("email");

            List<Player> players = playerService.getPlayerByEmail(email);
            if(players.size() == 0){

            Player player = new Player();
            player.setFirstname(firstname);
            player.setLastname(lastname);
            player.setEmail(email);

            if (req.getParameter("street") != "" && req.getParameter("street") != null) {
                street = req.getParameter("street");
                address.setStreet(street);
            }
            if (req.getParameter("city") != "" && req.getParameter("street") != null) {
                city = req.getParameter("city");
                address.setCity(city);
            }
            if (req.getParameter("state") != "" && req.getParameter("state") != null) {
                address.setState(req.getParameter("state"));
            }
            if (req.getParameter("zip") != "" && req.getParameter("zip") != null) {
                address.setZip(req.getParameter("zip"));
            }
            if (req.getParameter("description") != "" && req.getParameter("description") != null) {
                player.setDescription(req.getParameter("description"));
            }

            player.setAddress(address);

            if (req.getParameter("sponsor") != "" && req.getParameter("sponsor") != null) {
                sponsorId = Integer.parseInt(req.getParameter("sponsor"));

                Optional<Sponsor> sponsor  = sponsorService.getSponsorById(sponsorId);
                if(sponsor.isPresent()){
                    player.setSponsor(sponsor.get());
                    flag = true;
                    sponsor_json.put("id",sponsor.get().getId());
                    sponsor_json.put("description",sponsor.get().getDescription());
                    sponsor_json.put("address",sponsor.get().getAddress());
                    sponsor_json.put("name",sponsor.get().getName());
                }else {
                    flag = false;
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Invalid Sponsor Id : "+  req.getParameter("sponsor")
                    );
                }
            }
            Player player1 = playerService.addPlayer(player);

            if (flag) {
            long sponor_id = Long.parseLong(req.getParameter("sponsor"));
            playerService.addForeignKey(sponor_id,player1.getId());
            }
//                payload=  responseUtils.shallowFormPlayer(player1);
            JsonConfig jc = new JsonConfig();
            jc.setExcludes(new String[]{"sponsor","players"});
            jc.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            payload = JSONObject.fromObject(player1,jc) ;
            payload.put("sponsor",sponsor_json);
            }
            else{

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "this email duplicated: "+ email
                );

            }
        }else{
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "required parameters are not complete"
            );

        }

        returnData.put("code",code);
        returnData.put("payload",payload);

        return returnData;
    }

    /**
     * API to completely replace player object with given id and updated details
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
     * @param opponents
     * @return JSONObject containing updated player details
     */
    @Transactional
    @RequestMapping(value={"/{id}"},method = RequestMethod.PUT,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public JSONObject updatePlayerDetails(@PathVariable("id") long id,
                                          @RequestParam(required = true) String email,
                                          @RequestParam(required = true) String firstname,
                                          @RequestParam(required = true) String lastname,
                                          @RequestParam(required = false) String description,
                                          @RequestParam(required = false) String street,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) String state,
                                          @RequestParam(required = false) String zip,
                                          @RequestParam(required = false) Long sponsor,
                                          @RequestParam(required = false) String opponents){
//        Optional<Player> player = playerService.findPlayerById(id);
//        if(!player.isPresent()){
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Player not found with id "+id
//            );
//        }
        if(email.trim().equals("") || firstname.trim().equals("") || lastname.trim().equals(""))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email, firstname and lastname cannot be empty"
            );
        if(opponents!=null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Opponents not allowed in the parameters: "
            );
        }
        Sponsor sponsorEntity = null;
        if(sponsor!=null) {
            Optional<Sponsor> sponsorObj = sponsorService.getSponsorById(sponsor);
            if (!sponsorObj.isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid Sponsor Id: " + sponsor
                );
            } else {
                sponsorEntity = sponsorObj.get();
            }
        }
        Optional<Player> playerUpdated = playerService.updatePlayer(id,email.trim(),
                firstname.trim(),lastname.trim(),description,street,city,
                state,zip,sponsorEntity);
        if(!playerUpdated.isPresent()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found with id "+id
            );
        }
        JSONObject object = new JSONObject();
        object.put("code",200);
        object.put("payload",responseUtils.deepFormPlayer(playerUpdated.get()));
        return object;
    }

    /**
     * API to delete player object with given id
     * @param id
     * @return JSONObject containing deleted player details
     */
    @RequestMapping(value={"/{id}"},method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional
    public JSONObject deletePlayerDetails(@PathVariable("id") long id){
        Optional<Player> playerDeleted = playerService.deletePlayer(id);
        if(!playerDeleted.isPresent()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found with id "+id
            );
        }
        JSONObject object = responseUtils.deepFormPlayer(playerDeleted.get());
        return object;
    }

    /**
     * API to get player by id
     * @param id
     * @return JSONObject containing player details
     */
    @RequestMapping(value={"/{id}"},method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional
    public JSONObject getPlayerById(@PathVariable("id") long id){
        Optional<Player> player  = playerService.findPlayerById(id);
        Player playerEntity;
        JSONObject returnData = new JSONObject();
        JSONObject payload = new JSONObject();

        if(player.isPresent()){
            playerEntity = player.get();
            payload.put("id",playerEntity.getId());
            payload.put("firstname",playerEntity.getFirstname());
            payload.put("lastname",playerEntity.getLastname());
            payload.put("email",playerEntity.getEmail());

            if(playerEntity.getSponsor() != null){

            	Sponsor sponsor = playerEntity.getSponsor();
                JSONObject sponsorJson = new JSONObject();
                sponsorJson.put("id",sponsor.getId());
                if(sponsor.getName()!=null){
                    sponsorJson.put("name",sponsor.getName());
                }else{sponsorJson.put("name","");}

                if(sponsor.getAddress()!=null){
                    sponsorJson.put("address",sponsor.getAddress());
                }else{sponsorJson.put("address","");}

                if(sponsor.getDescription()!=null){
                    sponsorJson.put("description",sponsor.getDescription());
                }else{sponsorJson.put("description","");}


                payload.put("sponsor",sponsorJson);
            }else {
                payload.put("sponsor",new JSONObject());
            }

//            if(playerEntity.getAddress()!= null){
//                payload.put("address",playerEntity.getAddress());
//            }else{
                payload.put("address",responseUtils.addressJson(playerEntity.getAddress()));
//            }

            if(playerEntity.getDescription()!= null){
                payload.put("description",playerEntity.getDescription());
            }else{
                payload.put("description","");
            }

            if(playerEntity.getOpponents()!= null){
                List<Long> opponents_ids = new ArrayList<Long>();
                List<Player> opponents = playerEntity.getOpponents();
                for(Player item : opponents){
                    opponents_ids.add(item.getId());
                }
                payload.put("opponents",opponents_ids);
            }else{
                payload.put("opponents","");
            }

            returnData.put("code",200);

        } else {
            payload.put("mess","there is no data related to this id");
            returnData.put("code",404);
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Player not found with id "+id
                );
        }
        returnData.put("payload",payload);
        return returnData;
    }

    }
