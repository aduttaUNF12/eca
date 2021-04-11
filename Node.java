import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;

public class Node {
	private int Col;
	private int Row;
	private boolean Obstructed;
	private Color Color;
	private double Fn;
	private double Hn;
	private double Gn;
	private double rcH;
	private int Width;
	private int Height;
	private int size = Main.RoomSize;
	private Node[] Neighbors = new Node[8];
	private Node Previous;
	private int Cost = 0;
	
	public Node(int col, int row) {
		this.SetCol(col);
		this.SetRow(row);
		this.SetObstructed(false);
		this.SetColor(Color.white);
		this.SetWidth(size);
		this.SetHeight(size);
		//SetGn(0);
	}
	
	public void NodePaint(Graphics g) {
		g.setColor(Color);
		g.fillRect(Col, Row, Width, Height);
	}
	
	public void SetCol(int col) {
		Col = col;
	}
	public int GetCol() {
		return Col;
	}
	public void SetRow(int row) {
		Row = row;
	}
	public int GetRow() {
		return Row;
	}
	public void SetObstructed(boolean obstructed) {
		Obstructed = obstructed;
	}
	public boolean GetObstructed() {
		return Obstructed;
	}
	public void SetColor(Color color) {
		Color = color;
	}
	public Color GetColor() {
		return Color;
	}
	public void SetWidth(int size) {
		Width = size;
	}
	public int GetWidth() {
		return Width;
	}
	public void SetHeight(int size) {
		Height = size;
	}
	public int GetHeight() {
		return Height;
	}
	public void SetNeighbors(Node[][] Room) {
		int i = this.Col;
		int j = this.Row;
			if(j!=0) {
				Neighbors[0] = Room[i][j-1];
			}else Neighbors[0] = null;
			if(j!=0 && i!= size-1) {
				Neighbors[1] = Room[i+1][j-1];
			}else Neighbors[1] = null;
			if(i!=size-1) {
				Neighbors[2] = Room[i+1][j];
			}else Neighbors[2] = null;
			if(i!=size-1 && j!=size-1) {
				Neighbors[3] = Room[i+1][j+1];
			}else Neighbors[3] = null;
			if(j!=size-1) { 
				Neighbors[4] = Room[i][j+1];
			}else Neighbors[4] = null;
			if(j!=size-1 && i!=0) {
				Neighbors[5] = Room[i-1][j+1];
			}else Neighbors[5] = null;
			if(i!=0) { 
				Neighbors[6] = Room[i-1][j];
			}else Neighbors[6] = null;
			if(i!=0 && j!= 0) {
				Neighbors[7] = Room[i-1][j-1];
			}else Neighbors[7] = null;
	}
	public Node GetNeighbors(int i) {
		return Neighbors[i];
	}
	public void SetPrevious(Node Current) {
		Previous = Current;
	}
	public Node GetPrevious() {
		return Previous;
	}
	public void SetFn() {
		Fn = this.Hn + this.Gn;
	}
	public double GetFn() {
		return Fn;
	}
	public void SetHn(Node End) {
		int D = 1;
		int D2 =1;
		int dx = Math.abs(this.GetCol() - End.GetCol());
		int dy = Math.abs(this.GetRow() - End.GetRow());
		Hn = D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
	}
	public double GetHn() {
		return Hn;
	}
	public void SetGn(double i) {
		Gn = i;
	}
	public double GetGn() {
		return Gn;
	}
	public void SetrcH(double H) {
		rcH = H;
	}
	public double GetrcH() {
		return rcH;
	}
	public void SetCost(int C) {
		Cost = C;
	}
	public int GetCost() {
		return Cost;
	}
}
