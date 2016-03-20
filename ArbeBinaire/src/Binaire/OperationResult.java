package Binaire;


public class OperationResult {
	private int value;
	private String comment;
	
	OperationResult() {}
	OperationResult(int value, String comment) {
		this.value = value;
		this.comment = comment;
	}

	public int getValue() {return value;}
	public String getComment() {return comment;}
	
	public void setValue(int value) {this.value = value;}
	public void setComment(String comment) {this.comment = comment;}
}
