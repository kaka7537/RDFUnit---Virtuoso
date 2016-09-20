package org.aksw.rdfunit.validate.cli;

import virtuoso.jena.driver.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class ReadConfigurationFromINIFile {
    public static String HOST="jdbc:virtuoso://211.109.9.79:1111/charset=UTF-8/log_enable=2";
    public static String TESTHOST="jdbc:virtuoso://dmserver5.kaist.ac.kr:4004/charset=UTF-8/log_enable=2";
    public static String USERNAME="dba";
    public static String PASSWORD="dba";
    public static String GRAPH;
    public static String TEST_OUTPUT_IRI; //Graph IRI of Input triple file
    public static String TEST_OUTPUT_IRI2;
    public static String TEST_OUTPUT_IRI3;

    public static List<String> readConfigurationFromINIFile()
    {
	//Deploy - Date that BBox run
	Calendar startTime = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
	String date = dateFormat.format(startTime.getTime());
		
	TEST_OUTPUT_IRI = "http://bbox_"+ date +".kaist.ac.kr";
	TEST_OUTPUT_IRI2 = "http://kbox_"+ date +".kaist.ac.kr";	
	TEST_OUTPUT_IRI3 = "http://eachbox_operation_date.kaist.ac.kr";

	// Get IRI from Virtuoso
	String sparqlQueryString = "SELECT ?g (COUNT(*) as ?cnt) WHERE { GRAPH ?g {?s ?p ?o.} } GROUP BY ?g ORDER BY DESC(2)";
	Query query = QueryFactory.create(sparqlQueryString);
	VirtGraph graph = new VirtGraph(HOST, USERNAME, PASSWORD);
	VirtuosoQueryExecution qexec = VirtuosoQueryExecutionFactory.create(query, graph);
	ResultSet results = qexec.execSelect();
		
	ArrayList<String> existingGraphs = new ArrayList<String>();
		
	while (results.hasNext()) {
		QuerySolution row = results.next();
		String graphName = row.get("?g").toString();
		existingGraphs.add(graphName);
	}

	// Check recent KBox operation date  -  Added on 160608
	VirtGraph runtimeGraph = new VirtGraph(TEST_OUTPUT_IRI3, HOST, USERNAME, PASSWORD);
	Query sparql = QueryFactory.create("SELECT * WHERE {?s ?p ?o FILTER regex(str(?s), \"KBox\")}");
	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, runtimeGraph);
	ResultSet results_runtime = vqe.execSelect();
	ArrayList<String> runningTimeList = new ArrayList<String>();
	int kboxLastItrDate = 0;
		
	try{
		while (results_runtime.hasNext()) {
			QuerySolution row = results_runtime.next();
			String runningTime = row.get("?o").toString();
			runningTimeList.add(runningTime);
		}
		
		Comparator cmp = Collections.reverseOrder();
		Collections.sort(runningTimeList, cmp);
		kboxLastItrDate = Integer.parseInt(runningTimeList.get(0));
	}catch(IndexOutOfBoundsException e){
		kboxLastItrDate = 151030;  // Initial day
	}
		
		
	System.out.println("KBoxLastOperationDate: " + kboxLastItrDate);	

	// Find the l2k, c2k, and kbox IRIs
	ArrayList<String> l2k_c2k_b2k_Graphs = new ArrayList<String>(); 
	ArrayList<String> kboxGraphs = new ArrayList<String>(); 
		
	for (String string : existingGraphs) {
            if(string.matches("(http://l2k_1).*") || string.matches("(http://c2k_1).*")){     //  || string.matches("(http://b2k_1).*")){
                l2k_c2k_b2k_Graphs.add(string);
            }
	    else if(string.matches("(http://kbox_1).*")){    //kbox
            	kboxGraphs.add(string);
            }
        }
		
	//Commented on 160608
	//Comparator cmp = Collections.reverseOrder();
	//Collections.sort(kboxGraphs, cmp);
	//String kboxLastItr = kboxGraphs.get(0);
	//int kboxLastItrDate = Integer.parseInt(kboxGraphs.get(0).substring(12, 18));
	//kboxLastItrDate = 160202; // For Testing		

	// Search the kbox data not enriched yet
	ArrayList<String> needEnrichIRI = new ArrayList<String>();
	Collections.sort(l2k_c2k_b2k_Graphs);
	for (String string : l2k_c2k_b2k_Graphs) {
            if(Integer.parseInt(string.substring(11, 17)) > kboxLastItrDate){
                needEnrichIRI.add(string);
            }
        }

	//Commented on 160608
	//needEnrichIRI.add(kboxLastItr);	
	graph.close();
	runtimeGraph.close();
	return needEnrichIRI;
	//OUTPUT_FILE_PATH = "./output/"+"bbox_"+date+".ttl";
    }



}
