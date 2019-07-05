var games = [];
var id_array = [];
var leaderboard = [];
var leaderboard_2 = [];
var tableGames = document.getElementById("games");
var tableRanking = document.getElementById("ranking");
var loginButton = document.getElementById("loginButton");
var logoutButton = document.getElementById("logoutButton");
var usernameInput = document.getElementById("usernameInput");
var passwordInput = document.getElementById("passwordInput");
var loginDiv = document.getElementById("login");
var signUpButton  = document.getElementById("signUpButton");
var errorDiv = document.getElementById("errorDiv");
var game_player = getSearchParams();
var createGameButton = document.getElementById("createGameButton");


function fetchData(){
    fetch("/api/games", {mode: 'no-cors'}).then(function(response){
        return response.json()
    }).then(function(json){
     games = json;
     createTable_Games();
     createTable_Ranking();
      if (games.player == 'GUEST'){
         loginDiv.style.display = "block";}
      else{
         loginDiv.style.display = "none";}
       });
    }

fetchData();

function createTable_Games(){
    tableGames.innerHTML = "";
    for (var i = 0; i < games.games.length; i++){
        var row = tableGames.insertRow(-1);
        var cell = row.insertCell(0);
        var cell_2 = row.insertCell(1);
        var cell_3 = row.insertCell(2);
        cell.innerHTML = "<h5>" + games.games[i].id + "</h5>";
        cell_2.innerHTML = "<h5>"+ games.games[i].created + "</h5>";
        for (var j = 0; j < games.games[i].GamePlayers.length; j++){
            if (games.games[i].GamePlayers[j].id == games.player.Player_id)
                cell_3.innerHTML = '<button><a href="/web/game.html?gp=' + games.games[i].GamePlayers[j].gpid + '" class="loginText">Go to Game</a></button>'
            else if (games.games[i].GamePlayers.length != 2)
                cell_3.innerHTML = '<button id="' + games.games[i].id + '" onclick="joinGame(event)" class="loginText">Join Game</button>';
        }
    }
}

function createTable_Ranking(){
    tableRanking.innerHTML = "";
    leaderboard = [];
    id_array = [];
    for (var i = 0; i < games.games.length; i++){
        for (var j = 0; j < games.games[i].GamePlayers.length; j++){
        if (id_array.indexOf(games.games[i].GamePlayers[j].id) == -1){
            id_array.push(games.games[i].GamePlayers[j].id);
            id_array.push(
             {"username" : games.games[i].GamePlayers[j].username,
             "score" : 0,
             "Win" : 0,
             "Tie" : 0,
             "Lose" : 0
            })
        }
        leaderboard.push({
            "id" : games.games[i].GamePlayers[j].id,
            "score" : games.games[i].GamePlayers[j].score
            })
        }
    }

    for (var i = 0; i < leaderboard.length; i++){
        for (var j = 0; j < id_array.length; j += 2){
            if (leaderboard[i].id == id_array[j]){
                id_array[j+1].score += leaderboard[i].score;
                switch (leaderboard[i].score){
                     case 3:
                    id_array[j+1].Win ++;
                    break;
                     case 1:
                    id_array[j+1].Tie ++;
                    break;
                     case 0:
                    id_array[j+1].Lose ++;
                    break;
                    }
                }
            }
    }

    var sorted_array = id_array.filter(element => element.username != null).sort( (a,b) => b.score - a.score);

    for (var i = 0; i < sorted_array.length; i ++){
        var row = tableRanking.insertRow(-1);
        cell_1 = row.insertCell(0);
        cell_2 = row.insertCell(1);
        cell_3 = row.insertCell(2);
        cell_4 = row.insertCell(3);
        cell_5 = row.insertCell(4);
        cell_1.innerHTML = "<h5>" + sorted_array[i].username + "</h5>";
        cell_2.innerHTML = "<h5>" + sorted_array[i].score + "</h5>";
        cell_3.innerHTML = "<h5>" +sorted_array[i].Win + "</h5>";
        cell_4.innerHTML = "<h5>" + sorted_array[i].Tie + "</h5>";
        cell_5.innerHTML = "<h5>" +sorted_array[i].Lose + "</h5>";
        }
}

loginButton.addEventListener("click",function(){
     user= usernameInput.value;
     password = passwordInput.value;
    $.post("/api/login", { username: user , password: password })
    .catch(function(error){alert("Error: Invalid user name or password")})
    .done(function() {
    fetchData();
    })
    }
    );

logoutButton.addEventListener("click",function(){
    $.post("/api/logout").done(function() { console.log("logged out!");
    passwordInput.value = "";
    usernameInput.value = "";
    errorDiv.innerHTML = "";
     fetchData();})
    }
    );

signUpButton.addEventListener('click', function(){
    user= usernameInput.value;
    password = passwordInput.value;
    $.post("/api/players", { username: user, password: password})
    .done(function(response) {console.log("Registered!");
     $.post("/api/login", { username: user , password: password})
     .done(function() { console.log("logged in!");
      fetchData();});
    })});

createGameButton.addEventListener('click', function(){
     $.post("/api/games")
     .done(function(response) {window.location =  "game.html?gp=" + response.gpid})
     .catch(function() {alert("User not logged in")})
});

function joinGame(e){
    buttonId = e.target.id;
    $.post("/api/game/" + parseInt(buttonId) + "/players")
    .done(function(response) {window.location = "game.html?gp=" + response.gpid})
    .catch(function() {alert("User not logged in")})
    }

function getSearchParams(){
   var params = {};
   document.location.search.substr(1).split('&'
).forEach(pair => {
   [key, value] = pair.split('=');
   params [key]= decodeURI(value);
 })
 return params;
};