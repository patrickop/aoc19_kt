import java.io.File

fun lineToOrbitRelation(line : String) : Pair<String, String> {
    val bodies = line.split(")")
    return Pair(bodies[1], bodies[0])

}

fun getTotalOrbits(body : String, starMap : Map<String,String>) : Int{
    val directOrbit = starMap[body] ?: throw Exception("$body not found to orbit anything in the star map.")
    return if (directOrbit == "COM") {
        1
    } else {
        return 1 + getTotalOrbits(directOrbit,starMap)
    }
}

fun getOrbitChain(body : String, starMap: Map<String, String>) : List<String> {
    return if (body == "COM"){
        listOf()
    } else {
        val directOrbit = starMap[body] ?: throw Exception("$body not found to orbit anything in the star map.")
        return getOrbitChain(directOrbit,starMap) + listOf(directOrbit)
    }

}
fun main() {
    val starMap = File("res/day6_input.txt").
                    readLines().
                    map(transform = ::lineToOrbitRelation).
                    toMap()

    val totalOrbitsPerBody = starMap.keys.map { getTotalOrbits(it, starMap)}
    val totalOrbitsSum = totalOrbitsPerBody.fold(0) { sum, value -> sum + value}
    val youOrbitChain = getOrbitChain("YOU", starMap)
    val sanOrbitChain = getOrbitChain("SAN", starMap)
    val comparedOrbitChains = youOrbitChain.zip(sanOrbitChain)
    val lastCommonBodyOrbitedIndex = comparedOrbitChains.indexOfLast { it.first == it.second }
    val youOrbitalTransfersToCommon = youOrbitChain.drop(lastCommonBodyOrbitedIndex+1).size
    val sanOrbitalTransfersFromCommon = sanOrbitChain.drop(lastCommonBodyOrbitedIndex+1).size
    val totalOrbitalTransfersNeeded = youOrbitalTransfersToCommon + sanOrbitalTransfersFromCommon


    println("$totalOrbitsSum")

    println("$totalOrbitalTransfersNeeded")

}