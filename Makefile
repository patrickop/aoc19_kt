day1: outdir
	kotlinc src/Day1.kt -include-runtime -d out/Day1.jar
	java -jar out/Day1.jar
day2: outdir
	kotlinc src/Day2.kt -include-runtime -d out/Day2.jar
	java -jar out/Day2.jar

outdir:
	mkdir -p out
