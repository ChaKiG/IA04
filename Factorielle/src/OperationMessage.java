
public class OperationMessage {
	private Long v1;
	private Long v2;
	private String operator;
	
	OperationMessage() {}
	OperationMessage(Long v1, Long v2, String operator) {
		this.v1 = v1;
		this.v2 = v2;
		this.operator = operator;
	}

	public Long getV1() {return v1;}
	public Long getV2() {return v2;}
	public String getOperator() {return operator;}
	
	public void setV1(Long v1) {this.v1 = v1;}
	public void setV2(Long v2) {this.v2 = v2;}
	public void setOperator(String operator) {this.operator = operator;}
}