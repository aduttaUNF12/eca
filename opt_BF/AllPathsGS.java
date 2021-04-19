/******************************************************************************
 * Graphstream.org version of AllPaths.java from
 * https://introcs.cs.princeton.edu/java/45graph/AllPaths.java.html
 *
 ******************************************************************************/

import org.graphstream.graph.*;

import java.util.*;


public class AllPathsGS {

    private Stack<Node> path = new Stack<>();
    private Set<Node> onPath = new HashSet<>();
    private int b;
    private Set<Integer> charge = new HashSet<Integer>();
    private ArrayList<ArrayList<Node>> paths = new ArrayList<>();
    private int opt_cost = Integer.MAX_VALUE;
    
    public AllPathsGS(Graph g, Node s, Node t, int budget, Set<Integer> stations) {
    	b=budget;
    	charge = stations;
        enumerate(g, s, t);
        System.out.println("Found " + paths.size() + " paths.");
    }

    public ArrayList<ArrayList<Node>> getPaths(){
        return paths;
    }

    // use DFS
    private void enumerate(Graph G, Node v, Node t) {

        // add node v to current path from s
        path.push(v);
        onPath.add(v);

        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t)) {
            //Copy the stack so the reference stored into paths doesn't get changed
            Node[] anArray = new Node[path.size()];
            path.copyInto(anArray);
            ArrayList<Node> nAL = new ArrayList<>(Arrays.asList(anArray));
            //if(checkValid(nAL,G,v,t))
            	paths.add(nAL);
        }
            // consider all neighbors that would continue path without repeating a node
        else {
            Iterator<Node> neighbors = v.getNeighborNodeIterator();
                while(neighbors.hasNext()){
                    Node w = neighbors.next();
                    if(!onPath.contains(w)){
                        enumerate(G, w, t);
                    }
                }
        }
        // done exploring from v, so remove from path
        path.pop();
        onPath.remove(v);

    }
    
	private boolean checkValid(ArrayList<Node> nAL, Graph G, Node s, Node t) {
		// TODO Auto-generated method stub
		int cost=0;
		int b_rem = b;//remaining budget
		int stations = 0;
		int grid = (int) Math.sqrt(G.getNodeCount());
		for(Node k: nAL) {
			if (cost!=0) {//for the START node
				b_rem--;
			}
			if(charge.contains(rowcol2id(k.getId(), grid))) {
				b_rem+=b; stations++;
				if(b_rem>b) b_rem=b;
			}

			if (b_rem<0) {
				cost = Integer.MAX_VALUE;
				return false;//this path is no good.
			}
			else
				cost++;
		}
		if(cost-1<opt_cost) {
			opt_cost = cost-1;
			paths.clear();
		}
		return true;
	}
	
	public static int rowcol2id(String id, int grid) {
		String[] xy = id.split("_");
		int idn = Integer.parseInt(xy[0])*(grid+1) + Integer.parseInt(xy[1]) +1;
		return idn;
	}
}
