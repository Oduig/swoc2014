call sbt assembly
del greedy.zip
java -jar out/proguard5.0/lib/proguard.jar @proguard.config
"C:\Program Files\7-Zip\7za.exe" a -tzip greedy.zip greedy.jar
PAUSE