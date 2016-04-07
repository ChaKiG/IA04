import java.util.ArrayList;
import java.util.List;

public class Cellule {
	private List<Integer> possible;
	private int value;
	
	public Cellule() {
		value = 0;
		possible = new ArrayList<Integer>();
		for (int i=1; i < 10 ; i++)
			possible.add(i);
	}
	public Cellule(int val) {
		possible = new ArrayList<Integer>();
		if (val <= 0) {
			value = 0;
			for (int i=1; i<10; i++)
				possible.add(i);
		} else {
			value = val;
		}
	}
	public Cellule(List<Integer> possibleValues) {
		value = 0;
		possible = new ArrayList<Integer>(possibleValues);
	}
	
	public int getValue() {return value;}
	public void setValue(int val) {value = val;}
	public List<Integer> getPossible() {return new ArrayList<Integer>(possible);}
	public void setPossible(List<Integer> poss) {possible = new ArrayList<Integer>(poss);}
	
	public void removePossible(int val) {
		possible.remove((Integer)val);
	}
	
	
	public void defineValue(int val) {
		value = val;
		possible.clear();
	}
	public void reviewPossible(List<Integer> val) {
		possible.retainAll(val);
	}
	
	public void show() {
		System.out.print("val:" + value + "    - ");
		for (int i : possible) {
			System.out.print(" - " + i);
		}
		System.out.println();
	}

}