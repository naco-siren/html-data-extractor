import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haosun on 3/25/17.
 */
public class HTMLCleaner {
    String baseURL;
    String[] removeTagNames;

    public HTMLCleaner(String baseURL, String[] removeTagNames){
        this.baseURL = baseURL;
        this.removeTagNames = removeTagNames;
    }

    public HTMLCleaner(){
        baseURL = null;
        removeTagNames = null;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setRemoveTagNames(String[] removeTagNames) {
        this.removeTagNames = removeTagNames;
    }

    public String[] getRemoveTagNames() {
        return removeTagNames;
    }

    public void tail(Node node, ArrayList<Node> nodeArrayList){
        if(node instanceof Comment) nodeArrayList.add(node);

        if(node instanceof TextNode && ((TextNode) node).isBlank()) nodeArrayList.add(node);
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

    public Element clean() throws IOException {
        Document doc = Jsoup.connect(baseURL).get();
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

        return mainBody;

    }

    public static void main(String[] args) throws IOException {
        String[] removeTagNames = new String[]{"meta", "script", "title", "style"};
        String URL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        HTMLCleaner htmlCleaner = new HTMLCleaner(URL, removeTagNames);

        Element mainBody = htmlCleaner.clean();

        return;
    }
}
