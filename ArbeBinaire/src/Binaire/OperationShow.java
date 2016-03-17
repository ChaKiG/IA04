package Binaire;


public class OperationShow {
	int value;
	int fils1;
	int fils2;
	
	OperationShow() {}
	OperationShow(int value, int fils1, int fils2) {
		this.value = value;
		this.fils1 = fils1;
		this.fils2 = fils2;
	}

	public int getValue() {return value;}
	public int getFils1() {return fils1;}
	public int getFils2() {return fils2;}

	public void setValue(int value) {this.value = value;}
	public void setFils1(int fils1) {this.fils1 = fils1;}
	public void setFils2(int fils2) {this.fils2 = fils2;}
}