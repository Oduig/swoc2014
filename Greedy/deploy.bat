call sbt assembly
del greedy.zip
del greedy.dat
del greedy.jar
cp target\scala-2.11\Greedy-assembly-1.0.jar greedy.jar
"C:\Program Files\7-Zip\7za.exe" a -tzip greedy.zip greedy.jar
python preparefile.py
del greedy.zip
PAUSE