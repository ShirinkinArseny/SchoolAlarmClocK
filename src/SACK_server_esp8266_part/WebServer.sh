#!/bin/bash

#
#   Эмулирует работу веб-сервера на esp8266.
#   То же API для сети, что и на lua-прошивке wifi-модуля.
#

cd /home/nameless/IdeaProjects/SoundTest/src/SACK_server_part/

echo "   local html = [[" >  result.lua
cat Common.html >> result.lua
echo "]]   " >>  result.lua

echo "   local css = [[" >>  result.lua
cat Style.css >> result.lua
echo "]]   " >>  result.lua

echo "   local js = [[" >>  result.lua
cat Scripts.js >> result.lua
echo "]]   " >>  result.lua

cat WebServer.lua >>  result.lua

luvit result.lua