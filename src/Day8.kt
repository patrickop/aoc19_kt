import java.io.File

fun readRawPixelsFromFile(file : String) : List<Int>{
    return File(file).
            readLines().
            fold(initial = "") {sum, x -> sum + x}.
            map{it.toString().toInt()}
}

fun multiZip(list : List<List<Int>>) : List<List<Int>> {
    val heads = list.map { it.first() }
    val tails = list.map { it.drop(1) }
    val tailLengths = tails.map { it.size }
    val areAllTailLengthsEqual = tailLengths.groupBy { it }.size == 1
    if (!areAllTailLengthsEqual) {throw Exception("Unequal lengths in zip")}
    val tailsLength = tailLengths[0]
    return if (tailsLength < 1) {
        listOf(heads)
    } else {
        listOf(heads) + multiZip(tails)
    }
}

fun renderPixel(pixelLayers : List<Int>) : Int?  {
    return if (pixelLayers.isEmpty()) {
        null
    } else {
        if (pixelLayers[0] != 2){
            pixelLayers[0]
        } else {
            renderPixel(pixelLayers.drop(1))
        }
    }
}

fun printPixel(pixel : Int?) : String {
    return when (pixel) {
        1 -> "+"
        0 -> " "
        null -> " "
        else -> "E"
    }
}

fun printRow(row : List<Int?>) : String {
    return row.map ( ::printPixel ). fold("") { sum, ch -> sum+ch}
}

fun main () {
    val imageWidth = 25
    val imageHeight = 6
    val pixelsPerLayer = imageHeight * imageWidth
    val pixelsRaw = readRawPixelsFromFile("res/day8_input.txt")
    val layers = pixelsRaw.chunked(pixelsPerLayer)
    val leastZeroDigitsLayer = layers.minBy { layer -> layer.count { pixel -> pixel == 0  } }
    val oneDigits = leastZeroDigitsLayer?.count { pixel -> pixel == 1}
    val twoDigits = leastZeroDigitsLayer?.count { pixel -> pixel == 2}
    val p1Solution = oneDigits!! * twoDigits!!
    println("Part 1 Solution ; $p1Solution")
    val layersStacked = multiZip(layers)
    val renderedImage = layersStacked.map { renderPixel(it) }
    assert(!renderedImage.contains(null))
    val pictureRows =  renderedImage.chunked(imageWidth)
    val pictureRowsPrinted = pictureRows.map { printRow(it)}
    println("Part 2 Solution :")
    pictureRowsPrinted.forEach(::println)

}