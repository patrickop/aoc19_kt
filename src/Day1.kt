import java.io.File


fun massesFromFile(file: String) = File(file).readLines().map{it.toInt()}

fun fuelConsumption(mass : Int) = mass / 3 - 2

fun fuelConsumptionRecursive(mass : Int): Int {
    val fuel = fuelConsumption(mass);
    return when {
        fuel <= 0 -> 0
        else -> return fuel + fuelConsumptionRecursive(fuel)
    }
}

fun totalFuelConsumption(file : String) : Int{
    val fuel = massesFromFile(file).map(transform = ::fuelConsumptionRecursive)
    return fuel.fold(0) {sum, x -> sum + x}
}
fun main() {
    println(message = totalFuelConsumption(file = "res/day1_input.txt"))
}