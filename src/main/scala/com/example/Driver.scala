package com.example

import spark.SparkContext
import spark.SparkContext._

object Driver {
  def main(args: Array[String]) {
    val sc = Setup.GetSC("SampleProject")
    val input_list = List(1, 2, 3)
    println(input_list == sc.parallelize(input_list).collect.toList)
    sc.stop
  }
}
