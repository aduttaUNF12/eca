import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static org.graphstream.algorithm.Toolkit.*;

public class AllPairsAllPaths {

	static double results[] = {0,0,0};//to store results for writing to a file
	static int[] bp = {50};
	static Set<Integer> charge = new HashSet<Integer>();//random/pre-fixed charging nodes
	static boolean randCase = false;
	static String styleSheet =
			"node.charger {" +
					"	fill-color: green;"+
					"size: 15px;"+
					"text-size: 50;"+ "text-alignment: at-left;"+
					"}" +
					"edge.path {" +
					"	fill-color: red;" + "size: 15px;"+ "text-size: 20;"+
					"}";
	
	public static void readFromFile(String f, int grid) {
		ArrayList<Integer[]> locs = new ArrayList<Integer[]>();
		try {
			Scanner scanner = new Scanner(new File(f));
			while (scanner.hasNextLine()){
				String data[] = scanner.nextLine().split(",");
				if(!data[0].contains("(")) {
					Integer[] xy = {Integer.parseInt(data[0]),Integer.parseInt(data[1])};
					locs.add(xy);
				}
			}
			scanner.close();
			//locs.remove(locs.size()-1);
			//locs.remove(0);
			for (Integer[] xy: locs) {
				String id = xy[0].toString() + "_" + xy[1].toString();
				charge.add(rowcol2id(id, grid));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// option 1: random; option 2: grid
	public static Graph randGgen(int opt, int grid) {
		if (opt==1) {
			int n = (int) Math.pow(grid+1, 2);
			// random graph generator
			Graph graph = new SingleGraph("Rand");
			Generator gen = new DorogovtsevMendesGenerator();
			gen.addSink(graph);
			gen.begin();
			for(int i=0; i<n; i++) {
				gen.nextEvents();
			}

			gen.end();
			return graph;
		}
		else {
			Graph graph = new SingleGraph("Grid");
			Generator gen = new GridGenerator(true,false);

			gen.addSink(graph);
			gen.begin();

			for(int i=0; i<grid; i++) {
				gen.nextEvents();
			}

			gen.end();
			return graph;
		}
	}

	public static int rowcol2id(String id, int grid) {
		String[] xy = id.split("_");
		int idn = Integer.parseInt(xy[0])*(grid+1) + Integer.parseInt(xy[1]) +1;
		return idn;
	}

	public static void main(String[] args){
		monteCarloOPT();
		//caseStudy();
	}
	
	private static void caseStudy() {
		// TODO Auto-generated method stub
		int n = 15; // for a random graph
		randCase = true;
		generateOPTPathST(3,15,50, 1);
	}

	public static void monteCarloOPT() {
		
		for(int grid=3; grid<=3; grid+=2) {
			for(int charge_percent=5; charge_percent<=5; charge_percent+=5) {
				for(int budget=0; budget<bp.length; budget++) {
					int trial=0;
					int b = (int) Math.ceil((bp[budget]*Math.pow(grid+1, 1)/100));//budget
					String f = new String("Results/OPTenv_"+(grid+1)+"_RC_"+charge_percent+"p_B_"+b+".txt");
					PrintWriter pw=null;
					try {
						pw =  new PrintWriter(new FileWriter( f ));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					while(trial<10) {
						Arrays.fill(results, 0);
						charge.clear();
						if(generateOPTPathST(grid,charge_percent,b, trial)) {
							trial++;
							pw.println(results[0]+","+results[1]+","+results[2]);
						}
						//sleep();
					}
					pw.close();
				}
			}
		}

	}



	//generate the OPTIMAL path here
	public static boolean generateOPTPathST(int g, int cc, int b, int trial) {
		//prelim
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		int grid = g; // generated a square grid of size (grid+1)^2
		int charge_count = (int) Math.ceil((cc*Math.pow(grid+1, 2)/100));//# charging stations
		
		int display=0;
		String envFile = new String("envsetting/envSett_"+(grid+1)+"_RC_"+cc+"p_B_"+b+"_run_"+trial+".txt");
		readFromFile(envFile, grid);// read Brian's generated data
		//System.out.println("CC= "+charge_count+", B= "+b);
		
		//graph and charging station generation
		//budget = bp[budget];
		Graph graph = randGgen(2,grid);
		int n =graph.getNodeCount(); 
		Node source = graph.getNode("0_0");//graph.getNode(rand.nextInt(graph.getNodeCount()-1));
		String goal_node = Integer.toString(grid) + "_" + Integer.toString(grid);
		Node dest = graph.getNode(goal_node);//graph.getNode(rand.nextInt(graph.getNodeCount()-1));
		// System.out.println("Source node ID: "+source.getId()+" and index: "+source.getIndex());
		//charge.add(rowcol2id(source.getId(), grid));
		//charge.add(rowcol2id(dest.getId(), grid));
		/*
		while (charge.size() < charge_count) {
			charge.add(rand.nextInt((int) Math.pow(grid+1, 2)-1)+1);
		}
		*/
		//for case studies on a random graph
		if(randCase) {
			//graph = randGgen(1,grid);
			//n =graph.getNodeCount(); 
			//source = graph.getNode(rand.nextInt(graph.getNodeCount()-1));
			//dest = graph.getNode(rand.nextInt(graph.getNodeCount()-1));
			display=1;
			//findDijkstra(graph,source,dest);
			System.out.println("Done with findDijkstra()");
			//charge.clear();
			//while (charge.size() < charge_count) {
				//charge.add(rand.nextInt(graph.getNodeCount()));
			//}
		}
		
		//System.out.println("The charging stations are located at: "+charge.toString());
		//Iterator<Node> allSources = graph.getNodeIterator();
		//ArrayList<ArrayList<ArrayList<Node>>> apap = new ArrayList<>();

		/*
         // for all pairs in G.
        for (Iterator<Node> it = allSources; it.hasNext(); ) {
            Node n = it.next();
            //Get next source node
            Node source = n;
            Iterator<Node> allDestinations = graph.getNodeIterator();
            while(allDestinations.next() != source){
                //trim destinations view
                ;
            }
            while(allDestinations.hasNext()){
                Node dest = allDestinations.next();
                System.out.print(n.getId() + " to " + dest.getId() +": ");
                AllPathsGS allpaths = new AllPathsGS(graph, source, dest);
                ArrayList<ArrayList<Node>> paths = allpaths.getPaths();
                apap.add(paths);
            }


        }
		 */

		// for a single pair in Graph (random S and G)
		System.out.print(source.getId() + " to " + dest.getId() +": ");
		AllPathsGS allpaths = new AllPathsGS(graph, source, dest, b, charge);
		double startT = System.nanoTime()/Math.pow(10, 6);
		ArrayList<ArrayList<Node>> paths = allpaths.getPaths();
		double endT = System.nanoTime()/Math.pow(10, 6);
		//System.out.println("Start time: "+startT+" End time is: "+endT+" & Time taken (sec.)is : "+ (double)((endT-startT)));
		results[0] = (double)((endT-startT));//storing time in sec.

		//go through the paths to find the optimal one
		int opt_cost = Integer.MAX_VALUE;
		ArrayList<Node> opt_path = new ArrayList<Node>();
		for(ArrayList<Node> p: paths) {
			//Collections.reverse(p);
			int cost = 0;//cost of this particular path
			int b_rem = b;//remaining budget
			int stations = 0;
			for(Node k: p) {
				if (cost!=0) {//for the START node
					b_rem--;
				}
				if (b_rem<0) {
					cost = Integer.MAX_VALUE;
					break;//this path is no good.
				}
				else {
					cost++;
				}
				if(charge.contains(rowcol2id(k.getId(), grid))) {
				//if(charge.contains(k.getId())) {
					b_rem+=b; stations++;
					if(b_rem>b) b_rem=b;
				}

			}
			if(cost<opt_cost) {
				opt_cost = cost;
				opt_path = (ArrayList<Node>) p.clone();
				results[1] = p.size()-1;
				results[2] = stations;
			}
		}
		System.out.println("Grid size: "+(grid+1)+", OPTIMAL cost is: "+ opt_cost+ " and B= "+b+" and OPTIMAL path= "+opt_path.toString());

		if (display==1) {
			FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.HD1080);
			pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
			graph.setAttribute("ui.stylesheet", styleSheet);
			graph.display();
			String label = "Start";//"B="+b;
			//if(!randCase) 
				graph.getNode("0_0").setAttribute("ui.label", label);
				
			//for (int k=0; k<opt_path.size()-1;k++) {
			//Node first = graph.getNode(opt_path.get(k).getId());
			//Node second = graph.getNode(opt_path.get(k+1).getId());
			//graph.getEdge(arg0)
			//next.setAttribute("ui.class", "marked");
			//sleep();
			//}
			Collection<Edge> ite = graph.getEdgeSet();
			for(Edge e: ite) {
				//System.out.println("Edge id is: "+e.get);
				Node first = e.getNode0();
				Node second = e.getNode1();
				if (opt_path.contains(first) && opt_path.contains(second) && Math.abs(opt_path.indexOf(first)-opt_path.indexOf(second))==1) {
					e.setAttribute("ui.class", "path");
					//sleep();
				}
				if(charge.contains(rowcol2id(first.getId(), grid))) {
				//if(charge.contains(first.getId())) {
					first.setAttribute("ui.class", "charger");
				}
				if(charge.contains(rowcol2id(second.getId(), grid))) {
				//if(charge.contains(second.getId())) {
					second.setAttribute("ui.class", "charger");
				}
			}
			try {
				pic.writeAll(graph, "illust.png");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// decides whether a valid path is found or not
		if(opt_path.size()==0)
			return false;
		else
			return true;
	}
	private static void findDijkstra(Graph graph, Node source, Node dest) {
		// for GraphStream 1.3: 
		// https://data.graphstream-project.org/api/gs-algo/current/org/graphstream/algorithm/Dijkstra.html
		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result", "length");
		//graph.setAttribute("ui.stylesheet", styleSheet);
 		//graph.display();
 		dijkstra.init(graph);
 		dijkstra.setSource(graph.getNode(source.getId()));
 		dijkstra.compute();
 	// Print the lengths of all the shortest paths
 		 for (Node node : graph)
 		     System.out.printf("%s->%s:%6.2f%n", dijkstra.getSource(), node, dijkstra.getPathLength(node));
 		System.out.println("Found path: "+dijkstra.getPath(graph.getNode(dest.getId())));
 		// A shorter but less efficient way to do the same thing
 		List<Node> list2 = dijkstra.getPath(graph.getNode(dest.getId())).getNodePath();
 		System.out.println("Source node: "+source.getId()+" Dest node: "+dest.getId()+" Dijkstra path: "+list2.toString());
 		
 		Collection<Edge> ite = graph.getEdgeSet();
 		// Color in red all the edges in the shortest path tree
		for(Edge e: ite) {
			//System.out.println("Edge id is: "+e.get);
			Node first = e.getNode0();
			Node second = e.getNode1();
			if (list2.contains(first) && list2.contains(second)) {
				System.out.println("DIJK edge");
				e.setAttribute("ui.class", "path");
				//sleep();
			}
		}
	}

	protected static void sleep() {
		try { Thread.sleep(10000); } catch (Exception e) {}
	}
}
