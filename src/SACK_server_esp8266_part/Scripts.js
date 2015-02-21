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
}