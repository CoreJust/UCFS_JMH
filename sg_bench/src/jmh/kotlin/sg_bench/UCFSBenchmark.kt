package sg_bench

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

import org.ucfs.input.DotParser
import org.ucfs.input.IInputGraph
import org.ucfs.parser.Gll
import org.ucfs.grammar.combinator.Grammar

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
open class UCFSBenchmark {
    private lateinit var grammarInstance: Grammar
    private lateinit var dotText: String
    private lateinit var parsedGraph: IInputGraph<*, *>
    private lateinit var grammarClassName: String
    private lateinit var graphPath: String

    @Setup(Level.Trial)
    fun setup() {
        graphPath = System.getProperty("graphPath")
            ?: throw IllegalArgumentException("Please set -DgraphPath=/path/to/graph.dot")

        grammarInstance = UCFSGrammar()
        dotText = java.io.File(graphPath).readText()
        parsedGraph = DotParser().parseDot(dotText)
    }

    @TearDown(Level.Trial)
    fun tearDown() { }

    @Benchmark
    fun benchParse(): Int {
        val gll = Gll.gll(grammarInstance.rsm, parsedGraph)
        val sppf = gll.parse()
        if (sppf == null) {
            throw IllegalStateException("Parsing failed, SPPF is null")
        }
        return sppf.hashCode()
    }
}
