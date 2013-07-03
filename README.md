= Caliper.g8 =

This repo contains a [giter8](http://github.com/n8han/giter8#readme) template
that makes it easy to quickly write micro-benchmarks using Caliper. The
projects uses Scala 2.10.2 and SBT 0.12.4.

== Quick Start ==

To use this template you'll need to have installed
[sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html#installing-sbt)
and [giter8](https://github.com/n8han/giter8#installation).

Once you have those installed, you can instantiate the `caliper.g8` template
with the following command:

```
$ g8 non/caliper

Quickly build micro-benchmarks using Caliper.

name [Caliper Benchmarking Template]: demo

Template applied in ./demo
```

This command instantiated the template in the `demo` directory. directory.
Next, change to the directory, launch sbt, and run:

```
$ cd demo
$ sbt
[info] Loading project definition from /Users/osheim/demo/project
[info] Updating {file:/Users/osheim/demo/project/}default-001901...
[info] Resolving org.scala-sbt#precompiled-2_10_1;0.12.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/osheim/demo/project/target/scala-2.9.2/sbt-0.12/classes...
[info] Set current project to micro (in build file:/Users/osheim/demo/)
[info] Updating {file:/Users/osheim/demo/}micro...
[info] Resolving com.google.code.gson#gson;1.7.1 ...
[info] Done updating.
[info] Compiling 2 Scala sources to /Users/osheim/demo/target/scala-2.10/classes...
[info] Set current project to micro (in build file:/Users/osheim/demo/)
>
```

```
> run
[info] Running micro.Benchmark
[info]  0% Scenario{vm=java, trial=0, benchmark=Recursive, size=40} 1375.70 ns; ?=3.70 ns @ 3 trials
[info]  8% Scenario{vm=java, trial=0, benchmark=Stream, size=40} 5626.79 ns; ?=27.80 ns @ 3 trials
[info] 17% Scenario{vm=java, trial=0, benchmark=Recursive, size=80} 2952.29 ns; ?=9.01 ns @ 3 trials
[info] 25% Scenario{vm=java, trial=0, benchmark=Stream, size=80} 11258.46 ns; ?=111.81 ns @ 3 trials
[info] 33% Scenario{vm=java, trial=0, benchmark=Recursive, size=160} 6401.08 ns; ?=29.24 ns @ 3 trials
[info] 42% Scenario{vm=java, trial=0, benchmark=Stream, size=160} 24085.49 ns; ?=93.74 ns @ 3 trials
[info] 50% Scenario{vm=java, trial=0, benchmark=Recursive, size=320} 13581.49 ns; ?=74.11 ns @ 3 trials
[info] 58% Scenario{vm=java, trial=0, benchmark=Stream, size=320} 49899.63 ns; ?=1062.51 ns @ 10 trials
[info] 67% Scenario{vm=java, trial=0, benchmark=Recursive, size=640} 30962.70 ns; ?=482.06 ns @ 10 trials
[info] 75% Scenario{vm=java, trial=0, benchmark=Stream, size=640} 105990.81 ns; ?=154.84 ns @ 3 trials
[info] 83% Scenario{vm=java, trial=0, benchmark=Recursive, size=1280} 74175.30 ns; ?=983.50 ns @ 10 trials
[info] 92% Scenario{vm=java, trial=0, benchmark=Stream, size=1280} 230204.87 ns; ?=4463.03 ns @ 10 trials
[info]
[info] benchmark size     us linear runtime
[info] Recursive   40   1.38 =
[info] Recursive   80   2.95 =
[info] Recursive  160   6.40 =
[info] Recursive  320  13.58 =
[info] Recursive  640  30.96 ====
[info] Recursive 1280  74.18 =========
[info]    Stream   40   5.63 =
[info]    Stream   80  11.26 =
[info]    Stream  160  24.09 ===
[info]    Stream  320  49.90 ======
[info]    Stream  640 105.99 =============
[info]    Stream 1280 230.20 ==============================
[info]
[info] vm: java
[info] trial: 0
[success] Total time: 122 s, completed Jul 2, 2013 8:22:01 PM
```

The built-in benchmark tests two Fibonacci implementations. The `size`
parameter is the Fibonacci term to be generated (e.g. the 40th
Fibonacci number, 102334155). As you can see, the Stream-based version
is 3-4x slower than the tail-recursive version.

== Writing Benchmarks ==

The benchmarks are located in `src/main/scala/micro/benchmarks.scala`. Each
benchmark has a corresponding method whose name is `testXyz` where `Xyz` is
the benchmark name. For example, if you wanted a benchmark named `BubbleSort`
you'd name your method `testBubbleSort`.

All benchmark methods should follow the same basic form:

```scala
  def testBubbleSort(reps: Int) = run(reps) {
    // your code here...
  }
```

If you need to define classes, objects, or complex methods it is often useful
to define these outside of the benchmark method and just call into them:

```scala
  class Complicated(a: Double, b: String) {
    def doSomething(c: Long): Long = ...
  }

  def testComplicated(reps: Int) = run(reps) {
    new Complicated(3.0, "foo").doSomething(1234L)
  }
```


Also, it's important that each benchmark is self-contained. Ideally the
benchmarks won't modify any state. If you want to test something that does
mutate state (for instance, sorting an array in-place), you'll want to
initialize the state in the `setUp` method, and make sure that no other
benchmarks use it:

```scala
  var data1: Array[Int] = _
  var data2: Array[Int] = _

  override protected def setUp() {
    // initialize two random arrays of integers
    data1 = init(1024)(nextInt)
    data2 = init(1024)(nextInt)
  }

  def testBubbleSort(reps: Int) = run(reps) {
    inPlaceBubbleSort(data1)
  }

  def testSelectionSort(reps: Int) = run(reps) {
    inPlaceSelectionSort(data2)
  }
```

Notice that we have two separate arrays, one for each benchmark. Imagine if
both benchmarks used `data1`. In that case, `testBubbleSort` would run,
sorting the array. Then `SelectionSort` would run on an already-sorted array,
and the test would no longer be valid.

== Thinking about Benchmarks ==

TODO

== Copyright ==

This code is provided to you as free software under the MIT license.
