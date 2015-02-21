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

