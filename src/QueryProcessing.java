import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.tartarus.snowball.EnglishSnowballStemmerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*This method processses the query to transform the 
 * query into tokens and stem t using the stemmer*/
public class QueryProcessing {
	public static Map<Integer, ArrayList<String>> stemmedQuery = new HashMap<Integer, ArrayList<String>>();
	static Map<String, Integer> stopWords = new HashMap<String, Integer>();
    public static void mainStart() {
		Tokenizer token = new Tokenizer();
		stopWords = token.getStopWords("C:\\Users\\Piyush\\Desktop\\Project 2\\stop_list");
		QueryProcessing q = new QueryProcessing();
		q.getProcessedQuery("C:\\Users\\Piyush\\Desktop\\Project 2\\topics.xml");
	}

	public void getProcessedQuery(String fileName) {
		// String processedQuery = new String();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			File file = new File(fileName);
			if (file.exists()) {
				Document doc = db.parse(file);
				Element documentElement = doc.getDocumentElement();
				NodeList queryList = documentElement
						.getElementsByTagName("topic");
				if (queryList != null && queryList.getLength() > 0) {
					for (int i = 0; i < queryList.getLength(); i++) {
						Node node = queryList.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) node;
							int num = Integer.parseInt(e.getAttribute("number"));
							
							
							//Thread.sleep(100000);
							NodeList nodeList = e.getElementsByTagName("query");
							String intialQuery = nodeList.item(0)
									.getChildNodes().item(0).getNodeValue();
							ArrayList<String> tempStemmedQuery = getStemmedQuery(intialQuery);
							stemmedQuery.put(num, tempStemmedQuery);
						}
					}
				} else {
					System.exit(1);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private ArrayList<String> getStemmedQuery(String intialQuery) {
		String s[] = intialQuery.split(" ");
		int j=0;
		ArrayList<String> query = new ArrayList<String>();
		try {
			for (int i = 0; i < s.length; i++) {
				String term = EnglishSnowballStemmerFactory.getInstance()
						.process(s[i].toLowerCase());
				if (stopWords.containsKey(term)) {
					continue;
				}
				query.add(j,term);
				j=j+1;

			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return query;
	}
}
