import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Sudoku {
	private Cellule[][] sudoku;
	
	public Sudoku() {
		sudoku = new Cellule[9][9];

		File file = new File("SUDOKU_5");
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    for (int i=0; i<9; i++) {
		    	text = reader.readLine();
		    	for (int j=0; j<text.length(); j++) {
		    		sudoku[i][j] = new Cellule(Character.getNumericValue(text.charAt(j)));
		    	}
		    }
		    show(false);
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		} finally {
			if (reader != null) {
			    try {
					reader.close();
				} catch (Exception e) {
				    System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public void show(boolean detailed) {
		System.out.println();
		System.out.println();
		
	    for (int j=0; j<9; j++) {
	    	for (int i=0; i<9; i++) {
	    		if (detailed) {
	    			System.out.println("[" + j + "][" + i + "]" + sudoku[i][j].getValue() + "-" +sudoku[i][j].getPossible());
	    		} else {
	    			System.out.print(sudoku[i][j].getValue() + "  ");
	    		}
    		}
	    	if (!detailed) {
	    		System.out.println();
	    	}
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
					l.add(sudoku[3*line+i][3*col+j]);
				}
		}
		return l;
	}
	protected void setCellules(int group, List<Cellule> cells) {
		List<Cellule> l = getCellules(group);
		for (int i=0; i<9; i++) {
			if (cells.get(i).getValue() > 0)
				l.get(i).defineValue(cells.get(i).getValue());
			else
				l.get(i).reviewPossible(cells.get(i).getPossible());
		}
	}
	
	
	public int isEnded() {
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (sudoku[i][j].getValue() == 0)
					return 0;
			}
		}
		return 1;
	}
	
	public int isCorrect() {
		List<Integer> found = new ArrayList<Integer>();
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (found.contains(sudoku[i][j].getValue()))
					return 0;
				else
					found.add(sudoku[i][j].getValue());
			}
			found.clear();
		}
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (found.contains(sudoku[j][i].getValue()))
					return 0;
				else
					found.add(sudoku[j][i].getValue());
			}
			found.clear();
		}
		return 1;
	}
	
}
