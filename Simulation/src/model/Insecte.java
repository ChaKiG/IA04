package model;

import sim.engine.SimState;
import sim.engine.Steppable;
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

		System.out.println("debut tour : ");
		System.out.println("energie : " + this.energie);
		System.out.println("charge : " + this.charge);
		
		if (this.energie == 0) {
			this.energie = -1;
			insectes.yard.remove(this);
			insectes.schedule.scheduleRepeating(this).stop();
			System.out.println("insecte meurt");
			insectes.tuerInsecte(this);
		} else if (this.energie > 0){
			List<Food> perception = percevoir(insectes);
			
			System.out.println("insecte percois " + perception.size() + " food");
			
			if ( !manger(perception) && !charger(perception) )
				move(perception, insectes);
			//System.out.println("fin tour : ");
			//System.out.println("energie : " + this.energie);
			//System.out.println("charge : " + this.charge + "\n\n\n");
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
    	return l;
    }
    
    private boolean manger(List<Food> l) {
		if ( energie <= Constants.MAX_ENERGY - Constants.FOOD_ENERGY ) {
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
    	if (charge < this.CHARGE_MAX) {
			for (Food f : l) {
				if (getDistance(f.getX(), f.getY()) == 0) {
					System.out.println("nb food sur place : " + f.getNbFood());
					if ( f.removeFood() ) {
			        	System.out.println("ok");
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
    	//System.out.println("deplacer");
    	boolean d = false;
    	int s = Constants.GRID_SIZE;
		Food bestC = null;
    	Food bestM = null;
    	if (!l.isEmpty()) {
    		int dist = 0;
    		bestC = l.get(0);
    		bestM = l.get(0);
	    	for (Food f : l) {
	    		dist = getDistance(f.getX(), f.getY());
	    		if (dist > 0 && dist <= this.DISTANCE_DEPLACEMENT && f.getNbFood() > bestC.getNbFood() )
	    			bestC = f;
	    		if (dist > 0 && dist <= this.DISTANCE_DEPLACEMENT + 1 && f.getNbFood() > bestM.getNbFood() )
	    			bestM = f;
			}
    	}
    	if ( bestM != null && this.energie <= 2) {
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
    	}
    	if ( bestC != null && d == false) {
	    	System.out.println("deplacer pour charger");
    		// aller sur la case bestC pour charger au prochain tour
			x = bestC.getX();
			y = bestC.getY();
			insectes.yard.setObjectLocation(this, x, y);
			d = true;
    	}
		if (d == false) {
	    	//System.out.println("deplacer aléatoire");
	    	int nb = 0;
	    	int dir = 0;
	    	while (d == false) {
	    		dir = insectes.random.nextInt(4);
	    		nb = Math.min(this.DISTANCE_PERCEPTION + 2, this.DISTANCE_DEPLACEMENT);
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
					if (x + nb < s && y - nb >= 0) {
						x = x + nb;
						y = y - nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 2:
					if (x + nb < s && y + nb < s) {
						x = x + nb;
						y = y + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
					break;
				case 3:
					if ( x - nb >= 0 && y + nb < s) {
						x = x - nb;
						y = y + nb;
						insectes.yard.setObjectLocation(this, x, y);
						d = true;
					}
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
