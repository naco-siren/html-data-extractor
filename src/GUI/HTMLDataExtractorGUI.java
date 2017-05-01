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
public class HTMLDataExtractorGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("HTML Data Extractor GUI");
        frame.setContentPane(new HTMLDataExtractorGUI()._mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    /* UI */
    private JPanel _mainPanel;
    private JTextField _inputURLTextField;
    private JButton _parseButton;
    private JCheckBox _areaCheckBox;
    private JCheckBox _nodeDistanceCheckBox;
    private JTextField _expectedRecordCountTextField;
    private JComboBox _outputFormatComboBox;

    /* Input: */
    private boolean _considerTED = false;
    private boolean _considerArea = false;
    private boolean _getXml = false;
    private boolean _getJson = false;

    /**
     * Constructor
     */
    public HTMLDataExtractorGUI() {
        _parseButton.addActionListener(new PrintResult());
        ActionListener actionListener = new ActionHandler();
        _areaCheckBox.addActionListener(actionListener);
        _nodeDistanceCheckBox.addActionListener(actionListener);
        _outputFormatComboBox.addActionListener(new actionListenerComboBox());
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
            } else if (selection == "json & xml") {
                _getXml = true;
                _getJson = true;
            }
        }
    }

    class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JCheckBox checkbox = (JCheckBox) event.getSource();

            if (checkbox == _areaCheckBox) {
                _considerArea = !_considerArea;
            } else if (checkbox == _nodeDistanceCheckBox) {
                _considerTED = !_considerTED;
            }
        }
    }

    ArrayList<String> output = new ArrayList<String>();
    class PrintResult implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){
            String title = "HTML Data Extractor\n";
            String expectedOutputDataCounts = _expectedRecordCountTextField.getText();
            String InputUrl = _inputURLTextField.getText();

            output.add(expectedOutputDataCounts);
            final ImageIcon icon = new ImageIcon("image.jpg");

            try {
                extractDataFromURL(InputUrl, _getXml, _getJson);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.print(output);
            JOptionPane.showMessageDialog(null, output,"HTML Data Extractor", JOptionPane.INFORMATION_MESSAGE, icon);
        }

    }

    /**
     * Main working method to do the data extraction
     * @param URL
     * @param outputHTML
     * @param outputJSON
     * @return 0 on success
     */
    public int extractDataFromURL(String URL, boolean outputHTML, boolean outputJSON) throws IOException {
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
}
