package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();





    //Constructors

    public GamePlayer(){
    }

    public GamePlayer(Game game, Player player,  LocalDateTime joinDate, Set<Ship> ships, Set<Salvo> salvoes){
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
        salvoes.forEach(this::addSalvo);
        ships.forEach(this::addShip);
    }

    public GamePlayer(Game game, Player player,  LocalDateTime joinDate, Set<Ship> ships){
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
        ships.forEach(this::addShip);
    }

    public GamePlayer(Game game, Player player,  LocalDateTime joinDate){
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

    public void addShip (Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Salvo  getSunkShips(Salvo salvo){
        Salvo salvoes = this.enemyGamePlayer().getSalvoes().stream()
                .filter(x ->  x.getTurn() < salvo.getTurn()).max(Comparator.comparing(Salvo::getTurn)).get();
        return  salvoes;
    }

    //DTOs

    public Map<String, Object> toDTO_gamePlayers() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getGame().getId());
        dto.put("creation date", this.getGame().getCreateDate());
        dto.put("GamePlayers", this.getGame().getGamePlayers().stream().map(GamePlayer::toDto_players));
        dto.put("Ships", this.getShips().stream().map(Ship::toDTO_ships));
        dto.put("Salvoes", this.game.getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().map(Salvo::toDTO_salvoes)));
        return dto;
    }


    public Map<String, Object> toDto_players() {
        Map<String, Object> dtoPlayer = new LinkedHashMap<>();
        dtoPlayer.put("gpid", this.getId());
        dtoPlayer.put("id", this.getPlayer().getId());
        dtoPlayer.put("username", this.getPlayer().getUserName());
        if(this.getScore() != null)
            dtoPlayer.put("score", this.getScore().getPoints());
        else
            dtoPlayer.put("score",this.getScore());
        return dtoPlayer;
    }

    public GamePlayer enemyGamePlayer(){
        GamePlayer enemy = this.getGame().getGamePlayers().stream()
                .filter(gamePlayer -> gamePlayer.getId() != this.getId()).findFirst().orElse(null);
        return enemy;
    }
    //Getters & Setters

    public void setShips(Set<Ship> ships) { this.ships = ships; }

    public void setSalvoes(Set<Salvo> salvoes) { this.salvoes = salvoes; }

    public Score getScore(){
        return this.player.getScorebyGame(this.game);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Ship> getShips(){
        return ships;
    }

    public Set<Salvo> getSalvoes(){return salvoes;}
}
