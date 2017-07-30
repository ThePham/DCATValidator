package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;

public class Loader {
	
	private static final String OUTPUTFILE = "output.txt";
	
	//Helper method to load RDF file into repository
	public static void loadFile(File file, Repository repo) {
	        	System.out.println("Loading File: " + file.getName());
	            String baseURI = "http://data.gov.sk/";
	    		try {
	    		   RepositoryConnection con = repo.getConnection();
	    		   try {
	    		      con.add(file, baseURI, RDFFormat.RDFXML);
	    		   }
	    		   finally {
	    		      con.close();
	    		   }
	    		}
	    		catch (RDF4JException e) {
	    		   // handle exception
	    		}
	    		catch (IOException e) {
	    		   // handle io exception
	    		}    
	}
	
	public static void main(String[] args) {
		
		//Check for argument
		if (args.length == 0) {
			System.out.println("Error: please enter argument: [path-to-catalog-file]");
		    System.exit(0);
		}
		 			
		//Initialize native repository
		File dataDir = new File("Repository");
		String indexes = "spoc,posc,cosp";
		Repository repo = new SailRepository(new ForwardChainingRDFSInferencer(
                new NativeStore(dataDir, indexes)));
				
		repo.initialize();
		
		//Load rules file
		File file = new File(Loader.class.getClassLoader().getResource("rules/rules.rdf").getFile());
	    loadFile(file, repo);
	    
	    //Load catalog file
		file = new File(args[0]);
	    loadFile(file, repo);
	    
	    //Write header for output file
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) { 	
			bw.newLine();
			bw.write("Validating file: " + args[0]);
			bw.newLine();
			bw.write("Data and time: " + new Date());
			bw.newLine();
	    } catch (IOException e) {
			e.printStackTrace();
	    }
	    
		//Query for mandatory class validation		
		try (RepositoryConnection con = repo.getConnection()) {

			   //Check for dcat:Catalog
			   String queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Catalog>  } ";
			   TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Error: Mandatory class dcat:Catalog is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Error: Mandatory class dcat:Catalog is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for dcat:Dataset
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Dataset>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Error: Mandatory class dcat:Dataset is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Error: Mandatory class dcat:Dataset is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for foaf:Agent
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Agent>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Error: Mandatory class foaf:Agent is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Error: Mandatory class foaf:Agent is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for dcat:Distribution
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/dcat#Distribution>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Warning: Recommended class dcat:Distribution is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Warning: Recommended class dcat:Distribution is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for skos:Concept
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Warning: Recommended class skos:Concept is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Warning: Recommended class skos:Concept is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for dct:LicenseDocument
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/LicenseDocument>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Warning: Recommended class dct:LicenseDocument is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Warning: Recommended class dct:LicenseDocument is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			   
			   //Check for dct:LicenseDocument
			   queryString = "SELECT * WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#ConceptScheme>  } ";
			   tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   tupleQuery.setIncludeInferred(true);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {				   
				  if (!result.hasNext()) {
					  //System.out.println("Warning: Recommended class skos:ConceptScheme is missing.");
					  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
							bw.write("Warning: Recommended class skos:ConceptScheme is missing.");
							bw.newLine();
					  } catch (IOException e) {
							e.printStackTrace();
					  }
				  }
			   }
			    
		}
		
		//Query for mandatory property validation		
		try (RepositoryConnection con = repo.getConnection()) {
			
			   String queryString = "PREFIX dct: <http://purl.org/dc/terms/>"
			   		+ "select * where {" +
					   "?class <https://data.gov.sk/def/ontology/resource/hasMandatory> ?attribute ." +
					   "?instance rdf:type ?class ." +
					   "OPTIONAL {" +
					   		"?instance ?attribute ?value ." +
					   "}" + 
					  "}";
			   
			   TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
				   
			      while (result.hasNext()) {  // iterate over the result
			    	  BindingSet bindingSet = result.next();
			    	  Value valueOfClass = bindingSet.getValue("class");
			    	  Value valueOfAttr = bindingSet.getValue("attribute");
			    	  Value valueOfInstance = bindingSet.getValue("instance");
			    	  Value valueOfValue = bindingSet.getValue("value");
			    	  
			    	  //If value is missing (property is missing)
			    	  if (valueOfValue == null) {
			    		  //System.out.println("Error: Mandatory property " + valueOfAttr + " of class: " + valueOfClass + " ,instance: " + valueOfInstance + " is missing.");
			    		  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
								bw.write("Error: Mandatory property " + valueOfAttr + " of class: " + valueOfClass + ", instance: " + valueOfInstance + " is missing.");
								bw.newLine();
						  } catch (IOException e) {
								e.printStackTrace();
						  }
			    	  }

			      }
			   }
		}
		
	//Query for recommended property validation		
	try (RepositoryConnection con = repo.getConnection()) {
				
			   String queryString = "PREFIX dct: <http://purl.org/dc/terms/>"
			   		+ "select * where {" +
					   "?class <https://data.gov.sk/def/ontology/resource/hasRecommended> ?attribute ." +
					   "?instance rdf:type ?class ." +
					   "OPTIONAL {" +
					   		"?instance ?attribute ?value ." +
					   "}" + 
					  "}";
			   
			   TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			   
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
				   
			      while (result.hasNext()) {  // iterate over the result
			    	  BindingSet bindingSet = result.next();
			    	  Value valueOfClass = bindingSet.getValue("class");
			    	  Value valueOfAttr = bindingSet.getValue("attribute");
			    	  Value valueOfInstance = bindingSet.getValue("instance");
			    	  Value valueOfValue = bindingSet.getValue("value");
			    	  
			    	  //If value is missing (property is missing)
			    	  if (valueOfValue == null) {
			    		  //System.out.println("Error: Mandatory property " + valueOfAttr + " of class: " + valueOfClass + " ,instance: " + valueOfInstance + " is missing.");
			    		  try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUTFILE, true))) {
								bw.write("Warning: Recommended property " + valueOfAttr + " of class: " + valueOfClass + ", instance: " + valueOfInstance + " is missing.");
								bw.newLine();
						  } catch (IOException e) {
								e.printStackTrace();
						  }
			    	  }

			      }
			   }
			   
			   System.out.println("Validation finished. Please check output.txt file for results.");
			   //Clear repository for next use
			   con.clear();
			}		

	}
}
