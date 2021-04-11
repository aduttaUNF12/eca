//import java.util.*;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	int RoomSize = Main.RoomSize;
	int col = RoomSize;
	int row = RoomSize;
	int Border = 5;
	public GUI(){
		this.setTitle("Energy Constrained A*");
		this.setSize((RoomSize)*10+5, (RoomSize)*10+27);
		this.setVisible(true);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Window Window = new Window();
		this.setContentPane(Window);
	}
	
	public class Window extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.darkGray);
			g.fillRect(0, 0, (RoomSize)*10+5, (RoomSize)*10+5);
			for(int i = 0; i < col; i++) {
				for(int j = 0; j < row; j++) {
					//Main.Room[i][j].NodePaint(g);
					g.setColor(Main.Room[i][j].GetColor());
					g.fillRect(Border+i*10, Border+j*10, 6, 6);
				}
			}
			for(int i = 0; i < Main.Open.size(); i++) {
				g.setColor(Main.Open.get(i).GetColor());
				int x = Main.Open.get(i).GetCol();
				int y = Main.Open.get(i).GetRow(); 
				g.fillRect(Border+x*10, Border+y*10, 6, 6);
			}
			for(int i = 0; i < Main.Closed.size(); i++) {
				g.setColor(Main.Closed.get(i).GetColor());
				int x = Main.Closed.get(i).GetCol();
				int y = Main.Closed.get(i).GetRow(); 
				g.fillRect(Border+x*10, Border+y*10, 6, 6);
			}
			for(int i = 0; i < Main.BestPath.size(); i++) {
				g.setColor(Color.BLUE);
				int x = Main.BestPath.get(i).GetCol();
				int y = Main.BestPath.get(i).GetRow(); 
				g.fillRect(Border+x*10, Border+y*10, 6, 6);
			}
			for(int i = 0; i < Main.RC.size(); i++) {
				g.setColor(Main.RC.get(i).GetColor());
				g.setColor(Color.ORANGE);
				int x = Main.RC.get(i).GetCol();
				int y = Main.RC.get(i).GetRow(); 
				g.fillRect(Border+x*10, Border+y*10, 6, 6);
			}
			for(int i = 0; i < Main.RC.size(); i++) {
				if(Main.RC.get(i).GetColor() == Color.PINK) {
					g.setColor(Main.RC.get(i).GetColor());
					int x = Main.RC.get(i).GetCol();
					int y = Main.RC.get(i).GetRow();
					g.fillRect(Border+x*10, Border+y*10, 6, 6);
				}
			}
			for(int i = 0; i < Main.RCP.size(); i++) {
				g.setColor(Main.RCP.get(i).GetColor());
				g.setColor(Color.BLACK);
				int x = Main.RCP.get(i).GetCol();
				int y = Main.RCP.get(i).GetRow(); 
				g.fillRect(Border+x*10, Border+y*10, 6, 6);
			}
		}
	}	
}
