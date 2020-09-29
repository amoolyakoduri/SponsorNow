package com.example.demo.controller;


import com.example.demo.entity.Player;
import com.example.demo.service.PlayerService;
import com.example.demo.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/opponents")
public class OpponentContrller {

    @Resource
    private PlayerService playerService;

    @Resource
    ResponseUtils responseUtils;

    /**
     * API to create opponents relationship
     * @param id1
     * @param id2
     */
    @RequestMapping(value={"/{id1}/{id2}"},method = RequestMethod.PUT,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public void addOpponent(@PathVariable("id1") long id1, @PathVariable("id2") long id2){
        Optional<Player> player1  = playerService.findPlayerById(id1);
        Optional<Player> player2  = playerService.findPlayerById(id2);
        if(player1.isPresent() && player2.isPresent()){
            Player playerEntity1 = player1.get();
            Player playerEntity2 = player2.get();
            List<Player> opponents = playerEntity1.getOpponents();
            boolean isOpponent = false;
            for(Player item : opponents){
                if(item.getId() == playerEntity2.getId()){
                    isOpponent = true;
                }
            }
            if(isOpponent){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, " opponents already exists"
                );
            }else{
//                playerEntity1.addOpponent(playerEntity2);
//                playerEntity2.addOpponent(playerEntity1);
                playerService.addOpponent(playerEntity1.getId(),playerEntity2.getId());
                playerService.addOpponent(playerEntity2.getId(),playerEntity1.getId());
                throw new ResponseStatusException(
                        HttpStatus.OK, "insert success"
                );
            }
        }else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found with one or two of those ids "
            );
        }
    }

    /**
     * API to remove opponents relationship
     * @param id1
     * @param id2
     */
    @RequestMapping(value={"/{id1}/{id2}"},method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOpponent(@PathVariable("id1") long id1, @PathVariable("id2") long id2){
        Optional<Player> player1  = playerService.findPlayerById(id1);
        Optional<Player> player2  = playerService.findPlayerById(id2);
        if(player1.isPresent() && player2.isPresent()){
            Player playerEntity1 = player1.get();
            Player playerEntity2 = player2.get();
            List<Player> opponents = playerEntity1.getOpponents();
            boolean isOpponent = false;
            for(Player item : opponents){
                if(item.getId() == playerEntity2.getId()){
                    isOpponent = true;
                }
            }
            if(isOpponent){

                playerService.removeOpponent(playerEntity1.getId(),playerEntity2.getId());
                playerService.removeOpponent(playerEntity2.getId(),playerEntity1.getId());
                throw new ResponseStatusException(
                        HttpStatus.OK, "delete success"
                );

            }else{
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, " Given players are not opponents"
                );
            }
        }else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Player not found with one or two of those ids "
            );
        }
    }

}
