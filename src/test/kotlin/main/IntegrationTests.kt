package main

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.*

class IntegrationTests : Spek({

  instructions
    .split("\n")
    .map { it.trim() }
    .filter {
      it.isNotEmpty()
        && it.startsWith("java -jar")
        && !it.contains("<trainingFile>")
    }
    .map {
      it.split(" ")
        .drop(3)
        .toTypedArray()
    }
    .forEach { arguments ->

      it("does not crash with arguments: ${Arrays.toString(arguments)}") {
        main(arguments)
      }
    }
})

