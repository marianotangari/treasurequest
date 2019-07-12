package com.codeoftheweb.salvo.repositories;


import com.codeoftheweb.salvo.models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long>{
    List<GamePlayer> findByJoinDate(LocalDateTime joinDate);
}