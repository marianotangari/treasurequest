var game_player = getSearchParams();
var alfa = ["A", "B", "C", "D", "E", "F", "G", "H","I","J"];
var enemySalvoes = [];
var mySalvoes = [];
var cells = [];
var logoutButton = document.getElementById("logoutButton");
var container = document.getElementById("container");
var rotable = true;
var doneButton = document.getElementById("doneButton");
var doneButton2 = document.getElementById("doneButton2");
doneButton2.style.display = "none";
doneButton.style.display = "none";
var doneDiv = document.getElementById("doneDiv");
var shipsClasses = ["carrier", "battleship", "submarine", "destroyer", "patrol_boat"];
var placedShips = [];
var pala_cells = [];
var infoPlaceShips = document.getElementById("infoPlaceShips2");
var currentPlayer;
var turn;
var tableGameData = document.getElementById("gameData1").querySelector("tbody");
var tableGameData2 = document.getElementById("gameData2").querySelector("tbody");
var gameOverInfoDiv = document.getElementById('gameOverInfoDiv');
gameOverInfoDiv.style.display = "none";
var gameOverAnimation = document.getElementById('gameOverAnimation');
gameOverAnimation.style.display = 'none';

//Primer fetch: crea la grilla y la tabla de información.

function fetchData(){
    fetch("/api/game_view/" + game_player.gp, {mode: 'no-cors'}).then(function(response){
        return response.json()
        }).then(function(json){
        games = json;
        createShips();
         }).then(function(){
            currentPlayer= games.game.GamePlayers.filter(x => x.gpid == game_player.gp);
             turn = games.game.Salvoes.filter(x => x.player_id == currentPlayer[0].id)
             .sort((a,b) => b.Turn - a.Turn).map(x => x.Turn);
            if (turn == 0)
               turn = 1;
            else
              turn = turn[0] +1
            if (games.game_state == 0){
                container.style.display = "none";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.style.display = "block";
                gameOverInfoDiv.innerHTML = "<h2>You win!</h2><br><h6>Congratulations<br><br>" + currentPlayer[0].username +
                " !<br><br> You get 3 points!<br><br></h6>"
                }
            if (games.game_state == 1){

                container.style.display = "none";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.style.display = "block";
                gameOverInfoDiv.innerHTML = "<h2>You Lose!</h2> <br><br> <h6> You get 0 points...<br></h2>";
                }
             if (games.game_state == 9){

                container.style.display = "none";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.style.display = "block";
                gameOverInfoDiv.innerHTML = "<h2> It's a tie! </h2> <br><br> <h6> You get 1 point <br></h2>";
                }
            $(document).ready(function(){
              createGameDataTable();
                        })
            getSalvoesEnemy();
            putMySalvoes();
            placeSalvoes();
            getPosition();
            }).then(function(){
                putEnemySalvoes();
                 getHitShips();

                }).catch(function(){
            container.innerHTML = "";
           container.innerHTML = "<h3 style='margin-top: 25%'> PAGE NOT FOUND </h3>"}
         );
    }

// Segundo fetch: llena algunos datos de la tabla y trae nuevos datos del juego para actualizar la tabla.

function fetchData2(){
     fetch("/api/game_view/" + game_player.gp, {mode: 'no-cors'}).then(function(response){
            return response.json()
            }).then(function(json){
            games = json;
             }).then(function(){
                currentPlayer= games.game.GamePlayers.filter(x => x.gpid == game_player.gp);
                getSalvoesEnemy();
             }).then(function(){

            if (games.game_state == 0){
                container.style.display = "none";
                gameOverInfoDiv.style.display = "block";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.innerHTML = "<h2>You win!</h2><br><h6>Congratulations<br><br>" + currentPlayer[0].username +
                " !<br><br> You get 3 points!</h6>"
                }
            if (games.game_state == 1){
                container.style.display = "none";
                gameOverInfoDiv.style.display = "block";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.innerHTML = "<h2>You Lose!</h2> <br><br> <h6> You get 0 points...</h2>";
                }
             if (games.game_state == 9){
                container.style.display = "none";
                gameOverInfoDiv.style.display = "block";
                gameOverAnimation.style.display = "block"
                $(gameOverAnimation).addClass('gameOverAnimation');
                gameOverInfoDiv.innerHTML = "<h2> It's a tie! </h2> <br><br> <h6> You get 1 point </h2>";
                }
                fillTable()
                placeSalvoes()
                putMySalvoes();
                putEnemySalvoes();
                getHitShips()
                })
    }

function fetchData3(){
     fetch("/api/game_view/" + game_player.gp, {mode: 'no-cors'}).then(function(response){
     return response.json()}).then(function(json){
     games = json;
     }).then(function(){
     grid = $('#grid').data('gridstack');
     blockGrid(grid)}).then(function(){
     getPositionShips();})
    }

fetchData();
$(document).ready(setInterval(function(){fetchData2();}, 2000));


function createShips(){

            var options = {
                //grilla de 10 x 10
                width: 10,
                height: 10,
                //separacion entre elementos (les llaman widgets)
                verticalMargin: 0,
                //altura de las celdas
                cellHeight: 45,
                //deshabilitando el resize de los widgets
                disableResize: true,
                //widgets flotantes
        		float: true,
                //removeTimeout: 100,
                //permite que el widget ocupe mas de una columna
                disableOneColumnMode: true,
                //false permite mover, true impide
                staticGrid: false,
                //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
                animate: true
            }
            //se inicializa el grid con las opciones
            $('.grid-stack').gridstack(options);
            grid = $('#grid').data('gridstack');
            grid2 = $('#grid2').data('gridstack');

            createGrid(11, $("#grid-ships"), false);
            createGrid(11, $("#grid-ships_2"), true);


            //todas las funciones se encuentran en la documentación
            //https://github.com/gridstack/gridstack.js/tree/develop/doc
        };
 const createGrid = function(size, element, isTrue){
        let wrapper = document.createElement('DIV')
        wrapper.classList.add('grid-wrapper')
            for(let i = 0; i < size; i++){
                let row = document.createElement('DIV')
                row.classList.add('grid-row')
                row.id =`grid-row${i}`
                wrapper.appendChild(row)
                for(let j = 0; j < size; j++){
                    let cell = document.createElement('DIV');
                    cell.classList.add('grid-cell')
                    cell.id = `${alfa[i-1]}${ j}`;
            if (i > 0 && j > 0 && isTrue){
               for (let l=0; l < games.game.Salvoes.length; l++){
                    if (games.game.Salvoes[l].player_id == parseInt(getSearchParams().gp)){
                        for (let m = 0; m < games.game.Salvoes[l].Locations.length; m++){
                        location_x_2 = parseInt(alfa.indexOf(games.game.Salvoes[l].Locations[m].slice(0,1)))+1;
                        location_y_2 = parseInt(games.game.Salvoes[l].Locations[m].slice(1,2));
                        if ((i == location_x_2) && (j == location_y_2)){
                            $(cell).addClass('pala2');
                            }
                        }
                    }
                }
            }
            if(j===0 && i > 0){
               let textNode = document.createElement('SPAN')
               textNode.innerHTML = "<h5>" + String.fromCharCode(i+64) + "</h5>";
               textNode.style.color = "white";
               cell.appendChild(textNode)
               }
            if(i === 0 && j > 0){
               let textNode = document.createElement('SPAN')
               textNode.innerHTML = "<h5>" + j + "</h5>";
               textNode.style.color = "white";
               cell.appendChild(textNode)
               }
               row.appendChild(cell)
               }
            }
            element.append(wrapper)
        }
 function rotateShips(shipType, cells){
                if (rotable){
                    $(`#${shipType}`).click(function(){
                        if($(this).children().hasClass(`${shipType}Horizontal`)){
                            if ((parseInt($(this).attr("data-gs-y")) + parseInt($(this).attr("data-gs-width")) < 11) &&
                            grid.isAreaEmpty(parseInt($(this).attr("data-gs-x")),parseInt($(this).attr("data-gs-y"))+1,
                            parseInt($(this).attr("data-gs-height")), parseInt($(this).attr("data-gs-width"))-1)){
                            grid.resize($(this),1,cells);
                            $(this).children().removeClass(`${shipType}Horizontal`);
                            $(this).children().addClass(`${shipType}Vertical`);
                            }

                        }else{
                            if((parseInt($(this).attr("data-gs-x")) + parseInt($(this).attr("data-gs-height")) <11) &&
                            grid.isAreaEmpty(parseInt($(this).attr("data-gs-x"))+1,parseInt($(this).attr("data-gs-y")),
                            parseInt($(this).attr("data-gs-height"))-1, parseInt($(this).attr("data-gs-width")))){
                            grid.resize($(this),cells,1);
                            $(this).children().addClass(`${shipType}Horizontal`);
                            $(this).children().removeClass(`${shipType}Vertical`);
                            }

                        }
                     })
                }
         };

function getPositionShips(){
grid = $('#grid').data('gridstack');
count_ships = games.game.Ships.length;
        for (var i = 0; i < games.game.Ships.length; i++){
            type = games.game.Ships[i].Type;
            width = games.game.Ships[i].Location.length;
            height_beggining = alfa.indexOf(games.game.Ships[i].Location[0].slice(0,1));
            horizontal_beggining= parseInt(games.game.Ships[i].Location[0].slice(1,2)) - 1;
            horizontal_end = parseInt(games.game.Ships[i].Location[(games.game.Ships[i].Location.length)-1].slice(1,2))-1;
             if (horizontal_beggining != horizontal_end){
                grid.addWidget($('<div id="' + type + '"><div class="grid-stack-item-content ' +type + 'Horizontal"></div><div/>'),
                horizontal_beggining,height_beggining , width, 1);
                cells.push(alfa[height_beggining] + "" + (horizontal_beggining +1))
                for (var j = 1; j < width; j++){
                cells.push(alfa[height_beggining] + "" + (horizontal_beggining + j +1))
                }
             }
             else{
                grid.addWidget($('<div id="' + type +'"><div class="grid-stack-item-content ' +type +'Vertical"></div><div/>'),
                    horizontal_beggining,height_beggining , 1, width);
                    cells.push(alfa[height_beggining] + "" + (horizontal_beggining+1))
                    for (var j = 1; j < width; j++){
                        cells.push(alfa[(j + height_beggining)] + "" + (horizontal_beggining+1))
                    }
             }
        }
 }

function getPosition(){
        grid = $('#grid').data('gridstack');
        grid2 = $('#grid2').data('gridstack');
    if (games.game_state == 7 || games.game_state == 8 || games.game_state == 2 || games.game_state == 3
    || games.game_state == 10){
        getPositionShips();
        blockGrid(grid);
        placeSalvoes();
        doneButton2.style.display = "inline";
        doneDiv2.style.textAlign = "center";
    }


    if (games.game_state == 4 || games.game_state == 5 || games.game_state == 6){
        placeSalvoes();
        doneButton.style.display = "inline";
        doneDiv.style.textAlign = "center";
        doneButton2.style.display = "inline";
        doneDiv2.style.textAlign = "center";
        grid.addWidget($('<div id="carrier"><div class="grid-stack-item-content carrierHorizontal"></div></div>'),
        0,0,5,1)
        grid.addWidget($('<div id="battleship"><div class="grid-stack-item-content battleshipHorizontal"></div></div>'),
        2,0,4,1)
        grid.addWidget($('<div id="submarine"><div class="grid-stack-item-content submarineHorizontal"></div></div>'),
        4,2,3,1)
        grid.addWidget($('<div id="destroyer"><div class="grid-stack-item-content destroyerHorizontal"></div></div>'),
        7,5,3,1)
        grid.addWidget($('<div id="patrol_boat"><div class="grid-stack-item-content patrol_boatHorizontal"></div></div>'),
        8,6,2,1)
        rotateShips("carrier", 5);
        rotateShips("battleship", 4);
        rotateShips("submarine",3);
        rotateShips("destroyer", 3);
        rotateShips("patrol_boat",2);
        }
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

function getSalvoesEnemy(){
    for (var i = 0; i < games.game.Salvoes.length; i++){
            currentPlayer= games.game.GamePlayers.filter(x => x.gpid == game_player.gp);
            if (games.game.Salvoes[i].player_id != currentPlayer[0].id){
            //for (var j=0; j< games.game.Salvoes[i].Locations.length; j++){
                enemySalvoes.push({"locations" : games.game.Salvoes[i].Locations,
               "turn" : games.game.Salvoes[i].Turn});
                //}
            } else{
            for (var k=0; k< games.game.Salvoes[i].Locations.length; k++){
            mySalvoes.push(games.game.Salvoes[i].Locations[k])
            }
        }
    }
}

function putEnemySalvoes(){
        for (var j = 0; j < enemySalvoes.length; j++){
            for (var k = 0; k < enemySalvoes[j].locations.length; k++){
                    var cell = Array.from(document.getElementById("grid-ships").querySelectorAll(".grid-cell"))
                    .filter(x => x.id == enemySalvoes[j].locations[k]).map(y => $(y).addClass('hit'));
                 }
            }
        }

function putMySalvoes(){
    var cell = Array.from(document.getElementById("grid-ships_2").querySelectorAll(".grid-cell"));
            for (var j = 0; j < mySalvoes.length; j++){
                        cell.filter(x => x.id == mySalvoes[j]).map(y => $(y).addClass('pala2'));
                     }
    }

function getPlacedShips (){
    for (var i = 1; i < 6; i++){
        var ship = document.getElementById(shipsClasses[i-1]);
        var x = parseInt($(ship).attr("data-gs-x"));
        var y = parseInt($(ship).attr("data-gs-y"));
        var width = $(ship).attr("data-gs-width");
        var height = $(ship).attr("data-gs-height");
        if (width == "1"){
            var locations_array = [alfa[y] + (x + 1)];
            for (var j = 1; j < height; j++){
                locations_array.push(alfa[(y + j)] + (x +1));
                }
            placedShips.push(
            { "type": shipsClasses[i -1],
              "location": locations_array})
        }
        else{
            if (height == "1"){
                 var locations_array = [alfa[y] + (x + 1)];
                 for (var j = 1; j < width; j++){
                    locations_array.push(alfa[y] + (x + 1 + j));
                                }
                 placedShips.push(
                    { "type": shipsClasses[i -1],
                      "location": locations_array})
                }
            }
        }
    }


logoutButton.addEventListener("click",function(){
    $.post("/api/logout").done(function() { console.log("logged out!"); });
    window.location = "games.html";
    });

doneButton.addEventListener("click", function(){
   getPlacedShips();
   infoPlaceShips.innerHTML = "";
   $.post({url: "/api/games/players/" + game_player.gp + "/ships",
   data: JSON.stringify(placedShips),
   datatype: Text, contentType: "application/json"}).done(function(){
        doneDiv.removeChild(doneButton);
        grid = $('#grid').data('gridstack');
        grid.removeAll()
        fetchData3()
        }).fail(function(jqXHR){
        infoPlaceShips.innerHTML = "<h6>" + jqXHR.responseText + "</h6>"
        var time = setTimeout(() => infoPlaceShips.innerHTML = "",3000);
        $(infoPlaceShips).addClass('infoPlaceShips2');
        })
   });
doneButton2.addEventListener('click', function(){
    getPlacedSalvoes();
       $.post({url: "/api/games/players/" + game_player.gp + "/salvoes",
       data: JSON.stringify(pala_cells[0]), datatype: Text, contentType: "application/json"}).done(function(){
       infoPlaceShips.style.display = "";
       removePala()
       fetchData2()
       }).done(function(){createGameDataTable()}).fail(function(jqXHR){
       removePala()
        turn -=1
        infoPlaceShips.innerHTML = "<h6>" +  jqXHR.responseText+ "</h6>"
        var time = setTimeout(() => infoPlaceShips.innerHTML = "",3000);
        $(infoPlaceShips).addClass('infoPlaceShips2');

    })}
    )


function blockGrid (element){
     element.setStatic(true);
     element.resizable('.grid-stack-item', false);
     rotable = false;
}

function blockGrid2(){
    var cells_2 = Array.from(document.querySelectorAll("#grid-ships_2 .grid-cell")).filter((x) => $(x).has("span").length == 0);
         cells_2.forEach(x => x.removeEventListener('click', addPala));}
//Funcion que le asigna un Listener a cada celda
function placeSalvoes(){
    var cells_2 = Array.from(document.querySelectorAll("#grid-ships_2 .grid-cell")).filter((x) => $(x).has("span").length == 0);
    Array.from(cells_2).map(x => x.addEventListener('click', addPala));
}

//Función para tomar los salvos que ubica el jugador

function getPlacedSalvoes(){
    // Tomo las celdas que tienen la clase "pala"
    var locations = Array.from(document.querySelectorAll("#grid-ships_2 .grid-cell.pala")).map(x => x.id);
    pala_cells[0] = {"turn": turn, "locations" : locations};
    turn += 1;
    }
function getHitShips(){
    var cells_2 = Array.from(document.querySelectorAll("#grid-ships_2 .grid-cell")).filter((x) => $(x).has("span").length == 0);
      currentPlayer = games.game.Salvoes.filter(x => x.player_id == currentPlayer[0].id)
            .sort((a,b) => b.Turn - a.Turn);
    var hits = currentPlayer.map(y => y.hits);
    for (var i = 0; i < hits.length; i ++){
        for (var k = 0; k < hits[i].length; k++){
            for ( var j= 0; j< cells_2.length; j++){
                if (hits[i][k] == cells_2[j].id){
                    $(cells_2[j]).addClass('hit2')
                    $(cells_2[j]).removeClass('pala2')
                }
            }
        }
    }
    }

// función que ubica las palas/salvos en la grilla del enemigo. Una pala/salvo por cada ship que el jugador posee.

function addPala(e){
        var cell = e.target;
        if (cell.id != "undefined0" && !$(cell).hasClass("pala") && !($(cell).hasClass('pala2'))
        && !($(cell).hasClass('hit2'))){
                $(cell).addClass('pala');
        }
        else {
            if($(cell).hasClass("pala")){
            $(cell).removeClass('pala');
            }
        }
}

function removePala(){
    var locations = Array.from(document.querySelectorAll("#grid-ships_2 .grid-cell.pala")).map(x => $(x).removeClass('pala'))
    }

function createGameDataTable(){

    var currentPlayer= games.game.GamePlayers.filter(x => x.gpid == game_player.gp);
    tableGameData.innerHTML = "";
    tableGameData2.innerHTML = "";
    var currentUser = games.game.Salvoes.filter(x => x.player_id == currentPlayer[0].id)
    .sort((a,b) => b.Turn - a.Turn);
    var opponent = games.game.Salvoes.filter(x => x.player_id != currentPlayer[0].id)
    .sort((a,b) => b.Turn - a.Turn);
        row = tableGameData.insertRow(-1);
        cell_1= row.insertCell(0);
        cell_2 = row.insertCell(1);
        cell_3 = row.insertCell(2);
        cell_1.innerHTML = "<p id='sunkShips'></p>";
        cell_2.innerHTML = "<h5><p id='MyTurn'>1</p></h5>"
        cell_3.innerHTML = "<h5><p id='remainingSalvoes'>5</p></h5>";
        row_opp = tableGameData2.insertRow(-1);
        cell_1_opp= row_opp.insertCell(0);
        cell_2_opp= row_opp.insertCell(1);
        cell_3_opp  =row_opp.insertCell(2)
        cell_2_opp.innerHTML = "<h5><p id='turn'>1</p></h5>"
    }


function fillTable(){
     var currentUser = games.game.Salvoes.filter(x => x.player_id == currentPlayer[0].id)
        .sort((a,b) => b.Turn - a.Turn);
     var opponent = games.game.Salvoes.filter(x => x.player_id != currentPlayer[0].id)
        .sort((a,b) => b.Turn - a.Turn);
     var cell1 = document.getElementById('sunkShips');
     var turnCell = document.getElementById('turn');
     var turnCell2 = document.getElementById('MyTurn');
     var enemy = games.game.GamePlayers.filter(x => x.gpid != game_player.gp);
     var cell3 = document.getElementById('remainingSalvoes');
     if (enemy != 0)
        cell_3_opp.innerHTML = "<h5><p id='enemy'>" + enemy[0].username + "</p></h5>"
     //if (currentUser[0].sunk_ships.length != 0 && currentUser != null)
        //currentUser[0].sunk_ships.forEach(x => cell1.innerHTML += "<h5>" + x.Type + "</h5>");
     if (opponent == 0)
                cell3.innerHTML = "<h5><p id='remainingSalvoes'>5</p></h5>"
     else{
        if (opponent[0].sunk_ships.length != 0){
            cell_1.innerHTML = ""
            cell_1.innerHTML = "<h5>" + opponent[0].sunk_ships.length +  "<br></h5>";
            }
            cell3.innerHTML = "<h5><p id='remainingSalvoes'>" + (5- opponent[0].sunk_ships.length) + "</p></h5>";
            turnCell.innerHTML = "<h5><p id='turn'>" +(opponent[0].Turn +1) + "</p></h5>";
     }

    if (currentUser != 0){
            turnCell2.innerHTML = "<h5><p id='MyTurn'>" +  (currentUser[0].Turn +1) + "</p></h5>";
            if (currentUser[0].sunk_ships.length != 0){
            cell_1_opp.innerHTML = ""
            cell_1_opp.innerHTML = "<h5>" + currentUser[0].sunk_ships.length +  "<br></h5>";
            }
            }
    }