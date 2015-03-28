package algorithms;

import graphEntity.edge;
import graphEntity.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class dijkstra {

	  private final List<node> nodes;
	  private final List<edge> edges;
	  private Set<node> settledNodes;
	  private Set<node> unSettledNodes;
	  private Map<node, node> predecessors;
	  private Map<node, Integer> distance;

	  public dijkstra (List<node> _nodes, List<edge> _edges) {
	    // create a copy of the array so that we can operate on this array
	    this.nodes = new ArrayList<node>(_nodes);
	    this.edges = new ArrayList<edge>(_edges);
	  }
	  
	  public void reset (){
	    this.settledNodes = null;
	    this.unSettledNodes = null;
	    this.predecessors = null;
	    this.distance = null;
	  }

	  public void execute(node source) {
	    settledNodes = new HashSet<node>();
	    unSettledNodes = new HashSet<node>();
	    distance = new HashMap<node, Integer>();
	    predecessors = new HashMap<node, node>();
	    distance.put(source, 0);
	    unSettledNodes.add(source);
	    while (unSettledNodes.size() > 0) {
	      node node = getMinimum(unSettledNodes);
	      settledNodes.add(node);
	      unSettledNodes.remove(node);
	      findMinimalDistances(node);
	    }
	  }

	  private void findMinimalDistances(node node) {
	    List<node> adjacentNodes = getNeighbors(node);
	    for (node target : adjacentNodes) {
	      if (getShortestDistance(target) > getShortestDistance(node)
	          + getDistance(node, target)) {
	        distance.put(target, getShortestDistance(node)
	            + getDistance(node, target));
	        predecessors.put(target, node);
	        unSettledNodes.add(target);
	      }
	    }

	  }

	  private int getDistance(node node, node target) {
	    for (edge edge : edges) {
	      if (edge.getSender().equals(node)
	          && edge.getReceiver().equals(target)) {
	        return edge.getWeight();
	      }
	    }
	    throw new RuntimeException("Should not happen");
	  }

	  private List<node> getNeighbors(node node) {
	    List<node> neighbors = new ArrayList<node>();
	    for (edge edge : edges) {
	      if (edge.getSender().equals(node)
	          && !isSettled(edge.getReceiver())) {
	        neighbors.add(edge.getReceiver());
	      }
	    }
	    return neighbors;
	  }

	  private node getMinimum(Set<node> vertexes) {
	    node minimum = null;
	    for (node vertex : vertexes) {
	      if (minimum == null) {
	        minimum = vertex;
	      } else {
	        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
	          minimum = vertex;
	        }
	      }
	    }
	    return minimum;
	  }

	  private boolean isSettled(node vertex) {
	    return settledNodes.contains(vertex);
	  }

	  private int getShortestDistance(node destination) {
	    Integer d = distance.get(destination);
	    if (d == null) {
	      return Integer.MAX_VALUE;
	    } else {
	      return d;
	    }
	  }

	  /*
	   * This method returns the path from the source to the selected target and
	   * NULL if no path exists
	   */
	  public LinkedList<node> getPath(node target) {

	    LinkedList<node> path = new LinkedList<node>();
	    node step = target;
	    // check if a path exists
	    if (predecessors.get(step) == null) {
	      return null;
	    }
	    path.add(step);
	    while (predecessors.get(step) != null) {
	      step = predecessors.get(step);
	      path.add(step);
	    }

	    // Put it into the correct order
	    Collections.reverse(path);
	    return path;
	  }

	} 