import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;

/**
 * Created by naco_siren on 3/26/17.
 */
public class HTMLDataExtractor {

    private Document _document;

    // TODO: Data structure for outputting extracted data
    private ArrayList<ArrayList<Node>> _results;

    /**
     * Constructor
     */
    public HTMLDataExtractor(){

    }

    /**
     *
     * @param fileName
     * @return
     */
    public int readFromFile(String fileName){

        try {


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }


        return preprocess();
    }


    /**
     *
     * @param URL
     * @return
     */
    public int readFromURL(String URL){
        try {
            _document = Jsoup
                    .connect(URL)
                    .timeout(4000)
                    .get();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }

        return preprocess();
    }

    /**
     * Preprocessing: remove irrelevant Nodes/Elements
     * @return
     */
    private int preprocess(){
        try {
            Element body = _document.body();

            // TODO:
            body.getElementsByTag("script").remove();
            body.getElementsByTag("meta???").remove();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }
        return 0;
    }


    /**
     * Entrance for extraction
     * @return
     */
    public int extract(){
        try {
            // TODO: Pre-order traverse the DOM tree


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return -1;
        }
        return 0;
    }

}
