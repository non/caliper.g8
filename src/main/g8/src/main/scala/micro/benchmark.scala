package micro

import scala.util.Random._
import com.google.caliper.Param

import scala.annotation.tailrec

object Benchmark extends MyRunner(classOf[Benchmark])

class Benchmark extends MyBenchmark {

  // run the benchmarks for every value in the @Param annotation
  @Param(Array("40", "80", "160", "320", "640", "1280"))
  var size: Int = _

  // you can have multiple independent @Params if you want
  // @Params(Array("8", "16", "32"))
  // var bits: Int = _

  // declare any state you want to initialize for each benchmark here
  //var arr: Array[Long] = _

  // do any per-benchmark initialization here
  override protected def setUp() {
    //arr = init(size)(nextLong)
  }

  // this benchmark times the recursive method
  def timeRecursive(reps: Int) = run(reps) {
    val x = recursive(size)
  }

  // generate the nth fibonacci number via a recursive method
  def recursive(n: Int): BigInt = {
    @tailrec def loop(i: Int, x: BigInt, y: BigInt): BigInt =
      if (i > 0) loop(i - 1, y, x + y) else y
    if (n < 1) BigInt(0) else loop(n - 1, BigInt(0), BigInt(1))
  }

  // this benchmark times the stream
  def timeStream(reps: Int) = run(reps) {
    val x = stream(size)
  }

  // generate the nth fibonacci number via a stream
  def stream(n: Int): BigInt = {
    lazy val fibs: Stream[BigInt] =
      BigInt(0) #:: BigInt(1) #:: fibs.zip(fibs.tail).map(t => t._1 + t._2)
    fibs.drop(n).head
  }
}
