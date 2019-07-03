package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Player{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    private String userName;

    private String password;


//DTOS

    public Map<String, Object> toDto_player_scores(){
        Map<String, Object> dto_score = new HashMap<>();
        dto_score.put( "Player Id", this.getId());
        dto_score.put( "Username", this.getUserName());
        dto_score.put( "Scores", this.getScores().stream().map(Score::getPoints).collect(Collectors.toList()));
        return dto_score;
    }

    public Map<String, Object> toDto_player(){
        Map<String, Object> dto = new HashMap<>();
        dto.put( "Player_id", this.getId());
        dto.put( "Username", this.getUserName());
        return dto;
    }

    //Constructors

    public Player(){
    }

    public Player(String name, String password ) {
        this.userName = name;
        this.password = password;
    }



    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;


    public Score getScorebyGame(Game game){
        return this.scores.stream().filter(x -> x.getGame().getId() == game.getId()).findAny().orElse(null);
    }


    public void addGamePlayer (GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore (Score score){
        score.setPlayer(this);
        scores.add(score);
    }




    //Getters & Setters

    public void setScores(Set<Score> scores){ this.scores = scores;}

    public Set<Score> getScores(){ return scores; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

}
