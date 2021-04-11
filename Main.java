import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.Component;
import java.awt.Canvas;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.applet.*;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
public class Main implements Runnable{
	public static Node Room[][];
	static int RoomSize;
	public static int col;
	public static int row;
	public static Stack <Node> Open = new Stack<>();
	public static Stack <Node> Closed = new Stack<>();
	public static Stack <Node> BestPath = new Stack<>();
	public static Stack <Node> RC = new Stack<>();
	public static Stack <Node> RCclosed = new Stack<>();
	public static Stack <Node> RCP = new Stack <>();
	public static int FullTank = 0;
	public static int RCpercentage = 0;
	public static int Cost = 0;
	static GUI GUI;
	public static double timeStart = 0;
	public static double timeEnd = 0;
	public static double timeRun = 0;
	public static int RCindex = 0;
	public static int StationCounter = 0;
	public static double results[] = {0,0,0};
	
	public static int Trials = 10;
	public static int RoomSizeV1[] = {20, 30, 40 ,50};
	public static int RoomSizeV2[] = {100, 200, 300, 400, 500};
	public static int TrialRCs[] = {5, 10, 15};
	public static int TrialBudgets[] = {20, 35, 50};
	
	public static int TrialCounter = 0;
	public static int B = FullTank;
	public static void main(String[] args) {
		new Thread (new Main()).start();
	}//End of main
	public static Node NextStation(Node RCp, Node End) {
		Node rc;
		RCp.SetHn(End);
		if(HDist(RCp, End) <= FullTank) {
			//System.out.println("RC Node Hit: "+RCp.GetHn());
			rc = End;
		} else {
			if(RC.size() == 0) {
				//System.out.println("No more Recharge Stations");
				return null;
			}
			Node Temp = RC.get(0);
			Temp.SetrcH(HDist(RCp, RC.get(0)) + 2*HDist(RC.get(0), End) - HDist(RCp, End));
			for(int i = 0; i < RC.size(); i++) {
				RC.get(i).SetrcH(HDist(RCp, RC.get(i)) + 2*HDist(RC.get(i), End) - HDist(RCp, End));
				//System.out.println(RC.get(i).GetrcH());
				if(Temp.GetrcH() >= RC.get(i).GetrcH() && HDist(RCp, RC.get(i)) <= B) {
					Temp = RC.get(i);
					RCindex = i;
				}
			}
			rc = Temp;
			rc.SetColor(Color.PINK);
			//System.out.println("RC Node Hit: "+rc.GetrcH());
		}
		return rc;
	}
	public static double HDist(Node Start, Node End) {
		double H;
		int D = 1;
		int D2 =1;
		int dx = Math.abs(Start.GetCol() - End.GetCol());
		int dy = Math.abs(Start.GetRow() - End.GetRow());
		H = D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
		//H = Math.max(Math.abs(End.GetCol() - Start.GetCol()), Math.abs(End.GetRow() - Start.GetRow()));
		return H;
	}
	public static Node PathFind(Node Current, Node End) {
		int TempIndex = 0;
		Node Temp = null;
		Node Initial = Current;
		Open.push(Current);
		Current.SetCost(0);
		while(Open.empty() == false) {//Start of A*
			if(B < 0){
				//System.out.println("No solution under cost.");
				return null;
			}
			Current = Open.get(0);
			for(int i = 0; i < Open.size(); i++) {
				Open.get(i).SetFn();
				Current.SetFn();
				if(Open.get(i).GetHn() < Current.GetHn()) {
					Current = Open.get(i);
					TempIndex = i;
				}
			}
			if (Current == End) {
				Temp = Current;
				BestPath.push(Current);
				while(Current.GetPrevious() != Initial) {
					Cost++;
					BestPath.push(Current.GetPrevious());
					Current = Current.GetPrevious();
					//GUI.repaint();
				}
				return Temp;
			}
			if(Current.GetPrevious() != null) {
				Current.SetCost(Current.GetPrevious().GetCost() + 1);
			}else {
				Current.SetCost(0);
			}
			if(RC.contains(Current)) {
				B = FullTank;
			}else {
				B--;
			}
			//System.out.println("Fuel: "+(B));
			Temp = Open.get(TempIndex);
			Open.remove(TempIndex);
			Temp.SetColor(Color.CYAN);
			Closed.push(Temp);
			for(int i = 0; i < 8; i++){
				if(Current.GetNeighbors(i) == null) continue;
				if(Current.GetNeighbors(i).GetObstructed() == true) continue;
				Current.GetNeighbors(i).SetGn(Current.GetGn() + 1);
				if (Open.contains(Current.GetNeighbors(i))) {
						continue;
				}else {
					if(Closed.contains(Current.GetNeighbors(i))) {
						continue;
					}
					Current.GetNeighbors(i).SetColor(Color.green);
					Open.push(Current.GetNeighbors(i));
					//GUI.repaint();
				}
				Current.GetNeighbors(i).SetPrevious(Current);
			}
			/*try {
				TimeUnit.MILLISECONDS.sleep(60);
			} catch (InterruptedException e) {
				e.printStackTrace();
	        	}*/
			//GUI.revalidate();
			//GUI.repaint();
		}//end of Search
		return null;
	}
	public void run(){
		Scanner scan = new Scanner(System.in);
		int input = 0;
		while(true) {
			Open.clear();
			Closed.clear();
			BestPath.clear();
			System.out.println("Enter 4 to begin Data Collection");
			input = scan.nextInt();
			for(int N = 0; N < RoomSizeV1.length; N++) {
				RoomSize = RoomSizeV1[N];
				for(int W = 0; W < TrialRCs.length; W++) {
					RCpercentage = TrialRCs[W];
					for(int T = 0; T < TrialBudgets.length; T++) {
						FullTank = (int) Math.ceil((TrialBudgets[T]*Math.pow(RoomSize, 1)/100));//budget
						String f = new String("Results/env_"+RoomSize+"_RC_"+RCpercentage+"p_B_"+FullTank+".txt");
						BufferedWriter pw=null;
						try {
							pw =  new BufferedWriter(new FileWriter(f));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int z = 0;
						while(z < Trials){
							timeStart = System.currentTimeMillis();
							Room = new Node[col][row];
							Node Start = null;
							Node End = null;
							Node RCp = null;
							Boolean EndFound = false;
							Room = RoomLayot.RoomLayout(input);
							col = RoomSize;
							row = RoomSize;
							for(int i = 0; i < col; i++) {
								for(int j = 0; j < row; j++) {//Sets Start and End Nodes From room layout
									if(Room[i][j].GetColor() == Color.GREEN) {
										Start = Room[i][j];
									}else if(Room[i][j].GetColor() == Color.RED) {
										End = Room[i][j];
									}
									if(Room[i][j].GetColor() == Color.ORANGE) {
										RC.push(Room[i][j]);
									}
								}
							}
							//GUI = new GUI();
							Start.SetHn(End);
							Open.push(Start);
							for(int C = 0; C < col; C++) {
								for(int R = 0; R < row; R++) {
									Room[C][R].SetHn(End);//Sets Heuristic Values
								}
							}
							RCp = Start;
							Node Current = Start;
							Node rc = null;
							B = FullTank;
							outer:
								while(rc != End) {//Main Loop
									rc = NextStation(RCp, End);
									if(rc == null) {
										EndFound = false;
										break outer;
									}
									for(int C = 0; C < col; C++) {
										for(int R = 0; R < row; R++) {
											Room[C][R].SetHn(rc);//Sets Heuristic Values
										}
									}
									Current.SetCost(0);
									Current = PathFind(Current, rc);
									if(Current == null) {
										EndFound = false;
										break outer;
									}
									RCP.push(RCp);
									if(rc == End) {
										timeEnd = System.currentTimeMillis();
										timeRun = timeEnd - timeStart;
										for(int n = 0; n < BestPath.size(); n++) {
											if(RCP.contains(BestPath.get(n))) {
												StationCounter++;
											}
										}
										//System.out.println("Found End");
										//System.out.println("Best Path Cost  = "+ BestPath.size());
										//System.out.println("Run Time = " + timeRun);
										//System.out.println("Stations Visted = "+StationCounter);
										results[0] = timeRun;
										results[1] = BestPath.size();
										results[2] = StationCounter;
										StationCounter = 0;
										EndFound = true;
										try {
											//System.out.println(results[0]+","+results[1]+","+results[2]);
											pw.write(results[0]+","+results[1]+","+results[2]+"\n");
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										break outer;
									}
									RCp = rc;
									Current = rc;
									RC.remove(RCindex);
									B = FullTank;
									Open.clear();
									Closed.clear();
								}
							RC.clear();
							Closed.clear();
							Open.clear();
							RCP.clear();
							RCclosed.clear();
							BestPath.clear();
							timeEnd = 0;
							timeEnd = 0;
							timeRun = 0;
							Cost = 0;
							TrialCounter++;
							if(EndFound == false) {
								//System.out.println("No Solution");
							}else {
								RoomLayot.FileClose();
								z++;
							}
						}
						TrialCounter = 0;
						try {
							pw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Trials Complete: env_"+RoomSize+"_RC_"+RCpercentage+"p_B_"+FullTank+".txt");
					}
				}
			}
			System.out.println("Enter 0 to quit or anything else to run the program again");
			int quit = scan.nextInt();
			if (quit == 0) {
				scan.close();
				System.exit(0);
			}
		}
	}
}