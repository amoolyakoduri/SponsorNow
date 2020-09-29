package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.entity.Sponsor;
import com.example.demo.repository.SponsorRepository;

import com.example.demo.utils.ResponseUtils;

import com.example.demo.service.SponsorService;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import net.sf.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sponsor")
public class SponsorController {

    @Resource
    private SponsorRepository sponsorRepository;

    @Resource
    ResponseUtils responseUtils;

    @Resource
    private SponsorService sponsorService;

    /**
     * API to get sponsor details by id
     * @param id
     * @return JSONObject containing Sponsor details
     */
    @RequestMapping(value={"/{id}"},method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional
    public JSONObject getSponsorById(@PathVariable("id") long id){
    	Optional<Sponsor> sponsor  = sponsorService.getSponsorById(id);
        Sponsor sponsorEntity;
        JSONObject returnData = new JSONObject();
        JSONObject payload = new JSONObject();

        if(sponsor.isPresent()){
            sponsorEntity = sponsor.get();
//            payload.put("name",sponsorEntity.getName());
//            payload.put("description",sponsorEntity.getDescription());
//            payload.put("address",sponsorEntity.getAddress());
//            payload.put("players",sponsorEntity.getPlayers());
            payload = responseUtils.deepFormSponsor(sponsorEntity);
            returnData.put("code",200);

        } else {
//            payload.put("mess","There is no data related to this id");
//            returnData.put("code",404);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sponsor not found with this id " + id
            );
          }
        returnData.put("payload",payload);
        return returnData;
    }

    /**
     * API to create sponsor with given details
     * @param req
     * @param res
     * @return JSONObject containing created Sponsor details
     */
    @RequestMapping(method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional
    public JSONObject createSponsor(HttpServletRequest req, HttpServletResponse res) {
        String name = "",street="",city="",zip = "",state="",description="";
        int sponsorId;
        int code = 200;
        Address address = new Address();
        JSONObject payload = new JSONObject();
        JSONObject returnData = new JSONObject();
        if(req.getParameter("name") != null && req.getParameter("name") != "") {
        	name = req.getParameter("name");

            if(req.getParameter("description") != null ) {
            description = req.getParameter("description");}            

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

            Sponsor sponsor = new Sponsor();
            sponsor.setName(name);
            sponsor.setAddress(address);
            sponsor.setDescription(description);
          
            Sponsor spon = sponsorService.addSponsor(sponsor);      
            payload = JSONObject.fromObject(spon) ;

            }
        else{
//            code = 400;
//            payload.put("mess","Name is null");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is null "
            );
        }

        returnData.put("code",code);
        returnData.put("payload",payload);

        return returnData;
    }

    /**
    * API to completely replace sponsor object with given id and updated details
    * @param id
    * @param name
    * @param description
    * @param street
    * @param city
    * @param state
    * @param zip
    * @return JSONObject containing updated sponsor details
    */
   @RequestMapping(value={"/{id}"},method = RequestMethod.PUT,produces = {"application/json;charset=utf-8"})
   @ResponseBody
   @Transactional
   public JSONObject updateSponsorDetails(@PathVariable("id") long id,
                                         @RequestParam(required = true) String name,
                                         @RequestParam(required = false) String description,
                                         @RequestParam(required = false) String street,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) String zip){
       if(name.trim().equals("") )
           throw new ResponseStatusException(
                   HttpStatus.BAD_REQUEST, "Name cannot be empty"
           );
       Optional<Sponsor> sponsorUpdated = sponsorService.updateSponsor(id,name,description,street,city,state,zip);
       if(!sponsorUpdated.isPresent()){
           throw new ResponseStatusException(
                   HttpStatus.NOT_FOUND, "Sponsor not found with id "+id
           );
       }
       JSONObject object = responseUtils.deepFormSponsor(sponsorUpdated.get());
       return object;
   }

   /**
    * API to delete sponsor object with given id
    * @param id
    * @return JSONObject containing deleted sponsor details
    *
    */
   @RequestMapping(value={"/{id}"},method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
   @ResponseBody
   @Transactional
   public JSONObject deleteSponsorDetails(@PathVariable("id") long id){
       Optional<Sponsor> sponsorDeleted = sponsorService.deleteSponsor(id);
       if(!sponsorDeleted.isPresent()){
           throw new ResponseStatusException(
                   HttpStatus.NOT_FOUND, "Sponsor not found with id "+id
           );
       }
       JSONObject object = responseUtils.deepFormSponsor(sponsorDeleted.get());
       return object;
   }

    
}
