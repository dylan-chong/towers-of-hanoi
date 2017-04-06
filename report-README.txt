# Report #






IMPORTANT NOTES:
- Unzip the passworded a2 zip with the password '123' (without quotes)
  - This is needed because the submission system corrupts my jar. The
    password prevents this from happening.
  - You can run the program inside the zip with `java -jar submit/a2.jar`

- I used the dependency injection framework called Google Guice
- The java code can be found inside the full-project zip
    - The main java is under src/main/java
    - The test java is under src/test/java





## What was done ##

Note: a '[x]' represents a completed task it is the markdown symbol for a
ticked tickbox

Minimum out of 40:
* [x] (5) Has a way of specifying the start and end of a route.
* [x] (5) Has a priority queue of appropriate elements (node, parent,
  costToHere, totEstCost).
    - I used a TreeSet because it is O(n) to remove and O(log n) to re-add an
      item from a PriorityQueue, and TreeSet is O(log n) for both. The removal
      is required because these data structures aren't designed for changing
      priorities (which change as you find a shorter path), so it must be
      removed before the data is modified, then re-added.
* [x] (5) Uses an appropriate cost measure (sum of segment lengths).
* [x] (5) Uses an appropriate heuristic.
* [x] (5) Uses the appropriate graph structure of segments from a node.
    - It isn't immediately obvious that a graph is being used. The method
      {@link MapDataModel#findRoadSegmentsForNode} was used, which accesses the
      Graph
* [x] (5) Prints out the roads on the route. (Duplicates OK.)
* [x] (10) Correctly selects shortest paths.

Core out of 35 (up to 75):
* [x] (10) Finds articulation points in one part of the graph.
* [x] (5) Uses the correct graph structure, i.e. ignores one way. (Articulation
  points should use an undirected graph.)
* [x] (5) Displays the selected nodes. (i.e. displays art pts)
* [x] (5) Finds articulation points in all components of the graph.
* [x] (5) Uses the iterative version of the algorithm
* [x] (5) Do they have a report with pseudocode of their algorithms in it?

Completion out of 10 (up to 85):
* [x] (3) Uses one-way roads correctly in the route-finding.
* [x] (3) Highlights the route on the map as well as printing it.
* [x] (4) Removes duplicate roads from the printout, and give the right lengths
  and total length. (3 for removing duplicates, 1 for doing the lengths.)

Challenge out of 20 (up to 100, with 5 spare marks):
* [x] (8) Allows user to select distance or time, and find fastest route, using
  road class and speed limit data, and using an admissible heuristic.
* [x] (2) Can they explain and justify their cost function and heuristic?
  Heuristic must be admissible.
* [x] (5) Takes into account restriction information.
* [ ] (5) Takes into account intersection constraints such as traffic lights to
  prefer routes with fewer lights.

# Testing #

I tested the program by doing a semi-Test-Driven Development style.

- I wrote some simple tests for critical things like the A* algorithm
- wrote a simple random-node-picking algorithm (to make sure the tests work) in
  combination with a few more tests
- built the infrastructure around the algorithm (e.g. UI)
- added tests that require finding the most efficient path
- replaced the random algorithm with the real Dijkstra
- add a bit of code for A*, algorithm
- wrote more tests for extra requirements like one-way roads)
- test manually on the small and large data (fixing bugs and writing new tests
  as needed)

A similar process was used for the articulation points algorithm, although I
did not implement a dummy algorithm before going into the more complex, because
I couldn't think of one.

I did not bother testing UI-related features because they are much harder to
test (and tests were needed in the most complex parts of the program - the UI
was not as complex). I relied on manual testing (by thoroughly checking that
all combinations of inputs match my expectations) for UI.

You can run the tests yourself by opening the 'full-project.zip' file, and
running `./gradlew build test`. (This doesn't work on the uni computers though,
because of the proxy settings - you can run it on any non-uni computer though).

# Pseudocode and Notes #

## Djikstra / A* ##

    start.shortestCostSoFar = 0 // appear first in pq
    pq = new PriorityQueue([start,allOtherNodes...], sorted by shortestCostSoFar)

    while !pq.empty? {

      let n:Node = pq.poll() // n has the shortest shortestCostSoFar

      if !n.hasCheckedChildren
        continue
      n.hasCheckedChildren = true

      foreach c in n.childnodes {
        // don't check again because if we have visited before, then that was the fastest route
        if c.hasCheckedChildren, continue
        pq.add c

        let nodeDist = n.shortestCostSoFar
        let distFromNToC = n.distTo c
        let pathDistSoFar = nodeDist + distFromNToC

        // Is pathDistSoFar better than the current best for this node c?
        if (c.shortestCostSoFar || Infinity) < pathDistSoFar
          continue

        c.shortestCostSoFar = pathDistSoFar
        c.previousNode = n

      }

      if c == end, break
    }

    shortestPath:LinkedList(node -> node.previousNode) = goalNode

### Notes ###

Word redefinitions = {
  "visited" => "hasCheckedChildren"
  "fringe" => "priority queue" // Don't use a stack fringe
}

- If you visit node N, then you want to visit N again later, it will always be
  a longer path the second time around.
  - This is because, at any time (except in the foreach loop), the
    highest-prority node has the shortest path between the start and that node.
    Therefore, you will never find a faster route if you check again.

### Heuristic ###

I use strategy pattern to inject the distance or time heuristic into the
algorithm (see line 98 of MapDataModel and line 72 of NodeState). The distance
is the simple heuristic (it only requires addition). The time heuristic takes a
distance and divides it by the speed limit to get a duration. If the speed
limit isn't available (i.e. for the estimate) it uses the fastest known speed
limit (110km/h).

## Articulation Points ##

    Set<Node> findArticulationPoints()
      articulationPoints = []
      startNode = getAnyNode()
      startNode.count = 0
      *lastCount = 0
      subTreesOfStart = 0

      startNode.neighbours.foreach n ->
        if n.count // note: 0 is truthy (ruby style)
          continue
        subTreesOfStart++
        addArticulationPoints(n, startNode, articulationPoints, lastCount)

      if subTreesOfStart > 1
        add startNode -> articulationPoints

      return articulationPoints

    int addArticulationPoints(node, previousNode, articulationPoints, *lastCount)
      node.count = ++*lastCount
      reachBack = node.count
      node.neighbours.foreach neigh ->
        if neigh == previousNode
          continue
        if neigh.count
          reachBack = min(reachBack, neigh.count)
          continue

        neigh.count = ++*lastCount
        neighReachBack = addArticulationPoints(
          neigh, node, articulationPoints, lastCount
        )
        if neighReachBack < node.count
          reachBack = min(reachBack, neighReachBack)
          continue
        add node -> articulationPoints

      return reachBack

### Iterative Version ###

    // General version of addArticulationPoints
    // (just for working out the iterative version)
    void depthFirstSearchRecursive(node, doStuffBefore, doStuffAfter)
      node.setCount(whatever)
      node.children.foreach c ->
        if c.visited?
          continue
        doStuffBefore.run(node) // do stuff before recursion
        depthFirstSearchRecursive(node)
        doStuffAfter.run(node) // do stuff when recursion finishes
      return minResultFromNeighbours

    // Generalised version turned into generalised iterative version
    // (just for working out the iterative version)
    void depthFirstSearchIterative(node, root)
      stack = [[node: node, previousNode: root]] // array of keyword map

      while !stack.empty?
        state = stack.peek()

        if !state.node.visited?
          node.setCount(whatever)
          state.node.neighbours.foreach neigh ->
            doStuffBefore.run(state.node)
            if neigh.visited? || neigh == state.node
              continue
            add [node: neigh, previousNode: state.node] -> stack
          state.node.visited? = true
          continue

        minResultFromNeighbours = state.node.neighbours.foreach neigh
          whatever()
        doStuffAfter.run(state.node)
        stack.pop()

    // Generalised iterative version turned into proper algorithm
    int addArticulationPointsIterative(node, root, articulationPoints, *lastCount)
      stack = [[node: node, previousNode: root]] // array of keyword map
      root.reachBack = root.count

      while !stack.empty?
        state = stack.peek()

        if !state.node.count
          state.node.count = ++*lastCount
          state.reachBack = state.node.count
          state.node.neighbours.foreach neigh ->
            if neigh == state.previousNode
              continue
            if neigh.count
              state.reachBack = min(state.reachBack, neigh.count)
              continue
            add [node: neigh, previousNode: state.node] -> stack
          continue

        state.node.neighbours.foreach neigh ->
          if neigh == state.previousNode
            continue
          if neigh.reachBack < state.node.count
            state.node.reachBack = min(state.reachBack, neigh.reachBack)
            continue
          add state.node -> articulationPoints

        stack.pop()

