package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Game{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private
    Set<Salvo> salvoes;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();



    private LocalDateTime createDate;

    public void addGamePlayer (GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore (Score score){
        score.setGame(this);
        scores.add(score);
    }

    public Game(){
    }

    public Game(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    //DTO

    public Map<String, Object> toDTO() {
        Map<String, Object> getGames = new LinkedHashMap<>();
        getGames.put("id", this.getId());
        getGames.put("created", this.getCreateDate());
        getGames.put("GamePlayers", this.getGamePlayers().stream().map(GamePlayer::toDto_players));
        getGames.put("Scores", this.getScores().stream().map(Score::getPoints));
        return getGames;
    }



    public Set<Score> getScores() { return scores; }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Salvo> salvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Set<Salvo> getSalvoes(){ return salvoes; }
}
