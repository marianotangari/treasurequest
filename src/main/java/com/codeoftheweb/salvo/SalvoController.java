package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CollectionCertStoreParameters;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.Math.*;


@RestController
@RequestMapping("/api")
public class SalvoController {


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "GUEST");
        } else {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).toDto_player());
        }
        dto.put("games", gameRepository.findAll().stream().map(Game::toDTO).collect(Collectors.toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> IsPlayer(
            @RequestParam @RequestBody String username, @RequestParam String password) {
        ResponseEntity<Object> response;
        if (username.isEmpty() || password.isEmpty()) {
            response = new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(username) != null) {
            response =  new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        response = new ResponseEntity<>(makeMap("username", username), HttpStatus.CREATED);
        return response;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> CreateGame(Authentication authentication) {
        ResponseEntity<Object> response;
        if (isGuest(authentication))
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        else {
            Game game = new Game(LocalDateTime.now());
            gameRepository.save(game);
            Player currentPlayer = playerRepository.findByUserName(authentication.getName());
            GamePlayer gamePlayer = new GamePlayer(game, currentPlayer, LocalDateTime.now());
            gamePlayerRepository.save(gamePlayer);
            response =  new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(path = "/game/{game_id}/players", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> joinGame(@PathVariable Long game_id, Authentication authentication) {
        ResponseEntity<Object> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else {
            Optional<Game> OptionalGame = gameRepository.findById(game_id);
            Game game = new Game();
            if (OptionalGame.isPresent()) {
                game = OptionalGame.get();
                if (game.getGamePlayers().stream().count() > 1) {
                    response = new ResponseEntity<>("The game is already full", HttpStatus.FORBIDDEN);
                }
                else {
                    Player currentPlayer = playerRepository.findByUserName(authentication.getName());
                    GamePlayer gamePlayer = new GamePlayer(game, currentPlayer, LocalDateTime.now());
                    gamePlayerRepository.save(gamePlayer);
                    response = new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
                }
            }
            else {
                response = new ResponseEntity<>("No such Game", HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }


    @RequestMapping("/game_view/{game_id}")
    public ResponseEntity<Object> findById(@PathVariable Long game_id, Authentication authentication) {
        ResponseEntity<Object> response;
        Optional<GamePlayer> OptionalGamePlayer = gamePlayerRepository.findById(game_id);
        Player currentUser = playerRepository.findByUserName(authentication.getName());
        Map<String, Object> gamePlayerMap = new HashMap<>();

        GamePlayer gamePlayer = new GamePlayer();
        if (OptionalGamePlayer.isPresent()) {
            gamePlayer = OptionalGamePlayer.get();
        }
        if (gamePlayer.getPlayer().getId() != currentUser.getId())
            response = new ResponseEntity<>("Not Allowed", HttpStatus.UNAUTHORIZED);
        else {
            gamePlayerMap.put("game", gamePlayer.toDTO_gamePlayers());
            if (gamePlayer.enemyGamePlayer() != null) {

                /* Código de estados:
                    0 = game over, jugador 1 gana.
                    1 = game over, jugador 2 gana.
                    2 = jugador 2 esperar a que el jugador 1 ingrese salvos, ambos jugadores tienen salvos o el jugador 1 tiene salvos
                    y el jugador 2 no tiene.
                    3 = jugador 2 tiene ships pero el jugador 1 no tiene. Jugador 2 debe esperar.
                    4 = Ninguno de los jugadores tiene salvos ni ships. El jugador 1 debe ubicar sus ships.
                    5 = Ninguno de los jugadores tiene salvos ni ships, el jugador 2 debe esperar a que el 1 ubique sus ships.
                    6 = El jugador 1 tiene ships, el jugador 2 debe ingresar sus ships.
                    7 = Ambos jugadores tienen ships pero no hay salvos. El jugador 1 debe ingresar salvos.
                    8 = Ambos jugadores tienen ships pero no hay salvos. El jugador 2 debe esperar a que el jugador 1 ingrese salvos.
                    9 = Game Over, empate.
                    10 = jugador 1 tiene ships, no hay jugador 2.
                 */

                if (gamePlayer.getSalvoes().size() > 0 && gamePlayer.enemyGamePlayer().getSalvoes().size() > 0) {

                    //Empiezan las condiciones si ambos jugadores ya tienen salvos.

                    Salvo last_turn_salvoes = gamePlayer.getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
                    Salvo last_turn_salvoes_enemy = gamePlayer.enemyGamePlayer().getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
                    if (last_turn_salvoes.getTurn() == last_turn_salvoes_enemy.getTurn()) {
                        Game game = gamePlayer.getGame();
                        if (last_turn_salvoes.sunkShips().size() == gamePlayer.getShips().size()
                                && last_turn_salvoes_enemy.sunkShips().size() != gamePlayer.enemyGamePlayer().getShips().size()) {
                            gamePlayerMap.put("game_state", 0);
                            Score score = new Score(3, LocalDateTime.now(), game, gamePlayer.getPlayer());
                            game.addScore(score);
                            gameRepository.save(game);
                        }
                        else {
                            if (last_turn_salvoes.sunkShips().size() != gamePlayer.getShips().size()
                                    && last_turn_salvoes_enemy.sunkShips().size() == gamePlayer.enemyGamePlayer().getShips().size())
                            {
                                gamePlayerMap.put("game_state", 1);
                                Score score = new Score(0, LocalDateTime.now(), game, gamePlayer.getPlayer());
                                game.addScore(score);
                                gameRepository.save(game);
                            }
                            else {
                                if (last_turn_salvoes.sunkShips().size() == gamePlayer.getShips().size()
                                        && last_turn_salvoes_enemy.sunkShips().size() == gamePlayer.enemyGamePlayer().getShips().size()) {
                                    gamePlayerMap.put("game_state", 9);
                                    Score score = new Score(1, LocalDateTime.now(), game, gamePlayer.getPlayer());
                                    game.addScore(score);
                                    gameRepository.save(game);
                                }
                                else {
                                    if (gamePlayer.getId() < gamePlayer.enemyGamePlayer().getId())
                                        gamePlayerMap.put("game_state", 2);
                                    else {
                                        gamePlayerMap.put("game_state", 2);
                                    }
                                }
                            }
                        }
                    }
                    else{
                        if (gamePlayer.getId() < gamePlayer.enemyGamePlayer().getId())
                            gamePlayerMap.put("game_state", 2);
                        else {
                            gamePlayerMap.put("game_state", 2);
                        }
                    }
                }
                else{

                    // Empiezan las condiciones si solo uno de los jugadores tiene salvos.

                    if (gamePlayer.getSalvoes().size() > 0 && gamePlayer.enemyGamePlayer().getSalvoes().size() == 0)
                        gamePlayerMap.put("game_state", 2);
                    else{
                        if (gamePlayer.getSalvoes().size() == 0 && gamePlayer.enemyGamePlayer().getSalvoes().size() > 0)
                            gamePlayerMap.put("game_state", 2);
                        else{

                            //No hay salvos en ninguno de los dos jugadores, comienzan las condiciones para ubicar Ships.

                            if (gamePlayer.getShips().size() == 0 && gamePlayer.getId() < gamePlayer.enemyGamePlayer().getId())
                                gamePlayerMap.put("game_state", 4);
                            if (gamePlayer.getShips().size() == 0 && gamePlayer.getId() > gamePlayer.enemyGamePlayer().getId()) {
                                if (gamePlayer.enemyGamePlayer().getShips().size() > 0)
                                    gamePlayerMap.put("game_state", 5);
                                else
                                    gamePlayerMap.put("game_state", 5);
                            }
                            if (gamePlayer.getShips().size() > 0 && gamePlayer.enemyGamePlayer().getShips().size() == 0)
                                gamePlayerMap.put("game_state", 3);
                            if (gamePlayer.getShips().size() == 0 && gamePlayer.enemyGamePlayer().getShips().size() > 0)
                                gamePlayerMap.put("game_state", 6);
                            if (gamePlayer.getShips().size() > 0 && gamePlayer.enemyGamePlayer().getShips().size() > 0) {
                                    if (gamePlayer.getId() < gamePlayer.enemyGamePlayer().getId())
                                        gamePlayerMap.put("game_state", 7);
                                    else
                                        gamePlayerMap.put("game_state", 8);
                            }
                        }
                    }
                }
            }
            else{

                //Empiezan las condiciones si todavía no hay un contrincante en el juego.

                if (gamePlayer.getShips().size() == 0)
                    gamePlayerMap.put("game_state", 4);
                else{
                    gamePlayerMap.put("game_state", 10);
                    }
                }

            response = new ResponseEntity<>(gamePlayerMap, HttpStatus.OK);

        }
        return response;
    }

    @RequestMapping(path = "/games/players/{gamePlayer_id}/salvoes", method= RequestMethod.POST)
    public ResponseEntity<Object> placeSalvoes (@PathVariable Long gamePlayer_id, @RequestBody Salvo salvo,
                                                Authentication authentication){
           ResponseEntity<Object> response;
           if (isGuest(authentication)){
               response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
           }
           else {
               Player currentPlayer = playerRepository.findByUserName(authentication.getName());
               Optional<GamePlayer> optionalGamePlayer = gamePlayerRepository.findById(gamePlayer_id);
               if (!optionalGamePlayer.isPresent()) {
                   response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
               } else {
                   if (currentPlayer.getId() != optionalGamePlayer.get().getPlayer().getId()) {
                       response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                   } else {
                       GamePlayer gamePlayer = optionalGamePlayer.get();
                       GamePlayer enemyGamePlayer = gamePlayer.enemyGamePlayer();
                       if (enemyGamePlayer == null) {
                           response = new ResponseEntity<>("You cannot play without an enemy!", HttpStatus.FORBIDDEN);
                       } else {
                           if (enemyGamePlayer.getShips().stream().count() == 0)
                               response = new ResponseEntity<>("Your enemy did not place his gems yet", HttpStatus.FORBIDDEN);
                           else {
                               Salvo max = optionalGamePlayer.get().getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);
                               Salvo max_enemy = optionalGamePlayer.get().enemyGamePlayer().getSalvoes().stream().max(Comparator.comparing(Salvo::getTurn)).orElse(null);

                               if (max != null) {
                                   if (max.getTurn() >= salvo.getTurn())
                                       response = new ResponseEntity<>("Incorrect turn", HttpStatus.FORBIDDEN);
                                   else {
                                       if (gamePlayer.getShips().size() == 0)
                                           response = new ResponseEntity<>("Place your gems before shooting!", HttpStatus.FORBIDDEN);
                                       else {
                                           if (salvo.getLocations().stream().count() > 5)
                                               response = new ResponseEntity<>("Too many shots", HttpStatus.FORBIDDEN);
                                           else {
                                                   if (max_enemy != null) {
                                                       if (salvo.getLocations().stream().count() > (5 - gamePlayer.getSunkShips(salvo).sunkShips().stream().count()))
                                                           response = new ResponseEntity<>("Too many shots", HttpStatus.FORBIDDEN);
                                                       else {
                                                           if (Math.abs(salvo.getTurn() - max_enemy.getTurn()) > 1)
                                                               response = new ResponseEntity<>("Wait to your enemy's turn!", HttpStatus.FORBIDDEN);
                                                           else {
                                                               gamePlayer.addSalvo(salvo);
                                                               gamePlayerRepository.save(gamePlayer);
                                                               response = new ResponseEntity<>(HttpStatus.CREATED);
                                                           }
                                                   }} else {
                                                       if (salvo.getTurn() > 1)
                                                           response = new ResponseEntity<>("Wait to your enemy's turn!", HttpStatus.FORBIDDEN);
                                                       else {
                                                           gamePlayer.addSalvo(salvo);
                                                           gamePlayerRepository.save(gamePlayer);
                                                           response = new ResponseEntity<>(HttpStatus.CREATED);
                                                       }
                                                   }
                                               }
                                           }
                                       }
                                   }
                               else {
                                   if (gamePlayer.getShips().size() == 0)
                                       response = new ResponseEntity<>("Place your ships before shooting!", HttpStatus.FORBIDDEN);
                                   else {
                                       if (salvo.getLocations().stream().count() > 5)
                                           response = new ResponseEntity<>("Too many Salvoes", HttpStatus.FORBIDDEN);
                                           else {
                                                       gamePlayer.addSalvo(salvo);
                                                       gamePlayerRepository.save(gamePlayer);
                                                       response = new ResponseEntity<>(HttpStatus.CREATED);
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
           return response;
    }


    @RequestMapping(path ="/games/players/{gamePlayer_id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> placeShips(@PathVariable Long gamePlayer_id, @RequestBody Set<Ship> ships,
                                             Authentication authentication) {
        ResponseEntity<Object> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            Player currentPlayer = playerRepository.findByUserName(authentication.getName());
            Optional<GamePlayer> optionalGamePlayer = gamePlayerRepository.findById(gamePlayer_id);
            if (!optionalGamePlayer.isPresent()) {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else {
                if (currentPlayer.getId() != optionalGamePlayer.get().getPlayer().getId()) {
                    response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                } else {
                    if (optionalGamePlayer.get().getShips().stream().count() > 0) {
                        response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    } else {
                        GamePlayer gamePlayer = optionalGamePlayer.get();
                        ships.stream().forEach(ship -> gamePlayer.addShip(ship));
                        gamePlayerRepository.save(gamePlayer);
                        response = new ResponseEntity<>(HttpStatus.CREATED);
                    }
                }
            }
        }
        return response;
    }



    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}


