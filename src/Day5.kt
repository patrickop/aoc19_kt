import java.io.File

enum class ParameterMode (val code: Int){
    ADDRESS(0),
    IMMEDIATE(1);
    companion object {
        fun fromInt(value: Int) = ParameterMode.values().first { it.code == value }
    }
}
fun List<Int>.update(index : Int, value: Int) = this.take(index) + listOf<Int>(value) + this.drop(index+1)

class ProgramState(val pc: Int, val program : List<Int>, val input : List<Int>, val output : List<Int>, val halted : Boolean){
    fun increaseProgramCounter(step:Int) : ProgramState{
       return ProgramState(this.pc + step, this.program, this.input, this.output, this.halted)
    }
    fun setProgramCounter(programCounter : Int) : ProgramState{
        return ProgramState(programCounter, this.program, this.input, this.output, this.halted)
    }
    fun updateProgram(address : Int, value : Int) : ProgramState {
        return ProgramState(this.pc, this.program.update(address, value), this.input, this.output, this.halted)
    }
    fun writeOutput(value : Int) : ProgramState {
        return ProgramState(this.pc, this.program, this.input, this.output + listOf<Int>(value), this.halted)
    }
    fun popInput() : Pair<Int?, ProgramState> {
        return if (this.input.isEmpty()) {
            Pair(null, this)
        } else {
            val newInputs = this.input.drop(1)
            val inputValue = this.input[0]
            Pair(inputValue, ProgramState(this.pc, this.program, newInputs, this.output, this.halted))

        }
    }

    fun clearOutput() : ProgramState {
        return ProgramState(this.pc, this.program, this.input , listOf<Int>(), this.halted)
    }

    fun feedInput(value : Int) : ProgramState {
        return ProgramState(this.pc, this.program, this.input + listOf<Int>(value), this.output, this.halted)
    }

    fun halt() : ProgramState {
        return ProgramState(this.pc, this.program, this.input, this.output , true)
    }
    fun getValue(mode : ParameterMode, address: Int) : Int  {
        return if (mode == ParameterMode.ADDRESS) {
            this.program[this.program[address]]
        }else {
            this.program[address]
        }
    }
}

abstract class Instruction (val param1Mode: ParameterMode, val param2Mode: ParameterMode, val param3Mode: ParameterMode) {
    abstract fun execute(state : ProgramState) : ProgramState
    fun readParam1(state: ProgramState) : Int {
        return state.getValue(param1Mode, state.pc+1)
    }
    fun readParam2(state: ProgramState) : Int {
        return state.getValue(param2Mode, state.pc+2)
    }
    fun readParam3Immediate(state: ProgramState) : Int {
        return state.getValue(ParameterMode.IMMEDIATE, state.pc+3)
    }
    fun readParam1Immediate(state: ProgramState) : Int {
        return state.getValue(ParameterMode.IMMEDIATE, state.pc+1)
    }
}

class Add(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return state
                .updateProgram(readParam3Immediate(state),readParam1(state)+readParam2(state))
                .increaseProgramCounter(4)
    }
}
class Multiply(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return state
                .updateProgram(readParam3Immediate(state),readParam1(state)*readParam2(state))
                .increaseProgramCounter(4)
    }
}
class Read(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        val (input, intermediateState) = state.popInput()
        return input?.let {
            intermediateState
                ?.increaseProgramCounter(2)
                ?.updateProgram(readParam1Immediate(state), it)
        } ?: state
    }
}
class Write(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return state
                .writeOutput(readParam1(state))
                .increaseProgramCounter(2)
    }
}
class Halt(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return state
                .halt()
                .increaseProgramCounter(1)
    }
}
class JumpIfTrue(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return if (readParam1(state) != 0){
            state.setProgramCounter(readParam2(state))
        } else {
            state.increaseProgramCounter(3)
        }
    }

}

class JumpIfFalse(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return if (readParam1(state) == 0){
            state.setProgramCounter(readParam2(state))
        } else {
            state.increaseProgramCounter(3)
        }
    }

}

class LessThan(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return if (readParam1(state) < readParam2(state)){
            state
                    .updateProgram(readParam3Immediate(state), 1)
                    .increaseProgramCounter(4)
        } else {
            state
                    .updateProgram(readParam3Immediate(state), 0)
                    .increaseProgramCounter(4)
        }
    }

}

class Equals(param1Mode: ParameterMode, param2Mode: ParameterMode, param3Mode: ParameterMode) : Instruction(param1Mode, param2Mode, param3Mode) {

    override fun execute(state: ProgramState): ProgramState {
        return if (readParam1(state) == readParam2(state)){
            state
                    .updateProgram(readParam3Immediate(state), 1)
                    .increaseProgramCounter(4)
        } else {
            state
                    .updateProgram(readParam3Immediate(state), 0)
                    .increaseProgramCounter(4)
        }
    }

}

fun parseInstruction ( state: ProgramState) : Instruction {
    val pc = state.pc
    val opcode = state.getValue(ParameterMode.IMMEDIATE,pc)
    val operation = opcode % 100
    val param1Mode : ParameterMode = ParameterMode.fromInt((opcode % 1000) / 100)
    val param2Mode : ParameterMode = ParameterMode.fromInt((opcode % 10000) / 1000)
    val param3Mode : ParameterMode = ParameterMode.fromInt((opcode % 100000) / 10000)
    return when (operation) {
        1 -> Add(param1Mode,param2Mode,param3Mode)
        2 -> Multiply(param1Mode,param2Mode,param3Mode)
        3 -> Read(param1Mode,param2Mode,param3Mode)
        4 -> Write(param1Mode,param2Mode,param3Mode)
        5 -> JumpIfTrue(param1Mode,param2Mode,param3Mode)
        6 -> JumpIfFalse(param1Mode,param2Mode,param3Mode)
        7 -> LessThan(param1Mode,param2Mode,param3Mode)
        8 -> Equals(param1Mode,param2Mode,param3Mode)
        99 -> Halt(param1Mode,param2Mode,param3Mode)
        else -> throw Exception("BOOM $opcode")
    }
}

fun tick(state : ProgramState): ProgramState {
    val instruction = parseInstruction(state)
    return instruction.execute(state)
}
fun execute(state : ProgramState): ProgramState {
    val executedState = tick(state)
    return if (executedState.halted) {
        executedState
    } else {
        execute(executedState)
    }
}
fun readProgram(file :String) : List<Int> =
        File(file).
                readLines().
                fold(initial = "") {sum, x -> sum + x}.
                split(",").
                map{it.toInt()}


fun main() {
    val initialProgram = readProgram("res/day5_input.txt")
    val state = ProgramState(0, initialProgram, input = listOf(5), output = listOf(), halted = false)
    val final = execute(state)
    println(final.output)

}
