package com.example.demo.repository;

import com.example.demo.entity.Address;
import com.example.demo.entity.Player;
import com.example.demo.entity.Sponsor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@EntityScan(basePackages = {"com.example.demo.entity"})
public interface SponsorRepository extends JpaRepository<Sponsor,Long> {
    
	@Override
    List<Sponsor> findAll();
    
	@Override
    Optional<Sponsor> findById(Long s);
    
    @Override
    void deleteById(Long id);

    @Override
    void delete(Sponsor entity);

//  @Modifying
  @Query(value = "select * from sponsor where id=?1",nativeQuery = true)
  Optional<Sponsor> getSponsorById(@Param("id")long id);
  
  
//	void deleteById(long id);

}
