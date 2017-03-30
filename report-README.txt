# Report #






IMPORTANT NOTES:
- I used the dependency injection framework called Google Guice
- The java code can be found inside the full-project zip
    - The main java is under src/main/java
    - The test java is under src/test/java
- You can run the program just by executing the jar file






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
* [ ] (5) Uses the correct graph structure, i.e. ignores one way. (Articulation
  points should use an undirected graph.)
* [ ] (5) Displays the selected nodes. (i.e. displays art pts)
* [ ] (5) Finds articulation points in all components of the graph.
* [ ] (5) Uses the iterative version of the algorithm
* [ ] (5) Do they have a report with pseudocode of their algorithms in it?

Completion out of 10 (up to 85):
* [x] (3) Uses one-way roads correctly in the route-finding.
* [x] (3) Highlights the route on the map as well as printing it.
* [x] (4) Removes duplicate roads from the printout, and give the right lengths
  and total length. (3 for removing duplicates, 1 for doing the lengths.)

Challenge out of 20 (up to 100, with 5 spare marks):
* [ ] (8) Allows user to select distance or time, and find fastest route, using
  road class and speed limit data, and using an admissible heuristic.
* [ ] (2) Can they explain and justify their cost function and heuristic?
  Heuristic must be admissible.
* [ ] (5) Takes into account restriction information.
* [ ] (5) Takes into account intersection constraints such as traffic lights to
  prefer routes with fewer lights.

# Pseudocode and Notes #

## Djikstra / A* ##

    start.shortestDistanceSoFar = 0 // appear first in pq
    pq = new PriorityQueue([start,allOtherNodes...], sorted by shortestDistanceSoFar)

    while !pq.empty? {

      let n:Node = pq.poll() // n has the shortest shortestDistanceSoFar

      assert !n.hasCheckedChildren
      n.hasCheckedChildren = true

      foreach c in n.childnodes {
        // don't check again because if we have visited before, then that was the fastest route
        if c.hasCheckedChildren, continue
        pq.add c

        let nodeDist = n.shortestDistanceSoFar
        let distFromNToC = n.distTo c
        let pathDistSoFar = nodeDist + distFromNToC

        // Is pathDistSoFar better than the current best for this node c?
        if (c.shortestDistanceSoFar || Infinity) < pathDistSoFar
          continue

        c.shortestDistanceSoFar = pathDistSoFar
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
        n.count = ++*lastCount
        subTreesOfStart++
        addArticulationPoints(n, startNode, articulationPoints, lastCount)

      if subTreesOfStart > 1
        add startNode -> articulationPoints

      return articulationPoints

    int addArticulationPoints(node, previousNode, articulationPoints, *lastCount)
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

