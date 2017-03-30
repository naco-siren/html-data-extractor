package gokurakujoudo;

import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;
import gokurakujoudo.dom_tree_helpers.DomTreeCleaner;
import gokurakujoudo.dom_tree_helpers.DomTreeDataExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.util.ArrayList;
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


        /* TODO: Apply a filtering strategy */
        _results.refine();

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
    public int readFromFile(String fileName) {
        try {
            _document = Jsoup.parse(fileName, "UTF-8");
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


    /**
     * Remove irrelevant elements with given tag names
     * @param tagNamesToRemove
     * @return
     */
    public int cleanDomTree(String[] tagNamesToRemove){
        _domTreeCleaner = new DomTreeCleaner(_body);
        return _domTreeCleaner.clean(tagNamesToRemove);
    }


    /**
     * Extract data from DOM tree
     * @return
     */
    public int extract(){
        try {
            /* Instantiate a DomTreeDataExtractor */
            _domTreeDataExtractor = new DomTreeDataExtractor(_body);

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


}