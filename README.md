# HTMLDataExtractor
Extract data records from web pages based on DOM tree structure and visual info.

## Environment Requirements
* OS: Windows
* Software: Mozilla firefox (32 bit)

## Execution Procedures
1.  Use the latest version of IntelliJ IDEA to import the project.
2.  Run the Main class _src/Main.java_, a Swing GUI will pop up.
    1. Paste the URL of the web page to extract data records from to the "Source URL"
	text input.
	2. The _Expected records' count_ input can be left blank as it won't affect the result.
	3. Select at least one of the two checkbox for extraction strategy: _area_ and _node distance_. 
	4. Choose one output format from _json_, _xml_, or _json & xml_. 
	5. Finally, click on the "parse" button execute the extraction.
	The extracted records will show at the bottom's text edit.
4.  To test on different web pages automatically, please refer to _test/TestHTMLDataExtractor.java_, where you can modify and run the method _testWebpages()_.
