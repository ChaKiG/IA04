import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class BaseExterieur {
	
	public static void query(String file) {
		Query query = QueryFactory.read(file);
		
		/*System.setProperty("http.proxyHost","proxyweb.utc.fr"); 
		System.setProperty("http.proxyPort","3128"); */

		System.out.println("Query sent"); 
		System.out.println(query.toString());
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",query); 
		ResultSet r = qexec.execSelect(); 
		ResultSetFormatter.out(r); 
		qexec.close();
	}
	
	
}
