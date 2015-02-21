   local html = [[
<!DOCTYPE html>

<html>
<head>
    <title>RING</title>

    <link href='http://fonts.googleapis.com/css?family=Exo+2:100&subset=latin,cyrillic' rel='stylesheet' type='text/css'>

    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">

    <script src="Scripts.js"></script>
    <link rel="stylesheet" href="Style.css">

</head>
<body>
<h1>Rings</h1>

<canvas height='200' width='800' id='example'>Our osom canvas :3</canvas>
<br>
<br>
<button class="greenButton" onclick='zoomIn();'>Zoom in</button>
<button class="greenButton" onclick='zoomOut();'>Zoom out</button>

<div id="tables">

</div>

<script>
    buildPage()
</script>


<button class="greenButton">SAVE ALL DIS SHIT TO DUINO</button>

<div class="footer">
    <br>
    This site is a part of SACK project by Perm's 146 school. This if footer.
    <br>
    a-arsenij@yandex.ru - SACK-duino-part, SACK-server-part, SACK-client-part
    <br>
    peter.zh@gmail.com - SACK-esp-part
</div>

</body>
</html>]]   
   local css = [[
body {
        font-family: 'Exo 2', sans-serif;
        text-align: center;
        background-color: #eee;
        margin: 0px;
        }


        .cardTitleBox {
        position: relative;
        top: 0px;
        left: 0px;
        width: 100%;
        height: 60px;

        text-align: center;
        font-size: 34pt;
        padding-top: 0px;
        border-style: none;
        background-color: #ffa76d;
        color: #222;
        box-shadow:  0 0 3px rgba(0,0,0,0.4);
        }

        .card {
        margin:0 auto;
        position: relative;
        width: 50%;
        min-width: 700px;

        margin-top: 40px;
        margin-bottom: 40px;

        background-color: #fff;
        box-shadow:  0 0 3px rgba(0,0,0,0.8);
        }

        .cardContent {
        text-align: left;
        padding: 10px;
        color: #222;
        }

        button {
        resize: none;
        display:inline;
        font-family: 'Exo 2', sans-serif;
        font-size: 16pt;
        height: 35px;
        min-width: 190px;
        vertical-align:top;
        padding-top: 0px;
        border-radius: 3px 3px;
        box-shadow:  0 0 0px rgba(0,0,0,1);
        transition: box-shadow .3s;
        border: 1px solid #A00;
        }

        .footer {
        width: 100%;
        height: 100px;
        background-color: #333;
        color: #eee;
        margin: 0px;
        margin-top: 40px;
        }

        button:hover {
        box-shadow:  0 0 10px rgba(0,0,0,1);
        }

        .redButton .yellowButton {
            width: 100%;
        }

        .redButton {
        border-color: #A00;
        background-color: #FCC;
        }

        .redButton:hover {
        box-shadow:  0 0 10px rgba(64,0,0,1);
        }

        .greenButton {
        border-color: #0A0;
        background-color: #CFC;
        }

        .greenButton:hover {
        box-shadow:  0 0 10px rgba(0,64,0,1);
        }

        .yellowButton {
        border-color: #AA0;
        background-color: #FFC;
        }

        .yellowButton:hover {
        box-shadow:  0 0 10px rgba(64,64,0,1);
        }

        td {
            padding: 10px;
            valign: top;
        }

        table {
            width: 100%;
        }]]   
   local js = [[
var xhr = new XMLHttpRequest();
function sendPOST(address, content) {
    xhr.open("POST", address, true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    xhr.send(content);
}

function loadData(getter, callback) {
                  var request=new XMLHttpRequest();
                  request.onreadystatechange=function() {
                    if (request.readyState==4 && request.status==200) {
                      callback.call(request.responseText);
                    }
                  };
                  request.open("GET", getter);
                  request.send();
            }

function translateToHumanTime(time) {

    time=Math.floor(time);

    while (time<0) {
        time+=(3600*24);
    }
    while (time>=3600*24) {
        time-=(3600*24);
    }

    var sec=time%60;
    var min=Math.floor(time/60)%60;
    var hour=Math.floor(time/3600)%3600;

    return hour+":"+min+":"+sec;

}

function fillRedCircle(ctx, x, y) {
                ctx.beginPath();
                ctx.arc(x, y, 5, 0, 2 * Math.PI, false);
                ctx.fillStyle = "#faa";
                ctx.fill();
                ctx.lineWidth = 2;
                ctx.strokeStyle = "#444";
                ctx.stroke();
            }

function fillCircle(ctx, x, y) {
                ctx.beginPath();
                ctx.arc(x, y, 6, 0, 2 * Math.PI, false);
                ctx.fillStyle = "#444";
                ctx.fill();
            }

function drawCircle(ctx, x, y) {
    ctx.beginPath();
    ctx.arc(x, y, 6, 0, 2 * Math.PI, false);
    ctx.fillStyle = "#eee";
    ctx.fill();
    ctx.lineWidth = 2;
    ctx.strokeStyle = "#444";
    ctx.stroke();
}

function drawText(ctx, text, x, y) {
    ctx.fillStyle = "#444";
    ctx.font = "italic 30pt 'Exo 2'";
    ctx.fillText(text, x, y);
}


var parsedIncomingData;
var currentWeekDay=0;


var visibleDistance=8192;
var visibleDistanceTarget=visibleDistance;
var visibleDistanceDelta=0;

var time;

function zoomIn() {

    if (visibleDistance>128) {
        visibleDistanceTarget=Math.floor(visibleDistance/2);
        visibleDistanceDelta=Math.abs(visibleDistance-visibleDistanceTarget);
        }

}

function zoomOut() {

    if (visibleDistance<65534){
        visibleDistanceTarget=Math.floor(visibleDistance*2);
        visibleDistanceDelta=Math.abs(visibleDistance-visibleDistanceTarget);}

}

function updateZoom() {

    if (visibleDistance<visibleDistanceTarget) {
          visibleDistance=Math.min(visibleDistanceTarget, visibleDistance+visibleDistanceDelta/20);
          repaint();
    } else
     if (visibleDistance>visibleDistanceTarget) {
              visibleDistance=Math.max(visibleDistanceTarget, visibleDistance-visibleDistanceDelta/20);
              repaint();
        }
}

function updateTime() {
    loadData("getTime", function() {
        time=parseInt(this);
    });
}

function repaint() {
var example = document.getElementById("example"),
        ctx     = example.getContext('2d');

        ctx.fillStyle = "#eee";
        ctx.fillRect(0, 0, example.width, example.height);

        ctx.strokeStyle = "#444";
        ctx.beginPath();
        ctx.moveTo(50, 100);
        ctx.lineTo(750, 100);
        ctx.lineWidth = 2;
        ctx.stroke();

        var minTime=time-visibleDistance;
        var maxTime=time+visibleDistance;

        for (var ring=0; ring<parsedIncomingData[currentWeekDay].length; ring++) {
            var cRing=parsedIncomingData[currentWeekDay][ring];
            if (cRing>=minTime && cRing<=maxTime) {

                fillRedCircle(ctx, 50+700*(cRing-minTime)/(visibleDistance*2), 100);

            }
        }

        fillCircle(ctx, 50, 100);
        fillCircle(ctx, 750, 100);
        drawCircle(ctx, 400, 100);

        drawText(ctx, translateToHumanTime(time-visibleDistance), 0, 50);
        drawText(ctx, translateToHumanTime(time), 320, 50);
        drawText(ctx, translateToHumanTime(time+visibleDistance), 630, 50);
}

function generateRing(day, number, time) {
    var html_result = "<tr><td>"+
            number+
            "</td><td>"+
            translateToHumanTime(time)+
            "</td><td><button class='redButton' onclick='askForRemove("+day+", "+number+");'>REMOVE</button></td>"+
            "<td><button class='yellowButton' onclick='changeRing("+day+", "+number+");'>EDIT</button></td></tr>";
    return html_result;
}

var weekDayNames=[ "Monday",
                       "Seconday",
                       "Thirday",
                       "Fourthday",
                       "Fivthday",
                       "Sixthday",
                      "Selestia's day"];

function generateDay(day, dayRings) {
    var html_result = "<div class='card'><div class='cardTitleBox'><b>"+weekDayNames[day]+"</b></div><div class='cardContent'><table>";

    for (var i=0; i<dayRings.length; i++)
        html_result+=generateRing(day, i, dayRings[i]);

    html_result+="</table><p><button class='greenButton' onclick='addNewRing("+day+");'>ADD ONE MOAR RING</button></div></div>";

    return html_result;
}

function generatePage() {
    var html_result="";

    for (var day=0; day<7; day++) {
        html_result+=generateDay(day, parsedIncomingData[day]);
    }

    var tables=document.getElementById("tables");
    tables.innerHTML=html_result;
}

function buildPage() {
        setInterval('updateTime()', 1000);
        loadData("getWeekDay", function() {
                currentWeekDay=parseInt(this);
        });
        loadData("getRings", function() {
                parsedIncomingData=JSON.parse(this);
                generatePage();
                setInterval('repaint()', 1000);
                repaint();
                setInterval('updateZoom()', 20);
        });
}

function askForRemove(day, ring) {
    var r = confirm("U really wanna remove this ring?");

    if (r) {
            parsedIncomingData[day].splice(ring, 1);
            generatePage();
    }
}

function verifyInput(newTime) {
    if (newTime != null) {
        var splitted=newTime.split(":");
        if (splitted.length!=3) {
            splitted=newTime.split(" ");
        }

        if (splitted.length==3) {
            var h=parseInt(splitted[0]);
            if (h<0 || h>=24) return -1;
            var m=parseInt(splitted[1]);
            if (m<0 || m>=60) return -1;
            var s=parseInt(splitted[2]);
            if (s<0 || s>=60) return -1;
            s=Math.floor(s/10)*10;

            return h*3600+m*60+s;
        }
    }
    return -1;
}

function changeRing(day, ring) {
    var newTime = verifyInput(prompt("New time?", "09:00:00"));
    if (newTime!=-1) {
        parsedIncomingData[day][ring]=newTime;
        parsedIncomingData[day].sort(function(a,b) { return a - b });
        generatePage();
    }
    else
    alert("Can't parse value :c");
}

function addNewRing(day) {
    var newTime = verifyInput(prompt("New time?", "09:00:00"));
    if (newTime!=-1) {
        parsedIncomingData[day].push(newTime);
        parsedIncomingData[day].sort(function(a,b) { return a - b });
        generatePage();
    }
    else
    alert("Can't parse value :c");
}]]   
local time = require('os').time

function duinoPostDataWrapper()
end

function duinoGetDataWrapper()
    return [[
        [
        [0, 10000, 15600, 19600, 21600, 23600, 29900, 38100, 42000, 58300, 59400, 81100, 82200, 83300],
        [0, 12400, 18600, 19600, 20000, 23600, 26400, 38100, 42000, 58300, 75400, 10000],
        [0, 10000, 15600, 19600, 21600, 23600, 29900, 38100, 42000, 58300, 75400, 81100, 82200, 83300],
        [0, 10000, 15600, 19600, 21600, 23000],
        [0, 10000, 15600, 19600],
        [10,10000, 15600, 19600, 21600, 23600, 29900, 38100, 42000, 58300, 75400, 81100, 82020, 83220],
        [0, 10000, 15600, 19600, 21600, 23600, 29900, 38010]
        ]
        ]]
end

function getTime()

    print("time requested")
    local value=time()%(24*3600)
    print("time requested: "..value)
    return value
end

function getWeekDay()
       print("weekday requested")
       local value=0
       print("weekday requested: "..value)
       return value
end

local correctHTTPResponse200 = [[HTTP/1.1 200 OK
Content-Type: text/html
Connection: close]]

function processGet(url)

    if (url=="/") then
    return correctHTTPResponse200..html
    end

    if (url=="/Style.css") then
    return css
    end

    if (url=="/Scripts.js") then
    return js
    end

    if (url=="/getWeekDay") then
    print("time requested")
    return ""..getWeekDay()
    end

    if (url=="/getTime") then
    print("time requested")
    return ""..getTime()
    end

    if (url=="/getRings") then
    return duinoGetDataWrapper()
    end

    return "404"
end

function processPost(data)
end

local http = require("http")

function processServing(req, res)

    print(req.url)

    local resp = processGet(req.url)

    res:writeHead(200, {
    ["Content-Type"] = "text/html",
    ["Content-Length"] = #resp
    })
    res:finish(resp)

end

http.createServer(processServing):listen(8080)

