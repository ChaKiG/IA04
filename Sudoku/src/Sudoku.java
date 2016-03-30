import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Sudoku {
	private Cellule[][] sudoku;
	
	public Sudoku() {
		sudoku = new Cellule[9][9];

		File file = new File("SUDOKU");
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    for (int j=0; j<9; j++) {
		    	text = reader.readLine();
		    	for (int i=0; i<text.length(); i++) {
		    		sudoku[i][j] = new Cellule(Character.getNumericValue(text.charAt(i)));
		    	}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
			if (reader != null) {
			    try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	    show();
	}
	
	public void show() {
	    for (int j=0; j<9; j++) {
	    	for (int i=0; i<9; i++) {
	    		System.out.print(sudoku[i][j].getValue());
	    	}
	    	System.out.println();
	    }		
	}
	
	
	
	protected List<Cellule> getCellules(int group) {
		List<Cellule> l = new ArrayList<Cellule>();
		if (group < 9) {
			for (int i=0; i<9; i++)
				l.add(sudoku[group][i]);
		} else if (group < 18) {
			for (int i=0; i<9; i++)
				l.add(sudoku[i][group-9]);
		} else if (group < 27) {
			int line = (group - 18)/3;
			int col = (group - 18)%3;
			for(int i=0; i<3; i++)
				for(int j=0; j<3; j++) {
					l.add(sudoku[line+i][col+j]);
				}
		}
		return l;
	}
	protected void setCellules(int group, List<Cellule> cells) {
		List<Cellule> l = getCellules(group);
		for (int i=0; i<9; i++) {
			if (cells.get(i).getValue() != 0)
				l.get(i).setValue(cells.get(i).getValue());
			else
				l.get(i).reviewPossible(cells.get(i).getPossible());
		}
	}
}
