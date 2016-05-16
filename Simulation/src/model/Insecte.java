package model;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Insecte implements Steppable {
	private static final long serialVersionUID = 1L;
	public int x, y;
	private int DISTANCE_DEPLACEMENT = 1;
	private int DISTANCE_PERCEPTION = 1;
	private int CHARGE_MAX = 1;
	private int energie = Constants.MAX_ENERGY;
	private int charge = 0;
	
	public Insecte(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		
		Random r = new Random();
		int nb = Constants.CAPACITY - 3;
		int nb2 = r.nextInt(Math.min(nb, Constants.MAX_LOAD)+1);
		nb -= nb2;
		CHARGE_MAX += nb;
		nb2 = r.nextInt(nb+1);
		nb -= nb2;
		DISTANCE_PERCEPTION += nb2;
		DISTANCE_DEPLACEMENT += nb;
		
		
		System.out.println("percois : " + DISTANCE_PERCEPTION);
		System.out.println("deplace : " + DISTANCE_DEPLACEMENT);
		System.out.println("charge : " + CHARGE_MAX);
	}
	
	
	
	
	
    @Override
	public void step(SimState state) {
		Insectes insectes = (Insectes) state;
		
		if (this.energie == 0) {
			insectes.yard.remove(this);
			insectes.schedule.scheduleRepeating(this).stop(); 
		} else {
			List<Food> perception = percevoir(insectes);
			
			if (manger(perception))
				return;
			if (charger(perception))
				return;
			move(perception, insectes);
		}
	}
    
    private List<Food> percevoir(Insectes insectes) {
    	List<Food> l = new ArrayList<Food>();
    	for (int i = 0-DISTANCE_PERCEPTION; i <= DISTANCE_PERCEPTION; i++) {
    		for (int j = 0-DISTANCE_PERCEPTION; j <= DISTANCE_PERCEPTION; j++) {
        		Bag b = insectes.yard.getObjectsAtLocation(x+i, y+j);
        		if ( b != null && !b.isEmpty()) {
        			for (Object o : b) {
        				if (o.getClass() == Food.class) {
        					l.add((Food) o);
    					}
    				}
    			}
    		}
    	}
    	System.out.println("insecte percois " + l.size() + " food");
    	return l;
    }
    
    private boolean manger(List<Food> l) {
		if ( ( energie <= Constants.MAX_ENERGY - Constants.FOOD_ENERGY && charge == this.CHARGE_MAX)
			 || ( energie <= 2 &&(charge >= 1 || !l.isEmpty())) ) {
			for (Food f : l) {
				if (getDistance(f.getX(), f.getY()) == 1) {
		    		if (f.removeFood()) {
			        	this.energie += Constants.FOOD_ENERGY;
			        	System.out.println("manger food a cote");
						return true;
		    		}
				}
			}
			if (charge >= 1) {
	    		this.charge--;
	        	this.energie += Constants.FOOD_ENERGY;
	        	System.out.println("manger food charge");
				return true;
			}
		}
		return false;
    }
    
    private boolean charger(List<Food> l) {
    	if (charge < this.CHARGE_MAX && !l.isEmpty()) {
			for (Food f : l) {
				if (getDistance(f.getX(), f.getY()) == 0) {
		    		if (f.removeFood()) {
			        	this.charge++;
			        	System.out.println("charger");
						return true;
		    		}
				}
			}
    	}
    	return false;
    }
    
    private void move(List<Food> l, Insectes insectes) {
    	System.out.println("deplacer");
    	boolean d = false;
    	int s = Constants.GRID_SIZE;
    	if (!l.isEmpty()) {
			Food bestC = l.get(0);
	    	Food bestM = l.get(0);
	    	for (Food f : l) {
				if (getDistance(f.getX(), f.getY()) == 0) {
		    		if (f.getNbFood() > bestC.getNbFood() && getDistance(f.getX(), f.getY()) <= this.DISTANCE_DEPLACEMENT) {
		    			bestC = f;
		    		}
		    		if (f.getNbFood() > bestM.getNbFood() && getDistance(f.getX(), f.getY()) <= this.DISTANCE_DEPLACEMENT+1) {
		    			bestM = f;
		    		}
				}
			}
	    	if (this.energie <= 2) {
	        	System.out.println("deplacer pour manger");
	    		//aller a cote de la case bestM pour manger au prochain tour
	    		if (bestM.getX() > 0 && getDistance(bestM.getX()-1, bestM.getY()) <= this.DISTANCE_DEPLACEMENT) {
	    			x = bestM.getX() - 1;
	    			y = bestM.getY();
	    			insectes.yard.setObjectLocation(this, x, y);
	    			d = true;
	    		}
	    		else if (bestM.getX() < s-1 && getDistance(bestM.getX()+1, bestM.getY()) <= this.DISTANCE_DEPLACEMENT) {
	    			x = bestM.getX() + 1;
	    			y = bestM.getY();
	    			insectes.yard.setObjectLocation(this, x, y);
	    			d = true;
	    		}
	    		else if (bestM.getY() > 0 && getDistance(bestM.getX(), bestM.getY()-1) <= this.DISTANCE_DEPLACEMENT) {
	    			x = bestM.getX();
	    			y = bestM.getY() - 1;
	    			insectes.yard.setObjectLocation(this, x, y);
	    			d = true;
	    		}
	    		else if (bestM.getY() < s-1 && getDistance(bestM.getX(), bestM.getY()+1) <= this.DISTANCE_DEPLACEMENT) {
	    			x = bestM.getX();
	    			y = bestM.getY() + 1;
	    			insectes.yard.setObjectLocation(this, x, y);
	    			d = true;
	    		}
	    	} else {
    	    	System.out.println("deplacer pour charger");
	    		// aller sur la case bestC pour charger au prochain tour
    			x = bestC.getX();
    			y = bestC.getY();
    			insectes.yard.setObjectLocation(this, x, y);
				d = true;
	    	}
    	}
    	if (d == false) {
	    	System.out.println("deplacer no food");
	    	int nb = 0;
	    	int dir = 0;
	    	while (d == false) {
	    		dir = insectes.random.nextInt(8);
	    		nb = insectes.random.nextInt(this.DISTANCE_DEPLACEMENT) + 1;
	    		
				switch(dir) {
				case 0:
					if (x - nb >= 0 && y - nb >= 0) {
						x = x - nb;
						y = y - nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 1:
					if (y - nb >= 0) {
						y = y - nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 2:
					if (x + nb < s && y - nb >= 0) {
						x = x + nb;
						y = y - nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 3:
					if (x + nb < s) {
						x = x + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 4:
					if (x + nb < s && y + nb < s) {
						x = x + nb;
						y = y + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 5:
					if ( y + nb < s) {
						y = y + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 6:
					if ( x - nb >= 0 && y + nb < s) {
						x = x - nb;
						y = y + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 7:
					if ( x - nb >= 0 ) {
						x = x - nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				}
	    	}
    	}
    	energie = energie - 1;
    }
    
    
    
    
    private int getDistance(int x, int y) {
    	int dx = Math.abs(this.x - x);
    	int dy = Math.abs(this.y - y);
    	return Math.max(dx, dy);
    }
}
