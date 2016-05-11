import java.util.ArrayList;
import java.util.List;

public class SparqlRequest {
	private String request;
	private List<String> headers;
	static private List<String> defaultHeaders = new ArrayList<String>();
	static {
		defaultHeaders.add("PREFIX dc:      <http://purl.org/dc/elements/1.1/>");
		defaultHeaders.add("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>");
		defaultHeaders.add("PREFIX wot:     <http://xmlns.com/wot/0.1/>");
		defaultHeaders.add("PREFIX foaf:    <http://xmlns.com/foaf/0.1/>");
		defaultHeaders.add("PREFIX owl:     <http://www.w3.org/2002/07/owl#>");
		defaultHeaders.add("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		defaultHeaders.add("PREFIX vs:      <http://www.w3.org/2003/06/sw-vocab-status/ns#>");
		defaultHeaders.add("PREFIX geo:     <http://linkedgeodata.org/triplify/>");
		defaultHeaders.add("PREFIX td5:	 	<http://www.utc.fr/>");
	}
	
	SparqlRequest() {
		this.headers = new ArrayList<String>();
		headers.addAll(defaultHeaders);
	}
	
	SparqlRequest(String request) {
		this.request = request;
		this.headers = new ArrayList<String>();
		headers.addAll(defaultHeaders);
	}
	
	SparqlRequest(String request, List<String> headers) {
		this.request = request;
		this.headers = new ArrayList<String>();
		headers.removeAll(defaultHeaders);
		this.headers.addAll(defaultHeaders);
		this.headers.addAll(headers);
	}

	public String getRequest() {return request;}
	public void setRequest(String request) {this.request = request;}
	
	public List<String> getHeaders() {return headers;}
	public void setHeaders(List<String> headers) {
		this.headers.clear();
		this.headers.addAll(defaultHeaders);
		headers.removeAll(defaultHeaders);
		this.headers.addAll(headers);
	}
	
	public String transformRequest() {
		String str = "";
		for(String s : this.headers) {
			str += s + "\r\n";
		}
		str += request;
		return str;
	}
}