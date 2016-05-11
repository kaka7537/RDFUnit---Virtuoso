package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import java.util.StringTokenizer; 

/**
 * Factory that returns aggregate count queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:07 PM
 * @version $Id: $Id
 */
public class QueryGenerationCountFactory implements QueryGenerationFactory {

    private static final String selectClauseSimple = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE ";

    private static final String selectClauseGroupStart = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE {" +
                                                   " SELECT ?this WHERE ";
    private static final String selectClauseGroupEnd = "}";

    private static final String selectClauseTripleSimple = " SELECT ?this ";
    private static final String selectClauseTripleWhere = " WHERE ";

    private static final String selectClauseTripleGroupStart = " SELECT ?this "; 
    private static final String selectClauseTripleGroupWhere = " WHERE {"; 
    private static final String selectClauseTripleGroupStart2 = " SELECT ?this "; 
    private static final String selectClauseTripleGroupEnd = "}";
    
    private static String property;
    private static String object;
    /** {@inheritDoc} */
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getSparqlQuery(testCase).toString();
    }

    /** {@inheritDoc} */
    @Override
    public Query getSparqlQuery(TestCase testCase) {
        Query query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                        selectClauseSimple + testCase.getSparqlWhere()
        );

        if (!query.hasGroupBy()) {
            return query;
        }
	
        // When we have HAVING the aggregate is calculated against the HAVING expression
        // This way we enclose the query in a sub-select and calculate the count () correctly
        // See https://issues.apache.org/jira/browse/JENA-766
        query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                        selectClauseGroupStart
                        + testCase.getSparqlWhere() +
                        selectClauseGroupEnd
        );

        return query;
    }

    public static Query getSparqlTripleQuery(TestCase testCase) {
        
        //Query query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
        //                selectClauseSimple + testCase.getSparqlWhere()
        //);
	String whereClauses = testCase.getSparqlWhere().toString();
	int firstlineindex = whereClauses.indexOf("\n");
	int secondlineindex = whereClauses.indexOf("\n",firstlineindex+1);

	String firstlinewhereClauses = whereClauses.substring(firstlineindex+3, secondlineindex);
	StringTokenizer st = new StringTokenizer(firstlinewhereClauses," ");
	//String property = null;
	while(st.hasMoreTokens()) {
	    String temp = st.nextToken();
	    if((!temp.contains("this")) && temp.contains("?"))
		object = temp;	
	    else if(temp.contains("http"))
		property = temp;
	}
	//System.out.println(property);
	Query testingQuery = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
				selectClauseTripleSimple + object + selectClauseTripleWhere +
				testCase.getSparqlWhere());

        if (!testingQuery.hasGroupBy()) {
	    //System.out.println("origin\n" + query);
	    //System.out.println("new\n" + testingQuery);
            return testingQuery;
        }

        // When we have HAVING the aggregate is calculated against the HAVING expression
        // This way we enclose the query in a sub-select and calculate the count () correctly
        // See https://issues.apache.org/jira/browse/JENA-766
        //query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
        //                selectClauseGroupStart
        //                + testCase.getSparqlWhere() +
        //                selectClauseGroupEnd
        //);

	testingQuery = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + 
				selectClauseTripleGroupStart + object + selectClauseTripleGroupWhere +
				selectClauseTripleGroupStart2 + object + selectClauseTripleWhere +
				testCase.getSparqlWhere() + selectClauseTripleGroupEnd);

	//System.out.println("origin\n" + query);
	//System.out.println("new\n" + testingQuery);
        return testingQuery;
    }
   
    public static String getProperty() {
	return property;
    }

    public static String getObject() {
	return object;
    }
}
