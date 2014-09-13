del cookie.txt
D:\Programs\Curl\curl.exe --user Guidoffrey:baratheon --cookie-jar ./cookie.txt http://swoc.jdub.nl/#/login > nul
D:\Programs\Curl\curl.exe --connect-timeout 60 -H "Host: swoc.jdub.nl" --cookie ./cookie.txt -H "Connection: keep-alive" -H "Content-Type: multipart/form-data; boundary=---------------------------9832550717318" --data-binary @greedy.zip -X POST http://swoc.jdub.nl/api/bot/upload --progress-bar > nul
PAUSE