import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
//import org.jsoup.safety.Whitelist;

/**
 * Created by naco_siren on 3/26/17.
 */
public class HTMLDataExtractor {

    private Document _document;
    String baseURL;
    String[] removeTagNames;

//    HtmlCleaner cleaner = new HtmlCleaner();

    public HTMLDataExtractor(String baseURL, String[] removeTagNames){
        this.baseURL = baseURL;
        this.removeTagNames = removeTagNames;
    }

    private static String cleanHtmlFragment(String htmlFragment, String attributesToRemove) {
        return htmlFragment.replaceAll("\\s+(?:" + attributesToRemove + ")\\s*=\\s*\"[^\"]*\"","");
    }

    public void tail(Node node, ArrayList<Node> nodeArrayList){
        if(node instanceof Comment) nodeArrayList.add(node);
        if(node instanceof TextNode && ((TextNode) node).isBlank()) nodeArrayList.add(node);
    }

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
    public String readFromFile(String fileName)throws IOException{

        Document doc = Jsoup.parse(fileName, "UTF-8");

        return preprocess(doc);
    }

    /**
     *
     * @param URL
     * @return
     */
    public String readFromURL(String URL)throws IOException{

        Document doc = Jsoup.connect(URL).get();


        return preprocess(doc);
    }

    /**
     * Preprocessing: remove irrelevant Nodes/Elements
     * @return
     */
    public String preprocess(Document doc) throws IOException{

        Element mainBody = doc.getElementsByTag("body").get(0);
        Elements elesToRemove = new Elements();
        for(String removeTagName : removeTagNames){
            elesToRemove.addAll(mainBody.getElementsByTag(removeTagName));
        }
        for(Element ele : elesToRemove){
            ele.remove();
        }
        ArrayList<Node> nodesToDelete = new ArrayList<Node>();
        nodesToDelete = traverse(mainBody);
        for(Node node : nodesToDelete){
            node.remove();
        }
        String html = mainBody.toString();
        String attributesToRemove = "id|style|";


        html = cleanHtmlFragment(html, attributesToRemove);

        return html;
    }

    public ArrayList<Node> traverse(Node root) {
        ArrayList<Node> nodeArrayList = new ArrayList<Node>();
        Node node = root;
        int depth = 0;
        while (node != null) {
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    tail(node, nodeArrayList);
                    node = node.parentNode();
                    depth--;
                }
                tail(node, nodeArrayList);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
        return nodeArrayList;
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

    public static void main(String[] args) throws IOException {
        String[] removeTagNames = new String[]{"meta", "script", "title", "style"};
        String URL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        HTMLDataExtractor HTMLpreporcess = new HTMLDataExtractor(URL, removeTagNames);
        String mainbody = HTMLpreporcess.readFromURL(URL);
        System.out.println(mainbody);
    }
}