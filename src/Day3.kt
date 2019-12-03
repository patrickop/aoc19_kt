import java.io.File
import kotlin.math.absoluteValue

class Coordinate(val right: Int, val up: Int, val stepsToReach : Int) {
    fun manhattanDistance() = up.absoluteValue+right.absoluteValue

    override fun equals(other: Any?): Boolean {
        return if (other is Coordinate) {
            up == other.up && right == other.right
        } else {
            return false
        }
    }

    override fun toString(): String {
        return "($right, $up) steps to reach: $stepsToReach"
    }
    fun isOrigin(): Boolean {
        return up == 0 && right== 0;
    }
}
fun getCoordinateAtDistance(start: Coordinate, direction :Char, magnitude :Int) : Coordinate =
    when (direction) {
        'U' -> Coordinate(right = start.right, up = start.up + magnitude, stepsToReach = start.stepsToReach + magnitude)
        'D' -> Coordinate(right = start.right, up = start.up - magnitude, stepsToReach = start.stepsToReach + magnitude)
        'R' -> Coordinate(right = start.right + magnitude, up = start.up, stepsToReach = start.stepsToReach + magnitude)
        'L' -> Coordinate(right = start.right - magnitude, up = start.up, stepsToReach = start.stepsToReach + magnitude)
        else -> throw Exception("BOOM")
    }

tailrec fun generateAllCoordinates(commands : List<String>, start: Coordinate) : List<Coordinate>
{
    if (commands.isEmpty()) {return listOf(start)}
    val head = commands[0]
    val tail = commands.drop(1)
    val direction = head[0]
    val magnitude = head.drop(1).toInt()
    if (magnitude < 1) { return generateAllCoordinates(tail, start) }
    val newCoordinates = (0..magnitude).map { getCoordinateAtDistance(start, direction, it) }
    val finalCoordinate = newCoordinates.last()
    return newCoordinates + generateAllCoordinates(tail, finalCoordinate)

}
fun readCommandStrings(file :String) =
        File(file).
        readLines().
        map { it.split(",") }

fun main() {
    val allCoordinates = readCommandStrings("res/day3_input.txt").map{generateAllCoordinates(it,Coordinate(0,0, 0))}
    val upper = allCoordinates[0].distinct()
    val lower = allCoordinates[1].distinct()
    // Brute force!!
    val intersections = upper.filter {lower.contains(it) && !it.isOrigin()}
    val intersectionSteps = intersections.map { u -> u.stepsToReach + (lower.find { l -> u == l }?.stepsToReach ?: 0) }
    val closestIntersection = intersections.minBy { it.manhattanDistance() }
    val leastStepsIntersection = intersectionSteps.min()
    val manhattanDistance = closestIntersection?.manhattanDistance()
    println("closest intersection $closestIntersection")
    println("closest intersection mh dist $manhattanDistance")
    println("least steps intersection $leastStepsIntersection")

}