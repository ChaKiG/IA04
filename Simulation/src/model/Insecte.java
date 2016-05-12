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
			move(perception);
		}
	}
    
    private List<Food> percevoir(Insectes insectes) {
    	List<Food> l = new ArrayList<Food>();
    	for (int i = 0-DISTANCE_PERCEPTION; i <= DISTANCE_PERCEPTION; i++) {
    		for (int j = 0-DISTANCE_PERCEPTION; j <= DISTANCE_PERCEPTION; j++) {
        		Bag b = insectes.yard.getObjectsAtLocation(x+i, y+j);
        		if (!b.isEmpty()) {
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
		if (energie <= Constants.MAX_ENERGY - Constants.FOOD_ENERGY && (charge >= 1 || !l.isEmpty())) {
			if (charge >= 1) {
	    		this.charge--;
	        	this.energie += Constants.FOOD_ENERGY;
				return true;
			}
			for (Food f : l) {
				if (getDistance(f.getX(), f.getY()) == 1) {
		    		if (f.removeFood()) {
			        	this.energie += Constants.FOOD_ENERGY;
						return true;
		    		}
				}
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
						return true;
		    		}
				}
			}
    	}
    	return false;
    }
    
    private void move(List<Food> l) {
    	
    }
    
    
    
    
    private int getDistance(int x, int y) {
    	int dx = Math.abs(this.x - x);
    	int dy = Math.abs(this.y - y);
    	return Math.max(dx, dy);
    }
    
    
    /*
  protected int friendsNum(Beings beings) {
	return friendsNum(beings,x,y);
 }
  protected int friendsNum(Beings beings,int l,int c) {
		int nb = 0;
	    for (int i = -1 ; i <= 1 ; i++) {
	    for (int j = -1 ; j <= 1 ; j++) {
	      if (i != 0 || j != 0) {
	    	  Int2D flocation = new Int2D(beings.yard.stx(l + i),beings.yard.sty(c + j));
	    	  Object ag = beings.yard.get(flocation.x,flocation.y);
	          if (ag != null) {
	        	  if (ag.getClass() == this.getClass())
	        		  nb++;
	          }
	      }
	    }
	  }
	  return nb;
	 }
  
  public boolean move(Beings beings) {
	boolean done = false;
	int n = beings.random.nextInt(Beings.NB_DIRECTIONS);
	switch(n) {
	case 0: 
		if (beings.free(x-1, y) 
	         && friendsNum(beings,x-1,y) >= LEVEL) {
		 beings.yard.set(x, y, null);
		 beings.yard.set(beings.yard.stx(x-1), y, this);
		 x = beings.yard.stx(x-1);
		 done = true;
		}
		break;
	case 1:
		if (beings.free(x+1, y) && friendsNum(beings,x+1,y) >= LEVEL) {
		 beings.yard.set(x, y, null);
		 beings.yard.set(beings.yard.stx(x+1), y, this);
		 x = beings.yard.stx(x+1);
		 done = true;
	    }
		break;
	case 2:
		if (beings.free(x, y-1) && friendsNum(beings,x,y-1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(x, beings.yard.sty(y-1), this);
			y = beings.yard.sty(y-1);
			done = true;
		}
		break;
	case 3: 
		if (beings.free(x, y+1) && friendsNum(beings,x,y+1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(x, beings.yard.sty(y+1), this);
			y = beings.yard.sty(y+1);
			done = true;
		}
		break;
	case 4:
		if (beings.free(x-1, y-1) && friendsNum(beings,x-1,y-1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y-1), this);
			x = beings.yard.stx(x-1);
			y = beings.yard.sty(y-1);
			done = true;
		}
		break;
	case 5:
		if (beings.free(x+1, y-1) && friendsNum(beings,x+1,y-1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y-1), this);
			x = beings.yard.stx(x+1);
			y = beings.yard.sty(y-1);
			done = true;
		}
		break;
	case 6:
		if (beings.free(x+1, y+1) && friendsNum(beings,x+1,y+1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y+1), this);
			x = beings.yard.stx(x+1);
			y = beings.yard.sty(y+1);
			done = true;
		}
		break;
	case 7:
		if (beings.free(x-1, y+1) && friendsNum(beings,x-1,y+1) >= LEVEL) {
			beings.yard.set(x, y, null);
			beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y+1), this);
			x = beings.yard.stx(x-1);
			y = beings.yard.sty(y+1);
			done = true;
		}
		break;
	}
	return done;
 }
  public boolean move2(Beings beings) {
		boolean done = false;
			int n = beings.random.nextInt(Beings.NB_DIRECTIONS);
			switch(n) {
			case 0: 
				if (beings.free(x-1, y)) {
				 beings.yard.set(x, y, null);
				 beings.yard.set(beings.yard.stx(x-1), y, this);
				 x = beings.yard.stx(x-1);
				 done = true;
				}
				break;
			case 1:
				if (beings.free(x+1, y)) {
				 beings.yard.set(x, y, null);
				 beings.yard.set(beings.yard.stx(x+1), y, this);
				 x = beings.yard.stx(x+1);
				 done = true;
			    }
				break;
			case 2:
				if (beings.free(x, y-1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(x, beings.yard.sty(y-1), this);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 3: 
				if (beings.free(x, y+1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(x, beings.yard.sty(y+1), this);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			case 4:
				if (beings.free(x-1, y-1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y-1), this);
					x = beings.yard.stx(x-1);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 5:
				if (beings.free(x+1, y-1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y-1), this);
					x = beings.yard.stx(x+1);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 6:
				if (beings.free(x+1, y+1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y+1), this);
					x = beings.yard.stx(x+1);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			case 7:
				if (beings.free(x-1, y+1)) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y+1), this);
					x = beings.yard.stx(x-1);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			}
		return done;
	 }
  public boolean tryMove(Beings beings,int f) {
		boolean done = false;
			int n = beings.random.nextInt(Beings.NB_DIRECTIONS);
			switch(n) {
			case 0: 
				if (beings.free(x-1, y) && friendsNum(beings,x-1,y) > f) {
				 beings.yard.set(x, y, null);
				 beings.yard.set(beings.yard.stx(x-1), y, this);
				 x = beings.yard.stx(x-1);
				 done = true;
				}
				break;
			case 1:
				if (beings.free(x+1, y) && friendsNum(beings,x+1,y) > f) {
				 beings.yard.set(x, y, null);
				 beings.yard.set(beings.yard.stx(x+1), y, this);
				 x = beings.yard.stx(x+1);
				 done = true;
			    }
				break;
			case 2:
				if (beings.free(x, y-1)  && friendsNum(beings,x,y-1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(x, beings.yard.sty(y-1), this);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 3: 
				if (beings.free(x, y+1)  && friendsNum(beings,x,y+1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(x, beings.yard.sty(y+1), this);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			case 4:
				if (beings.free(x-1, y-1)  && friendsNum(beings,x-1,y-1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y-1), this);
					x = beings.yard.stx(x-1);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 5:
				if (beings.free(x+1, y-1)  && friendsNum(beings,x+1,y-1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y-1), this);
					x = beings.yard.stx(x+1);
					y = beings.yard.sty(y-1);
					done = true;
				}
				break;
			case 6:
				if (beings.free(x+1, y+1)  && friendsNum(beings,x+1,y+1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x+1), beings.yard.sty(y+1), this);
					x = beings.yard.stx(x+1);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			case 7:
				if (beings.free(x-1, y+1) && friendsNum(beings,x-1,y+1) > f) {
					beings.yard.set(x, y, null);
					beings.yard.set(beings.yard.stx(x-1), beings.yard.sty(y+1), this);
					x = beings.yard.stx(x-1);
					y = beings.yard.sty(y+1);
					done = true;
				}
				break;
			}
		return done;
	 }
	
	*/
}
