call sbt assembly
del greedy.zip
"C:\Program Files\7-Zip\7za.exe" a -tzip greedy.zip target\scala-2.11\Greedy-assembly-1.0.jar
PAUSE