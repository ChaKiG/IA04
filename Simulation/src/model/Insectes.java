package model;

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Insectes extends SimState {
	private static final long serialVersionUID = 1L;
	
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

	private void addInsectes() {
		for(int  i  =  0;  i  <  Constants.NUM_INSECT;  i++) {
			Int2D location = new Int2D(random.nextInt(yard.getWidth()), random.nextInt(yard.getHeight()) );
			Insecte e = new Insecte(location.x, location.y);
			yard.setObjectLocation(e, location);
			schedule.scheduleRepeating(e);
		}  
	}
	
	private void addFoods() {  
		for(int  i  =  0;  i  <  Constants.NUM_FOOD_CELL;  i++) {
			Int2D location = new Int2D(random.nextInt(yard.getWidth()), random.nextInt(yard.getHeight()) );
			Food f = new Food(location.x, location.y);
			yard.setObjectLocation(f, location);
		}
	}
	
	public void relocateFood(Food f) {
		Int2D location = new Int2D(random.nextInt(yard.getWidth()), random.nextInt(yard.getHeight()) );
		yard.setObjectLocation(f, location);
		f.newFood(location);
	}

}
