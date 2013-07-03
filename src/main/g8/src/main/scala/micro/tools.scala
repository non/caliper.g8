package micro

import scala.reflect.ClassTag
import scala.util.Random._
import scala.util.Sorting.quickSort

import com.google.caliper.Runner 
import com.google.caliper.SimpleBenchmark
import com.google.caliper.Param

/**
 * Extend this to create an actual benchmarking class.
 */
trait MyBenchmark extends SimpleBenchmark {

  /**
   * Sugar for building arrays using a per-cell init function.
   */
  def init[A: ClassTag](size: Int)(f: => A) = {
    val data = Array.ofDim[A](size)
    for (i <- 0 until size) data(i) = f
    data
  }

  /**
   * More sugar for building arrays using a per-cell init function.
   */
  def mkarray[A: ClassTag: Ordering](size: Int, layout: String = "random")(f: => A): Array[A] = {
    val data = init(size)(f)
    val ct = implicitly[ClassTag[A]]
    val ordering = implicitly[Ordering[A]]
    layout match {
      case "random" =>
      case "sorted" => quickSort(data)
      case "reversed" => quickSort(data)(ordering.reverse)
      case _ => sys.error("unknown layout: %s" format layout)
    }
    data
  }

  /**
   * Sugar to run 'f' for 'reps' number of times.
   */
  def run(reps: Int)(f: => Unit): Unit = (0 until reps).foreach(_ => f)
}

/**
 * Extend this to create a main object which will run 'cls' (a benchmark).
 */
abstract class MyRunner(val cls: java.lang.Class[_ <: com.google.caliper.Benchmark]) {
  def main(args: Array[String]): Unit = Runner.main(cls, args: _*)
}
