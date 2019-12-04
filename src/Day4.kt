import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    var i = 272091
    var passwords = mutableListOf<Int>()
    while (i < 815432) {
        val oldI = i
        val iStr = i.toString()
        var duplication = false
        for (digit in 1 until iStr.length) {
            val prev = iStr[digit-1].toString().toInt()
            val cur = iStr[digit].toString().toInt()
            if (prev > cur){
                //prune
                val newI =
                        (iStr.slice(0 until digit).toInt().toString() +
                                prev.toString().repeat(iStr.length-digit)).toInt()
                println("$i violates at $digit ($prev > $cur), pruning to $newI")
                i = newI
                break
            }
            if (prev == cur){
                if ( digit > 1 && iStr[digit-2].toString().toInt() == cur) {
                } else if ( digit < iStr.length - 1 && iStr[digit+1].toString().toInt() == cur){
                }
                else {
                    duplication = true
                }
            }
        }
        if (duplication && i == oldI) passwords.add(element = i)
        if (i == oldI) i += 1
    }
    println("$passwords")
    val count = passwords.size
    println("$count")
}