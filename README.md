# HTMLDataExtractor
Extract data records from web pages based on DOM tree structure and visual info.

## Environment Requirements
* OS: Windows
* Software: Mozilla firefox (32 bit)

## Execution Procedures
1.  Use the latest version of IntelliJ IDEA to import the project.
2.  Run the Main class `src/Main.java`, a Swing GUI will pop up, as shown in the screenshot below:

    ![GUI](https://6zndqg.bn1302.livefilestore.com/y4md175y362r-eH8igxo_-WlsVss0SMtrxx3cDTEUeUN-vhxzTOcjg4xWTkxW1qVeIToKMHxSWFcalIgedT6FawpNDDZjJ8E1n-36p5-Plac-I5UNyYjNZJWn_U3osmoKUC25-VR7Dlx9vHEAhb3HfAVVwy_yZWFZp7mAeSKpF2mBu8nPRdevsjLZ5HlPb9TphC1s95IIQygMMqKyXX0bJ-Vg?width=1024&height=598&cropmode=none)
    1. Paste the URL of the web page to extract data records from to the `Source URL`
	text input.
    2. The `Expected records count` input can be left blank as it won't affect the result.
    3. Select at least one of the two checkbox for extraction strategy: `area` and `node distance`. 
    4. Choose one output format from `json`, `xml`, or `json & xml`. 
    5. Finally, click on the "parse" button execute the extraction. The extracted records will show at the bottom's text edit, as shown below:
        * Google Scholar:
		![Google Scholar output example](https://65ntqg.bn1302.livefilestore.com/y4mBScAsQTwlcQ0B7ENw1ouqcqmtC8s5n1bseL7zJYSmPaQi9VXYk4qgvySVh2rh46Pqs_wPyrtxG_b_CxaA9YySSg4Lfwp4MZYMWz-n67VlItiIL3x5ybJ7UcGGwWuzWDbhu2Qk9Vs9-Yvw54KB1FByTMwDHhwq4WxfF1IuycF5KNnywql61uYzF9R8VOuVhvdszDsmOgsRarSgReK6zasFQ?width=1024&height=358&cropmode=none)
        * Best Buy:
    	![Best Buy output example](https://hgj7oa.bn1302.livefilestore.com/y4mKBEytxW0O8GF1mpaVDmK2GKy2Cy7s3wT1hYo1yadMkLD6_e6D39R9OEELqwBAcYN-GlVijAzLGhq9stUmgHAs63l0Sw1dG4oVlWJfegc8_PcQ8cfI9Y5H7Lgo9DGcRf8AAkDfgUwUurSkcfgMGyYzFf5gx5aqZssqHdfvUPiDtgLjXPDEqwPGNquhJCYVjrRp-xFtGul52nad0KxaW-jsQ?width=735&height=1171&cropmode=none)
	
4.  To test on different web pages automatically, please refer to `test/TestHTMLDataExtractor.java`, where you can modify and run the method `testWebpages()`.
