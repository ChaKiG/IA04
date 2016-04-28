import java.io.FileInputStream;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;

public class BaseConnaissances {
	Model model;
	
	public BaseConnaissances() {
		model = ModelFactory.createDefaultModel(); 
		try {
			model.read(new FileInputStream("foaf.n3"), null, "TURTLE");
			model.read(new FileInputStream("baseConnaissances"), null, "TURTLE"); 
		} catch (Exception e) {
			e.printStackTrace();
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
