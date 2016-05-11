package org.aksw.rdfunit.validate.cli;

import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.coverage.TestCoverageEvaluator;
import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.writer.RdfMultipleWriter;
import org.aksw.rdfunit.io.writer.RdfResultsWriterFactory;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.TestCaseWriter;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.utils.ValidateUtils;

import org.aksw.rdfunit.validate.cli.ReadConfigurationFromINIFile;
import org.aksw.rdfunit.virtuoso.MakeTTL;
import org.aksw.rdfunit.virtuoso.WriteToVirtuoso;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>TestMain class.</p>
 *
 * @author Jaesung Kim 
 *         Description
 * @since 04/29/16 10:49 AM
 * @version $Id: $Id
 */
public class TestMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCLI.class);
    public static String HOST="jdbc:virtuoso://143.248.135.60:1111/charset=UTF-8/log_enable=2";
    public static String TESTHOST="jdbc:virtuoso://dmserver5.kaist.ac.kr:4004/charset=UTF-8/log_enable=2";
    public static String USERNAME="dba";
    public static String PASSWORD="dba";


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
	System.out.println("TEST MAIN START!");

	Calendar startTime = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
	String date = dateFormat.format(startTime.getTime());

	List<String> address = ReadConfigurationFromINIFile.readConfigurationFromINIFile();
	List<String> filenames = new ArrayList<String>();
	System.out.println(address);
	for(String address_name : address)
	{
	    String filename_parameter = MakeTTL.makeTTLfile(HOST, USERNAME, PASSWORD, address_name);
	    filenames.add(filename_parameter);
	    String[] rdfunit_parameter = {"-d", filename_parameter};
	    //ValidateCLI.doMain(rdfunit_parameter, filename_parameter);
	}
	
	//String OUTPUT_IRI = "http://kbox_" + date + ".kaist.ac.kr";
	//for(String filename : filenames)
	//    WriteToVirtuoso.Write(HOST, USERNAME, PASSWORD, OUTPUT_IRI, filename);
    }

}

