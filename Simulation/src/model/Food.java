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
		super();
		this.x = x;
		this.y = y;
	}

    @Override
	public void step(SimState state) {
		Insectes i = (Insectes) state;
		if (nbFood <= 0) {
			nbFood = Constants.MAX_FOOD;
			Int2D pos = i.getNewPos();
			System.out.println("rellocating food ");
			System.out.println("old " + x + " / " + y);
			System.out.println("new " + pos.getX() + " / " + pos.getY());
			this.x = pos.x;
			this.y = pos.y;
			i.yard.setObjectLocation(this, pos);
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
}
