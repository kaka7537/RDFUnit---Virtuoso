package org.aksw.rdfunit.tests.executors;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.AggregatedTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.tests.query_generation.QueryGenerationCountFactory;
/**
 * Test Executor that extends StatusExecutor and in addition reports error counts and prevalence for every test case
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 4:05 PM
 * @version $Id: $Id
 */
public class AggregatedTestExecutor extends TestExecutor {

    /**
     * Instantiates a new AggregatedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public AggregatedTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase, Model testModel) throws TestCaseExecutionException {
        int total = -1, prevalence = -1;

        try {
            Query prevalenceQuery = testCase.getSparqlPrevalenceQuery();
            if (prevalenceQuery != null) {
                prevalence = getCountNumber(testSource.getExecutionFactory(), testCase.getSparqlPrevalenceQuery(), "total", "Prevalence", 0, testCase, testModel);
            }
        } catch (QueryExceptionHTTP e) {
            if (SparqlUtils.checkStatusForTimeout(e)) {
                prevalence = -1;
            } else {
                prevalence = -2;
            }
        }

        if (prevalence != 0) {
            // if prevalence !=0 calculate total
            try {
                total = getCountNumber(testSource.getExecutionFactory(), queryGenerationFactory.getSparqlQuery(testCase), "total", "ERROR", prevalence, testCase, testModel);
            } catch (QueryExceptionHTTP e) {
                if (SparqlUtils.checkStatusForTimeout(e)) {
                    total = -1;
                } else {
                    total = -2;
                }
            }
        } else {
            // else total will be 0 anyway
            total = 0;
        }

        // No need to throw exception here, class supports status
        return Collections.<TestCaseResult>singletonList(new AggregatedTestCaseResultImpl(testCase, total, prevalence));
    }

    private int getCountNumber(QueryExecutionFactory model, Query query, String var, String var2, int prevalence, TestCase testCase, Model testModel) {

        checkNotNull(query);
        checkNotNull(var);

        int result = 0;
	//System.out.println(var2);
        try ( QueryExecution qe = model.createQueryExecution(query) ) {
            ResultSet results = qe.execSelect();
            if (results != null && results.hasNext()) {
                QuerySolution qs = results.next();
		//System.out.println(qs);
                result = qs.get(var).asLiteral().getInt();
            }
        }
	if(var2.equals("ERROR") && prevalence > 0)
	{
	    String subject = null;
	    String object = null;

	    Query tripleQuery = QueryGenerationCountFactory.getSparqlTripleQuery(testCase);

	    String property = QueryGenerationCountFactory.getProperty();
	    property = property.replace(">", "");
	    property = property.replace("<", "");
	    Property rdf_prop = testModel.createProperty(property);

	    String obj = QueryGenerationCountFactory.getObject();
	    obj = obj.replace("?","");

            try ( QueryExecution qe_triple = model.createQueryExecution(tripleQuery) ) {
		ResultSet results_triple = qe_triple.execSelect();
		while(results_triple != null && results_triple.hasNext()){
		    QuerySolution qs_triple = results_triple.next();

		    RDFNode rdf_sub = qs_triple.get("this");
		    RDFNode rdf_obj = qs_triple.get(obj);

		    subject = rdf_sub.toString();
		    object = rdf_obj.toString();
		    Resource res_sub = testModel.createResource(subject);
		    //System.out.println(subject + " " + property + " " + object);
		    //if(testModel.contains(res_sub, rdf_prop, rdf_obj))
		    //	System.out.println("RESOURCE EXISTS!!!!");
		    //StmtIterator iterator = testModel.listStatements(res_sub, rdf_prop, rdf_obj);
		    //while(iterator.hasNext())
		    //{
			//Statement stm = iterator.next();
			//testModel.remove(stm);
			//System.out.println(stm.getSubject() + " " + stm.getPredicate() + " " + stm.getObject());
		    //}
		    testModel = testModel.removeAll(res_sub, rdf_prop, rdf_obj); 

		    //Query deleteQuery = "DELETE WHERE { +<" + subject + "> " + property + " <" + object + ">}";
		}
	
   	    }
 	    
	}

        return result;

    }
}
