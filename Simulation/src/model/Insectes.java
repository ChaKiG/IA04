package model;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Insectes extends SimState {
	private static final long serialVersionUID = 1L;
	private int nbInsectes = Constants.NUM_INSECT;
	public SparseGrid2D yard = new SparseGrid2D(Constants.GRID_SIZE, Constants.GRID_SIZE);
	
	
	public Insectes(long seed) {
		super(seed);
	}
	
	public void start() {
		System.out.println("Simulation started");
		super.start();
		yard.clear();
		addInsectes();
		addFoods();
	}

	public int getNbInsectes(){ return nbInsectes; }
	
	private void addInsectes() {
		for(int  i  =  0;  i  <  Constants.NUM_INSECT;  i++) {
			Int2D location = getNewPos();
			Insecte e = new Insecte(location.x, location.y);
			yard.setObjectLocation(e, location);
			schedule.scheduleRepeating(e);
		}
		nbInsectes = Constants.NUM_INSECT;
	}
	
	private void addFoods() {  
		for(int  i  =  0;  i  <  Constants.NUM_FOOD_CELL;  i++) {
			Int2D location = getNewPos();
			Food f = new Food(location.x, location.y);
			yard.setObjectLocation(f, location);
			schedule.scheduleRepeating(f);
		}
	}
	
	public Int2D getNewPos() {
		int x = this.random.nextInt(Constants.GRID_SIZE);
		int y = this.random.nextInt(Constants.GRID_SIZE);
		return new Int2D(x, y);
	}
	
	public void tuerInsecte(Insecte i) {
		nbInsectes--;
	}

}
