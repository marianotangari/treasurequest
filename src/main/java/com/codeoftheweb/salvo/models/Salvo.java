package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private int turn;

    @ElementCollection
    private List<String> location;

    //DTO

    public Map<String, Object> toDTO_salvoes(){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("player_id", this.getGamePlayer().getPlayer().getId());
        dto.put("Turn", this.getTurn());
        dto.put("Locations",this.getLocations());
        dto.put("hits", hitEnemyShips());
        dto.put("sunk_ships", sunkShips());
        return dto;
    }

    private List<String> hitEnemyShips(){
        List<String> hits = new LinkedList<>();
        Map<String, Object> hitsMap = new HashMap<>();
        if (this.getGamePlayer().enemyGamePlayer() != null){
           hits = this.getLocations().stream().filter(location ->this.getGamePlayer().enemyGamePlayer().getShips()
                   .stream().anyMatch(ship -> ship.getLocation().contains(location))).collect(Collectors.toList());
        }
        return hits;
    }

    public List<Object> sunkShips(){
        List<String> allSalvoes = new ArrayList<>();
                this.getGamePlayer().getSalvoes().stream().filter(salvo -> salvo.getTurn() <= this.getTurn())
                .forEach(salvo -> allSalvoes.addAll(salvo.getLocations()));
        List<Object> sunkShips = this.getGamePlayer().enemyGamePlayer().getShips().stream().filter(ship -> allSalvoes.containsAll(ship.getLocation()))
                .map(Ship::toDTO_ships).collect(Collectors.toList());
        return sunkShips;
    }
    //Constructors

    public Salvo(){}

    public Salvo(int turn, List<String> location){
        this.turn = turn;
        this.location = location;
    }


    //Getters and Setters

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getLocations() {
        return location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setLocations(List<String> locations) {
        this.location = locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
