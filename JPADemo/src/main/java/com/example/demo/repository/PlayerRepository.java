package com.example.demo.repository;

import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@EntityScan(basePackages = {"com.example.demo.entity"})
public interface PlayerRepository extends JpaRepository<Player,Long> {
    @Override
    List<Player> findAll();
    @Override
    Optional<Player> findById(Long s);

    @Override
    void deleteById(Long id);

    @Override
    void delete(Player entity);

    @Transactional
    @Modifying
    @Query(value = "update player set sponsor_id=?1 where id=?2 ",nativeQuery = true)
    void addForeignKey(long sponsor_id,long player_id);

    @Transactional
    @Modifying
    @Query(value = "update player set sponsor_id=null where id=?1 ",nativeQuery = true)
    void removeForeignKey(long player_id);

    @Query(value = "select * from player where email=?1 ",nativeQuery = true)
    List<Player> getPlayerByEmail(String email);

//    @Query(value = "select * from player where id=?1 ",nativeQuery = true)
//    Player getPlayerById(long id);
    @Transactional
    @Modifying
    @Query(value = "insert into player_opponent(playerid1,playerid2) values(?1,?2) ",nativeQuery = true)
    void addOpponents(long id1,long id2);

    @Transactional
    @Modifying
    @Query(value = "delete from player_opponent where playerid1=?1 and playerid2=?2",nativeQuery = true)
    void removeOpponents(long id1,long id2);
}

