package com.codeoftheweb.salvo;


import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(GamePlayerRepository gamePlayerRepository, GameRepository gameRepository,
									  PlayerRepository playerRepository,
									  ScoreRepository scoreRepository, ShipRepository shipRepository, SalvoRepository salvoRepository) {
		return (args) -> {

			// save a couple of games
			LocalDateTime date1 = LocalDateTime.now();
			LocalDateTime date2 = date1.plusHours(1);
			LocalDateTime date3 = LocalDateTime.now();
			LocalDateTime date4 = date3.plusHours(1);
			LocalDateTime date5 = LocalDateTime.now();
			LocalDateTime date6 = date5.plusHours(1);
			LocalDateTime date7 = LocalDateTime.now();
			LocalDateTime date8 = date7.plusHours(1);
			LocalDateTime date9 = date7.plusHours(1);
			LocalDateTime date10 = date7.plusHours(1);
			LocalDateTime date11 = date7.plusHours(1);
			LocalDateTime date12 = date7.plusHours(1);
			LocalDateTime date13 = date7.plusHours(1);
			LocalDateTime date14 = date7.plusHours(1);


			Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));


			Game game1 = new Game(date1);
			Game game2  =new Game(date2);
			Game game3  =new Game(date3);
			Game game4  =new Game(date4);
			Game game5  =new Game(date5);
			Game game6  =new Game(date6);
			Game game7  =new Game(date7);
			Game game8  =new Game(date8);


 			List<String> location1 = new ArrayList<>(Arrays.asList("H2","H3","H4"));
			List<String> location2 = new ArrayList<>(Arrays.asList("E1","F1","G1"));
			List<String> location3 = new ArrayList<>(Arrays.asList("B4","B5"));
			List<String> location4 = new ArrayList<>(Arrays.asList("B5","C5","D5"));
			List<String> location5 = new ArrayList<>(Arrays.asList("F1","F2"));
			List<String> location6 = new ArrayList<>(Arrays.asList("A2","A3","A4"));
			List<String> location7 = new ArrayList<>(Arrays.asList("G6","H6"));
			List<String> location8 = new ArrayList<>(Arrays.asList("C6","C7"));




			List<String> salvoLocation1 = new ArrayList<>(Arrays.asList("B4","B5","B6"));
			List<String> salvoLocation2 = new ArrayList<>(Arrays.asList("E1","H3","A2"));
			List<String> salvoLocation3 = new ArrayList<>(Arrays.asList("B5","D5","C7"));
			List<String> salvoLocation4 = new ArrayList<>(Arrays.asList("C5","C6"));
			List<String> salvoLocation5 = new ArrayList<>(Arrays.asList("H1","H2","H3"));
			List<String> salvoLocation6 = new ArrayList<>(Arrays.asList("E1","F2","G3"));
			List<String> salvoLocation7 = new ArrayList<>(Arrays.asList("B5","C6","H1"));
			List<String> salvoLocation8 = new ArrayList<>(Arrays.asList("C5","C7","D5"));
			List<String> salvoLocation9 = new ArrayList<>(Arrays.asList("B5","B6","C7"));
			List<String> salvoLocation10 = new ArrayList<>(Arrays.asList("C6","D6","E6"));
			List<String> salvoLocation11 = new ArrayList<>(Arrays.asList("H1","H2"));
			List<String> salvoLocation12 = new ArrayList<>(Arrays.asList("B5","C5","F1"));
			List<String> salvoLocation13= new ArrayList<>(Arrays.asList("F2","D5"));
			List<String> salvoLocation14 = new ArrayList<>(Arrays.asList("A2","A4","G6"));
			List<String> salvoLocation15 = new ArrayList<>(Arrays.asList("A3","H6"));
			List<String> salvoLocation16 = new ArrayList<>(Arrays.asList("G6","H6","A4"));
			List<String> salvoLocation17 = new ArrayList<>(Arrays.asList("A2","A3","D8"));
			List<String> salvoLocation18 = new ArrayList<>(Arrays.asList("A3","A4","F7"));
			List<String> salvoLocation19 = new ArrayList<>(Arrays.asList("A2","G6","H6"));
			List<String> salvoLocation20 = new ArrayList<>(Arrays.asList("A1","A2","A3"));
			List<String> salvoLocation21 = new ArrayList<>(Arrays.asList("G6","G7","G8"));
			List<String> salvoLocation22 = new ArrayList<>(Arrays.asList(""));



			Ship ship1 = new Ship (location1, "destroyer");
			Ship ship2 = new Ship (location2, "submarine");
			Ship ship3 = new Ship (location3, "patrol_boat");
			Ship ship4 = new Ship (location4, "destroyer");
			Ship ship5 = new Ship (location5, "patrol_boat");
			Ship ship6 = new Ship (location4, "destroyer");
			Ship ship7 = new Ship (location8, "patrol_boat");
			Ship ship8 = new Ship (location6, "submarine");
			Ship ship9 = new Ship (location7, "patrol_boat");
			Ship ship10 = new Ship (location4, "destroyer");
			Ship ship11 = new Ship (location8, "patrol_boat");
			Ship ship12 = new Ship (location6, "submarine");
			Ship ship13 = new Ship (location7, "patrol_boat");
			Ship ship14 = new Ship (location4, "destroyer");
			Ship ship15 = new Ship (location8, "patrol_boat");
			Ship ship16 = new Ship (location6, "submarine");
			Ship ship17 = new Ship (location7, "patrol_boat");
			Ship ship18 = new Ship (location4, "destroyer");
			Ship ship19 = new Ship (location8, "patrol_boat");
			Ship ship20 = new Ship (location6, "submarine");
			Ship ship21 = new Ship (location7, "patrol_boat");
			Ship ship22 = new Ship (location4, "destroyer");
			Ship ship23 = new Ship (location8, "patrol_boat");
			Ship ship24 = new Ship (location4, "destroyer");
			Ship ship25 = new Ship (location8, "patrol_boat");
			Ship ship26 = new Ship (location6, "submarine");
			Ship ship27 = new Ship (location7, "patrol_boat");



			Set<Ship> setShips = new HashSet<>();
			Set<Ship> setShips2 = new HashSet<>();
			Set<Ship> setShips3 = new HashSet<>();
			Set<Ship> setShips4 = new HashSet<>();
			Set<Ship> setShips5 = new HashSet<>();
			Set<Ship> setShips6 = new HashSet<>();
			Set<Ship> setShips7 = new HashSet<>();
			Set<Ship> setShips8 = new HashSet<>();
			Set<Ship> setShips9 = new HashSet<>();
			Set<Ship> setShips10 = new HashSet<>();
			Set<Ship> setShips11 = new HashSet<>();
			Set<Ship> setShips12 = new HashSet<>();
			Set<Ship> setShips13 = new HashSet<>();




			setShips.add(ship1);
			setShips.add(ship2);
			setShips.add(ship3);
			setShips2.add(ship5);
			setShips2.add(ship6);
			setShips3.add(ship7);
			setShips3.add(ship8);
			setShips4.add(ship9);
			setShips4.add(ship10);
			setShips5.add(ship11);
			setShips5.add(ship12);
			setShips6.add(ship13);
			setShips7.add(ship14);
			setShips7.add(ship15);
			setShips8.add(ship16);
			setShips8.add(ship17);
			setShips9.add(ship18);
			setShips9.add(ship19);
			setShips10.add(ship20);
			setShips10.add(ship21);
			setShips11.add(ship22);
			setShips11.add(ship23);
			setShips12.add(ship24);
			setShips12.add(ship25);
			setShips13.add(ship26);
			setShips13.add(ship27);




			Salvo salvo1 = new Salvo(1, salvoLocation12);
			Salvo salvo2 = new Salvo(1, salvoLocation1);
			Salvo salvo3 = new Salvo(2, salvoLocation13);
			Salvo salvo4 = new Salvo(2, salvoLocation2);
			Salvo salvo5 = new Salvo(1, salvoLocation14);
			Salvo salvo6 = new Salvo(1, salvoLocation3);
			Salvo salvo7 = new Salvo(2, salvoLocation15);
			Salvo salvo8 = new Salvo(2, salvoLocation4);
			Salvo salvo9 = new Salvo(1, salvoLocation16);
			Salvo salvo10 = new Salvo(1, salvoLocation5);
			Salvo salvo11= new Salvo(2, salvoLocation17);
			Salvo salvo12= new Salvo(2, salvoLocation6);
			Salvo salvo13= new Salvo(1, salvoLocation18);
			Salvo salvo14= new Salvo(1, salvoLocation7);
			Salvo salvo15= new Salvo(2, salvoLocation19);
			Salvo salvo16= new Salvo(2, salvoLocation8);
			Salvo salvo17= new Salvo(1, salvoLocation20);
			Salvo salvo18= new Salvo(1, salvoLocation9);
			Salvo salvo19= new Salvo(2, salvoLocation21);
			Salvo salvo20= new Salvo(2, salvoLocation10);
			Salvo salvo21= new Salvo(3, salvoLocation22);
			Salvo salvo22= new Salvo(3, salvoLocation11);

			Set<Salvo> setSalvoes1 = new HashSet<>();
			Set<Salvo> setSalvoes2 = new HashSet<>();
			Set<Salvo> setSalvoes3 = new HashSet<>();
			Set<Salvo> setSalvoes4 = new HashSet<>();
			Set<Salvo> setSalvoes5 = new HashSet<>();
			Set<Salvo> setSalvoes6 = new HashSet<>();
			Set<Salvo> setSalvoes7 = new HashSet<>();
			Set<Salvo> setSalvoes8 = new HashSet<>();
			Set<Salvo> setSalvoes9 = new HashSet<>();
			Set<Salvo> setSalvoes10 = new HashSet<>();
			Set<Salvo> setSalvoes11= new HashSet<>();


			setSalvoes1.add(salvo1);
			setSalvoes1.add(salvo2);
			setSalvoes2.add(salvo3);
			setSalvoes2.add(salvo4);
			setSalvoes3.add(salvo5);
			setSalvoes3.add(salvo6);
			setSalvoes4.add(salvo7);
			setSalvoes4.add(salvo8);
			setSalvoes5.add(salvo9);
			setSalvoes5.add(salvo10);
			setSalvoes6.add(salvo11);
			setSalvoes6.add(salvo12);
			setSalvoes7.add(salvo13);
			setSalvoes7.add(salvo14);
			setSalvoes8.add(salvo15);
			setSalvoes8.add(salvo16);
			setSalvoes9.add(salvo17);
			setSalvoes9.add(salvo18);
			setSalvoes10.add(salvo19);
			setSalvoes10.add(salvo20);
			setSalvoes11.add(salvo21);
			setSalvoes11.add(salvo22);



			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);


			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);


			GamePlayer gamePlayer1 = new GamePlayer(game1, player1, date1, setShips, setSalvoes1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2, date2, setShips2, setSalvoes2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player1, date3, setShips3, setSalvoes3);
			GamePlayer gamePlayer4 = new GamePlayer(game2, player2, date4, setShips4, setSalvoes4);
			GamePlayer gamePlayer5 = new GamePlayer(game3, player2, date5, setShips5, setSalvoes5);
			GamePlayer gamePlayer6 = new GamePlayer(game3, player3, date6, setShips6, setSalvoes6);
			GamePlayer gamePlayer7 = new GamePlayer(game4, player2, date7, setShips7, setSalvoes7);
			GamePlayer gamePlayer8 = new GamePlayer(game4, player1, date8, setShips8, setSalvoes8);
			GamePlayer gamePlayer9 = new GamePlayer(game5, player3, date9, setShips9, setSalvoes9);
			GamePlayer gamePlayer10 = new GamePlayer(game5, player1, date10, setShips10, setSalvoes10);
			GamePlayer gamePlayer11 = new GamePlayer(game6, player4, date11, setShips11, setSalvoes11);
			GamePlayer gamePlayer12 = new GamePlayer(game7, player3, date12);
			GamePlayer gamePlayer13 = new GamePlayer(game8, player4, date13, setShips12);
			GamePlayer gamePlayer14 = new GamePlayer(game8, player3, date14, setShips13);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);
			gamePlayerRepository.save(gamePlayer8);
			gamePlayerRepository.save(gamePlayer9);
			gamePlayerRepository.save(gamePlayer10);
			gamePlayerRepository.save(gamePlayer11);
			gamePlayerRepository.save(gamePlayer12);
			gamePlayerRepository.save(gamePlayer13);
			gamePlayerRepository.save(gamePlayer14);

			Set<Score> setScore1 = new HashSet<>();
			Set<Score> setScore2 = new HashSet<>();
			Score score1= new Score(3, LocalDateTime.now(), game1, player1);
			Score score2 = new Score(0, LocalDateTime.now(), game1, player2);
			Score score3 = new Score(1, LocalDateTime.now(), game2, player1);
			Score score4 = new Score(1, LocalDateTime.now(), game2, player2);
			Score score5= new Score(3, LocalDateTime.now(), game3, player2);
			Score score6 = new Score(0, LocalDateTime.now(), game3, player4);
			Score score7 = new Score(1, LocalDateTime.now(), game4, player2);
			Score score8 = new Score(1, LocalDateTime.now(), game4, player1);
			/*Score score3 = new Score(34);
			Score score4 = new Score(36);
			Score score5 = new Score(37);
			Score score6 = new Score(38);
			Score score7 = new Score(90);
			Score score8 = new Score(23);
			Score score9 = new Score(55);
*/

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);
		};
	}
}

