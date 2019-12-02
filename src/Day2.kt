import java.io.File

fun readProgram(file :String) =
    File(file).
    readLines().
    fold(initial = "") {sum, x -> sum + x}.
    split(",").
    map{it.toInt()}

fun List<Int>.update(index : Int, value: Int) = this.take(index) + listOf<Int>(value) + this.drop(index+1)
fun getLhs(p: List<Int>, pc : Int) = p[p[pc+1]]
fun getRhs(p: List<Int>, pc : Int) = p[p[pc+2]]
fun getDstAddress(p: List<Int>, pc : Int) = p[pc+3]
fun applyOperator(p : List<Int>, pc : Int = 0, op : (Int, Int) -> Int) = op(getLhs(p,pc), getRhs(p,pc))
fun sum(lhs : Int, rhs : Int) = lhs+rhs;
fun product(lhs : Int, rhs : Int) = lhs*rhs;

fun execute(p : List<Int>, pc : Int = 0): List<Int> {
    return when (p[pc]) {
        1   -> execute(p.update(getDstAddress(p,pc), applyOperator(p,pc,::sum)), pc + 4)
        2   -> execute(p.update(getDstAddress(p,pc), applyOperator(p,pc,::product)), pc + 4)
       99   -> p
       else -> {
           throw Exception("BOOM!")
       }
   }
}

fun runProgram(noun : Int, verb : Int) :  Int {
    val original : List<Int> = readProgram("res/day2_input.txt")
    val modified = original.update(1,noun).update(2,verb)
    return execute(modified)[0]

}

fun main() {
    val target = 19690720;
    for (noun in 1..99){
        for (verb in 1..99) {
            val result = runProgram(noun, verb)
            if (result == target) {
                val solution = 100 * noun + verb
                println("Solution noun: $noun, verb: $verb, solution number: $solution")
                return
            } else if (result > target) {
                break
            }
        }
    }
}
