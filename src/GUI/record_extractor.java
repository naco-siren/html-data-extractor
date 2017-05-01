package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gokurakujoudo.HTMLDataExtractor;
import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;


import java.io.IOException;

import java.util.ArrayList;

/**
 * Created by yuqiz on 4/30/2017.
 */
public class record_extractor {

    private JPanel panel1;
    private JTextField inputurl;
    private JButton parse;
    private JCheckBox areaCheckBox;
    private JCheckBox nodeDistanceCheckBox;
    private JTextField ErecordN;
    private JComboBox select;
    private boolean _considerTED = false;
    private boolean _considerArea = false;
    private boolean _getXml = false;
    private boolean _getJson = false;
    public record_extractor() {
        parse.addActionListener(new PrintResult());
        ActionListener actionListener = new ActionHandler();
        areaCheckBox.addActionListener(actionListener);
        nodeDistanceCheckBox.addActionListener(actionListener);
        select.addActionListener(new actionListenerComboBox());
    }

    class actionListenerComboBox implements ActionListener{
        public void actionPerformed(ActionEvent event) {
            JComboBox combo = (JComboBox) event.getSource();
            String selection = (String) combo.getSelectedItem();
            System.out.print(selection);
            if (selection == "json") {
                _getJson = true;
            } else if (selection == "xml") {
                _getXml = true;
            } else if (selection == "json&xml") {
                _getXml = true;
                _getJson = true;
            }
        }
    }

    class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JCheckBox checkbox = (JCheckBox) event.getSource();
            if (checkbox == areaCheckBox) {
                _considerArea = !_considerArea;
                //System.out.println(_considerArea);
                //System.out.println("Checkbox #1 is clicked");
            } else if (checkbox == nodeDistanceCheckBox) {
                _considerTED = !_considerTED;
                //System.out.println("Checkbox #2 is clicked");
            }
        }
    }
    ArrayList<String> output = new ArrayList<String>();
    class PrintResult implements ActionListener{
        @Override

        public void actionPerformed(ActionEvent event){
            String title = "HTML Data Extrator\n";
            String expectedOutputDataCounts = ErecordN.getText();
            String InputUrl = inputurl.getText();

            //String[] GetDomainName = InputUrl.split("/");

            //String testing = "=== Testing on "+ GetDomainName[2] +" ===";
            output.add(expectedOutputDataCounts);
            final ImageIcon icon = new ImageIcon("image.jpg");



            try {
                testWebpage(InputUrl, _getXml, _getJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(output);
            JOptionPane.showMessageDialog(null, output,"HTML Data Extrator", JOptionPane.INFORMATION_MESSAGE, icon);
        }

    }

    public int testWebpage(String URL, boolean outputHTML, boolean outputJSON) throws IOException {
        /* Instantiate an HTMLDataExtractor */
        //System.out.print("i'm in!");
        HTMLDataExtractor htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        htmlDataExtractor.readFromURL(URL);

        /* Clean DOM tree */
        htmlDataExtractor.cleanDomTree();

        /* Perform extraction */
        htmlDataExtractor.setMinResultSize(2);
        if (htmlDataExtractor.extract(_considerTED, _considerArea) == 0) {

            /* Refine results using default strategy */
            htmlDataExtractor.refine();

            /* Collect and clean up the results */
            DataGroups results = htmlDataExtractor.getResults();
            results.clean();

            /* Output the results */
            int dataCount = 0;
            for (int i = 0; i < results.size(); i++) {
                DataGroup dataGroup = results.get(i);
                dataCount += dataGroup.size();

                //System.out.println();
                //System.out.println("*** No. " + i + ", " + dataGroup);

                /* HTML format */
                if (outputHTML) {
                    ArrayList<String> dataHTMLs = dataGroup.getHTMLs();
                    for (String dataHTML : dataHTMLs) {
                        output.add(dataHTML+"\n");
                    }
                }

                /* JSON format */
                if (outputJSON) {
                    ArrayList<String> dataJSONs = dataGroup.getJSONs();
                    for (String dataJSON : dataJSONs) {
                        //Prettify_json.readd(dataJSON);
                        output.add(dataJSON + "\n");
                    }
                }
            }

            return dataCount;
        } else {
            return -1;
        }
    }




    public static void main(String[] args) {
        JFrame frame = new JFrame("record_extractor");
        frame.setContentPane(new record_extractor().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
