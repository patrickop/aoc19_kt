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
fun setUpSystem(phasesSettings : List<Int>, programCode: List<Int>)
        = phasesSettings.map { ProgramState(0,programCode, listOf(it), listOf(), false) }.toMutableList()

fun executeUntilOutputOrHalt ( state: ProgramState) : ProgramState {
    val previousOutputs = state.output.size
    val newState = tick(state)
    return if (newState.output.size > previousOutputs || newState.halted) {
        newState
    } else {
        return executeUntilOutputOrHalt(newState)
    }
}

fun runSystemNoFeedback(input : Int, systemState : List<ProgramState>) : List<ProgramState>{
    var carry = input
    return systemState.map { previousState ->
        val inputState = previousState.feedInput(carry)
        val newState = executeUntilOutputOrHalt(inputState)
        carry = newState.output.last()
        newState
    }
}

fun runSystemFeedback(input: Int, systemState: List<ProgramState>) : List<ProgramState> {
    val newSystemState = runSystemNoFeedback(input, systemState)
    return if (newSystemState.any{ it.halted}){
        newSystemState
    } else {
        runSystemFeedback(newSystemState.last().output.last(), newSystemState)
    }
}

fun main() {
    val initialProgram = readProgram("res/day7_input.txt")

    val outputs = generatePermutations(listOf(5,6,7,8,9)).map { phaseSettings ->
        val initialStates = setUpSystem(phaseSettings, initialProgram)
        val finalState = runSystemFeedback(0, initialStates)
        val finalCarry = finalState.last().output.last()
        finalCarry
    }
    val maxOutput = outputs.max()
    val justMaxOutput = maxOutput ?: throw Exception("No maximum")
    println("Biggest Final Carry $justMaxOutput")

}
