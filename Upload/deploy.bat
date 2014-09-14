cd ..\Greedy
call sbt assembly
copy target\scala-2.11\Greedy-assembly-1.0.jar ..\Upload\upload.jar
cd ..\Upload
call "C:\Program Files\7-Zip\7za.exe" a -tzip upload.zip upload.jar
call python preparefile.py
del upload.zip
call D:\Programs\Curl\curl.exe "http://swoc.jdub.nl/login" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" -H "Accept-Encoding: gzip, deflate" -H "Accept-Language: en-us,en;q=0.7,nl;q=0.3" -H "Connection: keep-alive" --cookie-jar ./cookie.txt -H "Host: swoc.jdub.nl" -H "Referer: http://swoc.jdub.nl/" -H "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0" -H "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryWlCBiqkCrSe11n3H" --data-binary @credentials.txt
call D:\Programs\Curl\curl.exe "http://swoc.jdub.nl/api/bot/upload/" --cookie ./cookie.txt -H "Origin: http://swoc.jdub.nl" -H "Accept-Encoding: gzip,deflate" -H "Accept-Language: en-US,en;q=0.8,fr;q=0.6,nl;q=0.4" -H "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36" -H "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryUjAnyaOeGq83DPKC" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" -H "Cache-Control: max-age=0" -H "Referer: http://swoc.jdub.nl/" -H "X-CookiesOK: I explicitly accept all cookies" -H "Connection: keep-alive" --data-binary @upload.dat --compressed -X POST
del cookie.txt
del upload.dat
PAUSE