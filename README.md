# HTMLDataExtractor
Extract data records from web pages based on DOM tree structure and visual info.

## Environment Requirements
* OS: Windows
* Software: Mozilla firefox (32 bit)

## Execution Procedures
1.  Use the latest version of IntelliJ IDEA to import the project.
2.  Run the Main class `src/Main.java`, a Swing GUI will pop up.
    1. Paste the URL of the web page to extract data records from to the `Source URL`
	text input.
	2. The `Expected records` count_ input can be left blank as it won't affect the result.
	3. Select at least one of the two checkbox for extraction strategy: `area` and `node distance`. 
	4. Choose one output format from `json`, `xml`, or `json & xml`. 
	5. Finally, click on the "parse" button execute the extraction.
	The extracted records will show at the bottom's text edit.
4.  To test on different web pages automatically, please refer to `test/TestHTMLDataExtractor.java`, where you can modify and run the method `testWebpages()`.
