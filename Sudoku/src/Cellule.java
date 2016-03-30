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
		} else
			value = val;
	}
	public Cellule(List<Integer> possibleValues) {
		value = 0;
		possible = possibleValues;
	}
	
	public List<Integer> getPossible() {
		return possible;
	}
	public int getValue() {
		return value;
	}
	
	public void setValue(int val) {
		value = val;
	}
	
	public void setPossible(List<Integer> poss) {
		possible = poss;
	}
	
	public void removePossible(int val) {
		int index = possible.indexOf(val);
		if (index > -1)
			possible.remove(index);
		if (possible.size() == 1) {
			value = possible.get(0);
			possible.clear();
		}
	}
	
	
	
	
	
	public void reviewPossible(List<Integer> val) {
		int l = possible.size(),
			i = 0;
		while (i < l) {
			if (!val.contains(possible.get(i))) {
				possible.remove(i);
				l--;
			} else
				i++;
		}
		if (possible.size() == 1) {
			value = possible.get(0);
			possible.clear();
		}
	}
	
}
