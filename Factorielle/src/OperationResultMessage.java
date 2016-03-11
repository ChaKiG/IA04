
public class OperationResultMessage {
	private Long result;
	private String comment;
	
	OperationResultMessage() {}
	OperationResultMessage(Long result, String comment) {
		this.result = result;
		this.comment = comment;
	}

	public Long getResult() {return result;}
	public String getComment() {return comment;}
	
	public void setResult(Long result) {this.result = result;}
	public void setComment(String comment) {this.comment = comment;}
}