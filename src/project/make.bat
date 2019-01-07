javac -encoding UTF-8 -cp depends\Jama-1.0.3.jar;depends\gluegen-rt.jar;depends\gluegen-rt-natives-windows-amd64.jar;depends\jogl-all.jar;depends\jogl-all-natives-windows-amd64.jar java\*.java
mkdir class
move java\*.class class\
copy resources\* class\
copy depends\* class\
cd class
java -cp "Jama-1.0.3.jar;gluegen-rt.jar;gluegen-rt-natives-windows-amd64.jar;jogl-all.jar;jogl-all-natives-windows-amd64.jar;" MyPainter