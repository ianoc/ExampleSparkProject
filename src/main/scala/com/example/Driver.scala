package com.example

import spark.SparkContext
import spark.SparkContext._

object Driver {
  def main(args: Array[String]) {
    val sc = Setup.GetSC("SampleProject")

    def manhattan(p: Map[Int, Double], q: Map[Int,Double]): Double = {
      val diffs = for ((k, x) <- p; y = q.get(k); if y != None)
                    yield { Math.abs(x - y.get) }
      diffs.sum
    }

    val input = sc.textFile("intro.csv")

    val data = input.map(line => {
      val Array(user, item, rating) = line.split(",")
      (user.toInt, item.toInt -> rating.toDouble)
    }).groupByKey.map(grouped => {
      val (user, ratings) = grouped
      var ratMap = Map[Int, Double]()
      ratings.foreach(r => ratMap += r)
      (user, ratMap)
    }).cache()

    // Make n recommendations for user id using its nearest neighbor
      val id = 1
      val target = data.filter(user => {
        val (uid, ratings) = user
        uid == id
      }).take(1) /////////// in the SBT app it hangs here, before take.  ///////////
      val krat = target(0)._2
      println("Nearest neighbor recs: " + krat)

      // Remove the target user from the data file
      val filtered = data.filter(user => {
        val (uid, ratings) = user
        uid != id
      })

      // Find the nearest neighborg
      val neigh = filtered.map(user => {
        val (uid, ratings) = user
        (manhattan(ratings, krat), uid -> ratings)
      }).sortByKey(false).take(1)
      val neighRat = neigh(0)._2._2

      val recs = for ((a, r) <- neighRat; if krat.get(a) == None)
                   yield { (r, a) }
      println(recs.toList.sorted.reverse.take(1))

    sc.stop
  }
}
