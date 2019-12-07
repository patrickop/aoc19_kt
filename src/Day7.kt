
fun runProgramWith(programCode: List<Int>, phaseSetting : Int, input : Int) : Int{
    val state = ProgramState(0, programCode, input = listOf(phaseSetting, input), output = listOf(), halted = false)
    val final = execute(state)
    return final.output[0]

}

fun generatePermutations(elements : List<Int>) : List<List<Int>> {
    return if (elements.size <= 1) {
        listOf(elements)
    } else {
        var ret = mutableListOf<List<Int>>()
        for (elem in elements){
            val otherElements = elements.filter { it != elem }
            ret.addAll(generatePermutations(otherElements).map { listOf(elem) + it })
        }
        ret
    }
}


fun main() {
    val initialProgram = readProgram("res/day7_input.txt")

    val outputs = generatePermutations(listOf(0,1,2,3,4)).map {
        var carry = 0
        for (phaseSetting in it) {
            carry = runProgramWith(initialProgram, phaseSetting, carry)
        }
        carry
    }
    val maxOutput = outputs.max() ?: throw Exception("No Max")
    println("$maxOutput")

}
