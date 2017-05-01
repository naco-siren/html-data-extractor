package gokurakujoudo;

import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;
import gokurakujoudo.dom_tree_helpers.DomTreeCleaner;
import gokurakujoudo.dom_tree_helpers.DomTreeDataExtractor;
import gokurakujoudo.injection.SeleniumInjector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import org.jsoup.safety.Whitelist;

/**
 * Created by naco_siren on 3/26/17.
 */
public class HTMLDataExtractor {

    /* Constants: */
    private static final int CONNECTION_TIMEOUT_LIMIT = 3000;

    /* Parameters: minimum result size for filtering */
    private int _minResultSize ;
    public void setMinResultSize(int minResultSize) {
        this._minResultSize = minResultSize;
    }
    public int getMinResultSize() {
        return _minResultSize;
    }

    /* Data: */
    private Document _document;
    private Element _body;

    /* Tools: */
    private DomTreeCleaner _domTreeCleaner;
    private DomTreeDataExtractor _domTreeDataExtractor;

    /* Output: */
    private DataGroups _results;
    public DataGroups getResults() {

        /* Rank the results */
        int resultSize = _results.size();

        for (int i = 0; i < resultSize; i++) {
            DataGroup dataGroup = _results.get(i);
            ArrayList<Node> data = dataGroup._data;

            /* measurement += avg(tree_node_size) */
            double numOffspringsSum = 0;
            for (Node node : data) {
                numOffspringsSum += node.numOffsprings;
            }
            dataGroup._significance += (numOffspringsSum / (double) data.size());
        }

        /* Sort the results according to their significances */
        _results.sort();
        _results.reverse();

        return _results;
    }

    /**
     * Constructor
     */
    public HTMLDataExtractor(){
        this._minResultSize = 4;
    }

    /**
     * Get DOM tree from given file name
     * @param fileName
     * @return 0 if no error occurs
     */
    public int readFromFile(String fileName, String baseUri) {
        try {
            File inFile = new File(fileName);
            _document = Jsoup.parse(inFile, "UTF-8", baseUri);
            _body = _document.body();

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    /**
     * Get DOM tree from given URL
     * @param URL
     * @return 0 if no error occurs
     */
    public int readFromURL(String URL) {
        return readFromURL(URL, true, true);
    }
    public int readFromURL(String URL, boolean useInjectedHTML, boolean saveInjectedHtml) {
        if (useInjectedHTML == false) {
            return readRawHTMLFromURL(URL);
        } else {
            return readInjectedHTMLFromURL(URL, saveInjectedHtml);
        }
    }
    public int readRawHTMLFromURL(String URL){
        try {
            _document = Jsoup
                    .connect(URL)
                    .timeout(CONNECTION_TIMEOUT_LIMIT)
                    .get();
            _body = _document.body();

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int readInjectedHTMLFromURL(String URL, boolean saveInjectedHtml) {
        try {
            String HTML = null;

            SeleniumInjector seleniumInjector = new SeleniumInjector(SeleniumInjector.DevPlatform.WINDOWS, false);

            int errCode = seleniumInjector.render(URL);

            if (errCode == 0) {
                HTML = seleniumInjector.getHTML();
                _document = Jsoup.parse(HTML);
                _body = _document.body();

                /* Save the injected HTML into file */
                if (saveInjectedHtml == true) {
                    // Get current date time
                    DateFormat dateFormat = new SimpleDateFormat(" - yyyy-MM-dd HH-mm-ss");
                    Date date = new Date();
                    String currentDateTime = dateFormat.format(date);

                    // Save to output file
                    String outfile = seleniumInjector.getTitle() + currentDateTime + ".html";
                    if (seleniumInjector.saveToFile(outfile) == 0)
                        System.out.println("=== Successfully saved to " + outfile);
                }

            }

            seleniumInjector.close();
            return errCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Remove irrelevant elements with given tag names
     * @return
     */
    public int cleanDomTree(){
        _domTreeCleaner = new DomTreeCleaner(_body);
        return _domTreeCleaner.clean();
    }


    /**
     * Extract data from DOM tree
     * @return
     */
    public int extract(){
        return(extract(true, true));
    }
    public int extract(boolean considerTED, boolean considerArea) {
        if (considerTED == false && considerArea == false)
            throw new IllegalArgumentException("You have to use at least one factor for extraction!");

        try {
            /* Instantiate a DomTreeDataExtractor */
            _domTreeDataExtractor = new DomTreeDataExtractor(_body);
            _domTreeDataExtractor.setConsiderTED(considerTED);
            _domTreeDataExtractor.setConsiderArea(considerArea);

            /* Perform data extraction */
            int dataExtractErrCode = _domTreeDataExtractor.extractData();
            if (dataExtractErrCode != 0) {
                return dataExtractErrCode;
            }

            /* Filter the results */
            _domTreeDataExtractor.filterByMinResultSize(_minResultSize);

            /* Get the final results of data extraction */
            _results = _domTreeDataExtractor.getResults();

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }
    }


    /**
     * Apply a filtering strategy to refine the results
     * TODO: define more strategies
     */
    public int refine(){
        /* Rank results */
        getResults();

        /* Check if results null */
        if (_results == null) {
            System.err.println("Please call extract() first before refining!");
            return -1;
        }

        /* Refine DataGroups */
        double refineRatio = _results.refine();
        System.out.println("DataGroups refined to " + refineRatio);

        return 0;
    }
}