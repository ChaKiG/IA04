public class OperationResult {
	private String request;
	
	OperationResult() {}
	OperationResult(String request) {
		this.request = request;
	}

	public String getRequest() {return request;}
	
	public void setRequest(String request) {this.request = request;}
}