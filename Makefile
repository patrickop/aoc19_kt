day1: outdir
	kotlinc src/Day1.kt -include-runtime -d out/Day1.jar
	java -jar out/Day1.jar
day2: outdir
	kotlinc src/Day2.kt -include-runtime -d out/Day2.jar
	java -jar out/Day2.jar
day3: outdir
	kotlinc src/Day3.kt -include-runtime -d out/Day3.jar
	java -jar out/Day3.jar
day4: outdir
	kotlinc src/Day4.kt -include-runtime -d out/Day4.jar
	java -jar out/Day4.jar
day5: outdir
	kotlinc src/Day5.kt -include-runtime -d out/Day5.jar
	java -jar out/Day5.jar

outdir:
	mkdir -p out
