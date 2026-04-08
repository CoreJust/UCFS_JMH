package sg_bench

import org.ucfs.input.DotParser
import org.ucfs.parser.Gll
import org.ucfs.sppf.writeSppfToDot
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: <graph.dot> [output_prefix]")
        return
    }
    val graphPath = args[0]
    val outputPrefix = if (args.size > 1) args[1] else "sppf"

    println("Parsing graph from $graphPath...")
    val dotText = File(graphPath).readText()
    val parsedGraph = DotParser().parseDot(dotText)

    val grammar = UCFSGrammar()
    val gll = Gll.gll(grammar.rsm, parsedGraph)
    val sppfForest = gll.parse()

    if (sppfForest == null) {
        println("Parsing failed – SPPF is null")
        return
    }

    println("Parsing succeeded. Number of SPPF roots: ${sppfForest.size}")

    // Write each root to a separate file
    var index = 0
    for (root in sppfForest) {
        val outputFile = "${outputPrefix}_${index}.dot"
        writeSppfToDot(root, outputFile)
        println("  Wrote $outputFile")
        index++
    }
    println("Verification complete.")
}
