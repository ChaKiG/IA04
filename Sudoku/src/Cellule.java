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
		value = val;
		possible = new ArrayList<Integer>();
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
