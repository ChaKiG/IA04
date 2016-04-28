import com.hp.hpl.jena.query.*;

public class BaseExterieur {
	
	public static void query(String file) {
		Query query = QueryFactory.read(file);
		
		System.setProperty("http.proxyHost","proxyweb.utc.fr"); 
		System.setProperty("http.proxyPort","3128");

		System.out.println("Query sent"); 
		System.out.println(query.toString());
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql",query); 
		ResultSet r = qexec.execSelect(); 
		ResultSetFormatter.out(r); 
		qexec.close();
	}
	
	
}
