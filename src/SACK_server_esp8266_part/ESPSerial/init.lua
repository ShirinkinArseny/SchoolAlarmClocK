local SSID = "Peter the Cat Wi-Fi :3"
local password = "12210000"
local globalClient = nil

function resetClient()
     globalClient = nil
     uart.on("data")
end
function getFromUART(data)
     globalClient:send(data)   
end

function data(client, request)
     if globalClient==nil then
          globalClient=client
          uart.on("data",1,getFromUART,0)
     end
     uart.write(0,request)
     collectgarbage()
end

uart.setup(0,115200,8,0,1,0)

--Connecting
wifi.setmode(wifi.STATION)
wifi.sta.config(SSID,password)
wifi.sta.connect()

tmr.alarm(0, 1000, 1, function()
if wifi.sta.getip()~=nil then
     srv=net.createServer(net.TCP, 150)
     srv:listen(2992, function(conn)
          conn:on("receive", data)
          conn:on("disconnection", resetClient)
     end)
     tmr.stop(0)
end
end)
