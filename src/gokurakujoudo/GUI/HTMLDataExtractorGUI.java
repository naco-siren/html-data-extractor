package gokurakujoudo.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gokurakujoudo.HTMLDataExtractor;
import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;


import java.util.ArrayList;

/**
 * Created by yuqiz on 4/30/2017.
 */
public class HTMLDataExtractorGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("HTML Data Extractor gokurakujoudo.GUI");
        frame.setContentPane(new HTMLDataExtractorGUI()._mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    /* UI */
    private JPanel _mainPanel;

    private JTextField _inputURLTextField;
    private JTextField _expectedRecordCountTextField;
    private JButton _parseButton;

    private JCheckBox _areaCheckBox;
    private JCheckBox _TEDCheckBox;

    private JComboBox _outputFormatComboBox;
    private JTextArea _outputTextArea;

    private final ImageIcon _msgIcon = new ImageIcon("image.jpg");

    /* Input: */
    private boolean _considerTED = false;
    private boolean _considerArea = false;

    private boolean _outputXML = false;
    private boolean _outputJSON = false;

    /* Tools: */
    private HTMLDataExtractor _htmlDataExtractor;

    /* Output: */
    private DataGroups _results;
    private StringBuilder _resultsText;


    /**
     * Constructor
     */
    public HTMLDataExtractorGUI() {
        _parseButton.addActionListener(new PrintResult());
        _areaCheckBox.setSelected(true);
        _TEDCheckBox.setSelected(true);
    }


    class PrintResult implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){

            /* Data source URL and expected data count */
            String dataSourceURL = _inputURLTextField.getText();
            String expectedOutputDataCount = _expectedRecordCountTextField.getText();

            /* Extraction factor */
            _considerArea = _areaCheckBox.isSelected();
            _considerTED = _TEDCheckBox.isSelected();

            /* Output format */
            int outputFormatIndex = _outputFormatComboBox.getSelectedIndex();
            switch (outputFormatIndex) {
                case 0:
                    _outputJSON = true;
                    _outputXML = false;
                    break;
                case 1:
                    _outputJSON = false;
                    _outputXML = true;
                    break;
                case 2:
                    _outputXML = true;
                    _outputJSON = true;
                    break;
            }

            
            /* Check if no factors are selected */
            if (_considerArea == false && _considerTED == false) {
                JOptionPane.showMessageDialog(null, "Please specify at least a factor for data extraction!","Factors missing!", JOptionPane.INFORMATION_MESSAGE, _msgIcon);
                return;
            } 

            /* Execute data extraction */
            _outputTextArea.setText(">>> Performing data record extraction...");
            if (extractDataFromURL(dataSourceURL, _outputXML, _outputJSON) == 0) {
                _outputTextArea.setText(_resultsText.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Something is wrong performing data extraction!","Error!", JOptionPane.ERROR_MESSAGE, _msgIcon);
            }
        }

    }


    /**
     * Main working method to do the data extraction
     * @param URL data source webpage's URL
     * @param outputXML whether output XML
     * @param outputJSON whether output JSON
     * @return 0 on success
     */
    public int extractDataFromURL(String URL, boolean outputXML, boolean outputJSON){
        /* Instantiate an HTMLDataExtractor */
        _htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        _htmlDataExtractor.readFromURL(URL);

        /* Clean DOM tree */
        _htmlDataExtractor.cleanDomTree();

        /* Perform extraction */
        _htmlDataExtractor.setMinResultSize(2);
        if (_htmlDataExtractor.extract(_considerTED, _considerArea) == 0) {

            /* Refine _results using default strategy */
            _htmlDataExtractor.refine();

            /* Collect and clean up the _results */
            _results = _htmlDataExtractor.getResults();
            _results.clean();

            /* Build output text */
            _resultsText = new StringBuilder();
            for (int i = 0; i < _results.size(); i++) {
                DataGroup dataGroup = _results.get(i);
                _resultsText.append("\n=== No." + (i+1) + " data group, size: " + dataGroup.size() + " ===\n");

                if (_outputJSON) {
                    ArrayList<String> outputJSONs = dataGroup.getJSONs();
                    _resultsText.append("\n");
                    for (int j = 0; j < outputJSONs.size(); j++) {
                        _resultsText.append(outputJSONs.get(j));
                        _resultsText.append("\n");
                    }
                }

                if (_outputXML) {
                    ArrayList<String> outputXMLs = dataGroup.getHTMLs();
                    _resultsText.append("\n");
                    for (int j = 0; j < outputXMLs.size(); j++) {
                        _resultsText.append(outputXMLs.get(j));
                        _resultsText.append("\n");
                    }
                }

            }

            return 0;
        } else {
            return -1;
        }
    }
}
