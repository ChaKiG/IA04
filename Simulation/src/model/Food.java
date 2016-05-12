package model;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

public class Food implements Steppable {
	private static final long serialVersionUID = 1L;
	
	private int x;
	private int y;
	private int nbFood = Constants.MAX_FOOD;
	
	public Food(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void step(SimState step) {
		Insectes i = (Insectes) step;
		if (nbFood == 0) {
			i.relocateFood(this);
		}
	}
	
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getNbFood() { return nbFood; }
	
	public boolean removeFood() { 
		if (nbFood > 0) {
			nbFood--;
			return true;
		}
		return false;
	}
	
	
	public void newFood(Int2D pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.nbFood = Constants.MAX_FOOD;	
	}
}
