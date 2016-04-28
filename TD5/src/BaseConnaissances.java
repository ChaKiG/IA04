import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class BaseConnaissances {
	Model model;
	
	public BaseConnaissances() {
		model = ModelFactory.createDefaultModel(); 
		try {
			model.read(new FileInputStream("foaf.n3"), null, "TURTLE");
			model.read(new FileInputStream("baseConnaissances"), null, "TURTLE"); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void runSelectQuery(String qfilename) { 
		Query query = QueryFactory.read(qfilename); 
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model); 
		ResultSet r = queryExecution.execSelect(); 
		ResultSetFormatter.out(System.out,r); 
		queryExecution.close(); 
	}
}
