import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ScoringFunctions {
	/*---------------------------------------------------------------------------------*/
	// termIds.get(termId).get("noOfDoc");
	QueryProcessing q = new QueryProcessing();
	// docid,totalterms
	static Integer V = 0;
	static int totalScoreMore = 0;
	static int totalTimes = 0;
	public static Map<Integer, Map<Integer, Double>> scoresOfDoc = new HashMap<Integer, Map<Integer, Double>>();
	public static TreeMap<Integer, HashMap<Integer, Integer>> docTermFreq = new TreeMap<Integer, HashMap<Integer, Integer>>();
	public static Map<Integer, Integer> recordOfDoc = new HashMap<Integer, Integer>();
	// docid->doc vector for deno
	public static Map<Integer, Double> scoreOfDoc = new HashMap<Integer, Double>();
	public static Map<Integer, String> docIds = new HashMap<Integer, String>();
	// query number and terms
	public static Map<Integer, ArrayList<String>> stemmedQuery = new HashMap<Integer, ArrayList<String>>();
	public static HashMap<Integer, Integer> termInDoc = new HashMap<Integer, Integer>();

	/*
	 * Term-> TermId -> 1245 NumberOfDocContaining-> 24566
	 */
	public static Map<String, HashMap<String, Integer>> termIds = new HashMap<String, HashMap<String, Integer>>();
	/*
	 * DocId -> TermId -> Occurences TermID -> Occurences
	 */
	public static Map<Integer, HashMap<Integer, Integer>> docOccurence = new HashMap<Integer, HashMap<Integer, Integer>>();
	static int totalDocumentsLength = 0;
	static int totalFreq = 0;
	static int check = 0;
	static int checking = 0;

	/*---------------------------------------------------------------------------------*/
	public static void allstemmed() {
		//System.out.println(stemmedQuery.size());
		// Map<Integer, ArrayList<String>>
		// stemmedQueryd=ScoringFunctions.stemmedQuery;
		for (Map.Entry<Integer, ArrayList<String>> term : stemmedQuery
				.entrySet()) {
			ArrayList<String> terms = term.getValue();
			for (int i = 0; i < terms.size(); i++) {
				ScoringFunctions.getListingForTerm(terms.get(i));
			}
		}
	}

	public void getAllDocName() {
		try {
			FileInputStream stream = new FileInputStream(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\docids.txt");
			DataInputStream in = new DataInputStream(stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 1;

			while ((strLine = br.readLine()) != null) {
				String s[] = strLine.split("\\t");
				docIds.put(Integer.parseInt(s[0]), s[1]);
				i = i + 1;
			}
			in.close();
		} catch (Exception e) {
			// System.out.println(e);

		}
	}

	public static void getListingForDocuments() {

		try {
			BufferedReader br1 = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\doc_index.txt"));
			for (int i = 0; i < docIds.size() - 1; i++) {
				int distinctTerms = 0;// Distinct terms: 25
				int totalTerms = 0;// Total terms: 501
				String str;
				int docId = 0;
				while ((str = br1.readLine()) != null) {
					String afterSplit[] = str.split("\\t");
					docId = Integer.parseInt(afterSplit[0]);
					if (docId > i) {
						break;
					}
					if (docId == i) {
						int perTermId = afterSplit.length - 2;
						totalTerms = totalTerms + perTermId;
						distinctTerms = distinctTerms + 1;
					}
				}
				recordOfDoc.put(i, totalTerms);
				totalDocumentsLength = totalDocumentsLength + totalTerms;
			}
			br1.close();
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	// getting the queries in processed form
	public static void calculateTheOccurenceOfTerms() {
		QueryProcessing q = new QueryProcessing();
		q.mainStart();
		stemmedQuery = QueryProcessing.stemmedQuery;
		// calculate the occurence of term in all documents
		for (Map.Entry<Integer, ArrayList<String>> entry : stemmedQuery
				.entrySet()) {
			ArrayList<String> words = entry.getValue();
			for (int j = 0; j < words.size(); j++) {
				getListingForTerm(words.get(j));
			}
		}
	}

	// getting the total number of docs containing the terms and termids
	// using the map termIds
	public static void getListingForTerm(String s) {
		//System.out.println("did i reach here");
		int termId = 0;

		boolean found = false;
		int documentsContainingTerm = 0;
		int frequencyIncorpus = 0;
		int offSet = 0;
		try {
			//System.out.println("2");
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
			String read;
			//System.out.println("3");
			while ((read = br.readLine()) != null) {
				//System.out.println("4");
				String split[] = read.split("\\t");
				//System.out.println("hello 5");
				//System.out.println(split[0]);
				//System.out.println(split.length);
				if (split[1].equals(s)) {
					//System.out.println("6");
					termId = Integer.parseInt(split[0]);
					//System.out.println("7");
					found = true;
					break;
				}
			}
			br.close();
			if (found) {
				BufferedReader br1 = new BufferedReader(new FileReader(
						"C:\\Users\\Piyush\\Desktop\\Project 2\\term_info.txt"));
				//System.out.println("8");
				while ((read = br1.readLine()) != null) {
					String split[] = read.split("\\t");
					//System.out.println("9");
					int termIndex = Integer.parseInt(split[0]);
					if (termIndex == termId) {
						//System.out.println("10");
						offSet = Integer.parseInt(split[1]);
						frequencyIncorpus = Integer.parseInt(split[2]);
						documentsContainingTerm = Integer.parseInt(split[3]);
					}
				}
				br1.close();
				HashMap<String, Integer> values = new HashMap<String, Integer>();
				values.put("termid", termId);
				values.put("freqInCor", frequencyIncorpus);
				values.put("noOfDoc", documentsContainingTerm);
				values.put("offset", offSet);
				//System.out.println("Am i reaching here");
				termIds.put(s, values);

			} else {
				// System.err.println("Unable to find the term");
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	// calculate the frequency of terms in
	public void setDocTermFreq() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\doc_index.txt"));
			String line;
			int docId = 1;
			HashMap<Integer, Integer> termFreq = new HashMap<Integer, Integer>();
			while ((line = br.readLine()) != null) {
				String split[] = line.split("\\t");
				int docId1 = Integer.parseInt(split[0]);
				if (docId1 == docId) {
					int termId = Integer.parseInt(split[1]);
					int freq = split.length - 2;
					termFreq.put(termId, freq);
				} else {
					docTermFreq.put(docId, termFreq);
					docId = docId1;
					termFreq = new HashMap<Integer, Integer>();
					int termId = Integer.parseInt(split[1]);
					int freq = split.length - 2;
					termFreq.put(termId, freq);
				}
			}
			docTermFreq.put(docId, termFreq);
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	public static void setTheOccurence() {
		try {
			for (Map.Entry<String, HashMap<String, Integer>> entry : termIds
					.entrySet()) {
				HashMap<String, Integer> values = entry.getValue();
				int termID = values.get("termid");
				int offset = values.get("offset");
				getInvertedListForTheTerm(termID, offset);
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	// get the occurence of the term in documents
	public static void getInvertedListForTheTerm(int termId, int offSet) {
		try {
			RandomAccessFile ra = new RandomAccessFile(new File(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\term_index.txt"),
					"r");
			ra.seek(offSet);
			String termInfo = ra.readLine();
			String splitTerms[] = termInfo.split("\\t");
			int sum = 0;
			for (Map.Entry<Integer, String> entry : docIds.entrySet()) {
				int docId = entry.getKey();
				int termFreqInDoc = 0;
				for (int i = 1; i < splitTerms.length; i++) {
					String split[] = splitTerms[i].split(":");
					sum += Integer.parseInt(split[0]);
					if (sum >= docId) {
						if (!(sum == docId)) {
							//System.err
							//		.println("term does not exists in the given document");
							return;
						} else {
							int posSum = 0;
							for (int j = i; j < splitTerms.length; j++) {
								String split1[] = splitTerms[j].split(":");
								int doc = Integer.parseInt(split1[0]);

								if (doc == 0 || sum == docId) {
									posSum = posSum
											+ Integer.parseInt(split1[1]);
									++termFreqInDoc;
									// positions.add(posSum);
									sum += Integer.parseInt(split1[0]);
								} else {
									break;
								}

							}
							break;
						}
					}
				}
				if (termFreqInDoc > 0) {
					totalFreq = totalFreq + 1;
				}
				if (docOccurence.containsKey(docId)) {
					HashMap<Integer, Integer> values = docOccurence.get(docId);
					values.put(termId, termFreqInDoc);
					docOccurence.put(docId, values);
					check = check + 1;

				} else {
					checking = checking + 1;
					HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
					values.put(termId, termFreqInDoc);
					docOccurence.put(docId, values);
				}

				// System.out.println();
				ra.close();
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

	/*------------------------------------------------------------------------------------*/
	public static HashSet<Integer> getAllDocToProcess(ArrayList<String> query)
			throws Exception {
		HashSet<Integer> docSet = new HashSet<Integer>();
		for (String s : query) {
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\term_info.txt"));
			BufferedReader br1 = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
			String line1;
			int termId = 0;
			br1.readLine();
			br1.readLine();
			while ((line1 = br1.readLine()) != null) {
				String split[] = line1.split("\\t");
				if (split[1].equals(s)) {
					termId = Integer.parseInt(split[0]);
					break;
				}
			}
			br1.close();
			String line;
			int offset = 0;
			while ((line = br.readLine()) != null) {
				String split[] = line.split("\\t");
				if (split[0].equals(Integer.toString(termId))) {
					offset = Integer.parseInt(split[1]);
					break;
				}
			}
			br.close();
			RandomAccessFile raf = new RandomAccessFile(new File(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\term_index.txt"),
					"r");
			raf.seek(offset);
			String termInfo = raf.readLine();
			int sum = 0;
			String splitTerms[] = termInfo.split("\\t");
			for (int i = 1; i < splitTerms.length; i++) {
				String split[] = splitTerms[i].split(":");
				sum = sum + Integer.parseInt(split[0]);
				if (!docSet.contains(sum)) {
					docSet.add(sum);
				}
			}
			raf.close();
		}
		return docSet;
	}

	public void okapiTF() throws Exception {
		// Query q=new Query();
		double avgLen = totalDocumentsLength / docIds.size();
		Map<Integer, ArrayList<String>> queryMap = stemmedQuery;
		for (Map.Entry<Integer, ArrayList<String>> e : queryMap.entrySet()) {
			LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
			ArrayList<String> query = e.getValue();

			HashSet<Integer> docSet = getAllDocToProcess(query);
			scores = processDocSetOKAPI(docSet, query, e.getKey(), avgLen);
			scores = (LinkedHashMap<Integer, Double>) sortByComparator(scores);
			int rank = 1;
			for (Entry<Integer, Double> e2 : scores.entrySet()) {
				String docName = docIds.get(e2.getKey());
				System.out.println(e.getKey() + "\t" + "0" + "\t" + docName
						+ "\t" + (rank++) + "\t" + e2.getValue() + "\t"
						+ "run1");
			}
		}

	}

	public void TFIDF() throws Exception {
		// Query q=new Query();
		double avgLen = totalDocumentsLength / docIds.size();
		Map<Integer, ArrayList<String>> queryMap = stemmedQuery;
		for (Map.Entry<Integer, ArrayList<String>> e : queryMap.entrySet()) {
			LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
			ArrayList<String> query = e.getValue();

			HashSet<Integer> docSet = getAllDocToProcess(query);
			scores = processDocSetOKAPI(docSet, query, e.getKey(), avgLen);
			scores = (LinkedHashMap<Integer, Double>) sortByComparator(scores);
			int rank = 1;
			for (Entry<Integer, Double> e2 : scores.entrySet()) {
				String docName = docIds.get(e2.getKey());
				System.out.println(e.getKey() + "\t" + "0" + "\t" + docName
						+ "\t" + (rank++) + "\t" + e2.getValue() + "\t"
						+ "run1");
			}
		}

	}

	private static int getTermId(String s) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(
				"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
		String line;
		int termId = 0;
		br.readLine();
		br.readLine();
		while ((line = br.readLine()) != null) {
			String split[] = line.split("\\t");
			if (split[1].equals(s)) {
				termId = Integer.parseInt(split[0]);
				break;
			}
		}
		br.close();
		return termId;
	}

	public void setTermInDocMap() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"C:\\Users\\Piyush\\Desktop\\Project 2\\term_info.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String split[] = line.split("\\t");
			termInDoc.put(Integer.parseInt(split[0]),
					Integer.parseInt(split[3]));
		}
		br.close();

	}

	public static LinkedHashMap<Integer, Double> processDocSetOKAPI(
			HashSet<Integer> docSet, ArrayList<String> query, int tId,
			double avgLen) throws Exception {
		LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
		double averageLengthOfQuery = calculateTheAverageLengthOfQuery();
		ArrayList<Double> qv = new ArrayList<Double>();
		for (String s : query) {
			int j = Collections.frequency(query, s);
			double component = getTermVector(s, j, query.size(),
					averageLengthOfQuery);
			qv.add(component);
		}
		for (Integer docId : docSet) {
			double dsquare = 0.0;
			double qsquare = 0.0;
			double cp = 0.0;
			double docSize = 0.0;
			if (recordOfDoc.containsKey(docId)) {
				docSize = recordOfDoc.get(docId);
			}
			ArrayList<Double> dv = new ArrayList<Double>();
			ArrayList<Double> dv1 = new ArrayList<Double>();
			HashMap<Integer, Integer> freqMap = docTermFreq.get(docId);
			for (String s : query) {

				int termId = getTermId(s);

				double freq = 0.0;
				if (freqMap.containsKey(termId)) {
					freq = freqMap.get(termId);
				}
				//System.out.println(termId);
				//System.out.println(termIds.size());

				double oktf = freq / (freq + 0.5 + 1.5 * (docSize / avgLen));
				dv.add(oktf);

			}
			for (int i = 0; i < dv.size(); i++) {
				cp = cp + qv.get(i) * dv.get(i);
				qsquare = qsquare + qv.get(i) * qv.get(i);

			}

			for (Entry<Integer, Integer> e : freqMap.entrySet()) {
				double oktf = e.getValue()
						/ (e.getValue() + 0.5 + 1.5 * (docSize / avgLen));
				dv1.add(oktf);

			}

			for (Double oktf : dv1) {
				dsquare = dsquare + oktf * oktf;
			}

			double score = cp / (Math.sqrt(dsquare) * Math.sqrt(qsquare));
			// double nScore=Math.round(score*10000000000.0)/10000000000.0;
			scores.put(docId, score);
		}

		return scores;
	}

	public static LinkedHashMap<Integer, Double> processDocSetTFIDF(
			HashSet<Integer> docSet, ArrayList<String> query, int tId,
			double avgLen) throws Exception {
		LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
		double averageLengthOfQuery = calculateTheAverageLengthOfQuery();
		ArrayList<Double> qv = new ArrayList<Double>();
		for (String s : query) {
			int j = Collections.frequency(query, s);
			double component = getTermVector(s, j, query.size(),
					averageLengthOfQuery);
			qv.add(component);
		}
		for (Integer docId : docSet) {
			double dsquare = 0.0;
			double qsquare = 0.0;
			double cp = 0.0;
			double docSize = 0.0;
			if (recordOfDoc.containsKey(docId)) {
				docSize = recordOfDoc.get(docId);
			}
			ArrayList<Double> dv = new ArrayList<Double>();
			ArrayList<Double> dv1 = new ArrayList<Double>();
			HashMap<Integer, Integer> freqMap = docTermFreq.get(docId);
			for (String s : query) {

				int termId = getTermId(s);

				double freq = 0.0;
				if (freqMap.containsKey(termId)) {
					freq = freqMap.get(termId);
				}
				/*
				 * System.out.println(termId);
				 * System.out.println(termIds.size());
				 */

				double oktf = freq / (freq + 0.5 + 1.5 * (docSize / avgLen));
				double tfidf = oktf
						* (Math.log(3468 / termInDoc.get(termId)) / Math.log(2));
				dv.add(oktf);

			}
			for (int i = 0; i < dv.size(); i++) {
				cp = cp + qv.get(i) * dv.get(i);
				qsquare = qsquare + qv.get(i) * qv.get(i);

			}

			for (Entry<Integer, Integer> e : freqMap.entrySet()) {
				double oktf = e.getValue()
						/ (e.getValue() + 0.5 + 1.5 * (docSize / avgLen));
				dv1.add(oktf);

			}

			for (Double oktf : dv1) {
				dsquare = dsquare + oktf * oktf;
			}

			double score = cp / (Math.sqrt(dsquare) * Math.sqrt(qsquare));
			// double nScore=Math.round(score*10000000000.0)/10000000000.0;
			scores.put(docId, score);
		}

		return scores;
	}

	private static Map sortByComparator(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private void printScores() {
		for (Map.Entry<Integer, Map<Integer, Double>> singleQuery : scoresOfDoc
				.entrySet()) {
			int queryId = singleQuery.getKey();
			int count = 1;
			Map<Integer, Double> docs = singleQuery.getValue();
			for (Map.Entry<Integer, Double> everyDoc : docs.entrySet()) {
				int docid = everyDoc.getKey();
				Double finalScoreOfDoc = everyDoc.getValue();
				System.out.println(queryId + " " + 0 + " " + docIds.get(docid)
						+ " " + count + " " + finalScoreOfDoc + " " + "run1");
				count = count + 1;
			}
		}
	}

	/*---------------------------------------------------------------------------------*/
	// calculate the score of the single query for all docs
	public void calculateScore(Integer queryId,
			Map<Integer, Double> queryVector,
			Map<Integer, HashMap<Integer, Double>> docVector) {
		for (Map.Entry<Integer, HashMap<Integer, Double>> entry : docVector
				.entrySet()) {
			int docId = entry.getKey();
			double score = 0.0;
			double numerator = 0.0;
			double denominatorOfDoc = 0.0;
			double denominatorOfQuery = 0.0;
			HashMap<Integer, Double> termIdVec = entry.getValue();
			for (Map.Entry<Integer, Double> entryOfQuery : queryVector
					.entrySet()) {
				int termId = entryOfQuery.getKey();
				double vecInDoc = termIdVec.get(termId);
				double vecInQuery = entryOfQuery.getValue();
				numerator = numerator + (vecInDoc * vecInQuery);
				denominatorOfDoc = scoreOfDoc.get(docId);
				denominatorOfQuery = denominatorOfQuery
						+ (vecInQuery * vecInQuery);
			}
			if (denominatorOfDoc > 0) {
				denominatorOfDoc = Math.sqrt(denominatorOfDoc);
			}
			if (denominatorOfQuery > 0) {
				denominatorOfQuery = Math.sqrt(denominatorOfQuery);
			}
			if (numerator == 0
					|| ((denominatorOfDoc == 0) && (denominatorOfQuery == 0))) {
				score = 0.0;
			} else {
				score = numerator / (denominatorOfDoc * denominatorOfQuery);
			}
			totalTimes = totalTimes + 1;
			HashMap<Integer, Double> values = new HashMap<Integer, Double>();
			values.put(docId, score);
			scoresOfDoc.put(queryId, values);
			try {
				if (score > 0.0) {
					totalScoreMore = totalScoreMore + 1;
				}
			} catch (Exception e) {
			}

		}
	}

	// calculate the average length of the queries
	public static double calculateTheAverageLengthOfQuery() {
		double averageLength = 0.0;
		int size = 0;
		for (Map.Entry<Integer, ArrayList<String>> entry : stemmedQuery
				.entrySet()) {
			size = size + entry.getValue().size();
		}
		averageLength = size / stemmedQuery.size();
		return averageLength;
	}

	// calculates the vector of single term
	public static Double getTermVector(String term, int count, int length,
			double averageLengthOfQuery) {
		Double vector = 0.0;
		vector = (count / (count + 0.5 + 1.5 + (length / averageLengthOfQuery)));
		return vector;
	}

	// getting the vector for all the terms
	public HashMap<Integer, Double> getDocVectorForAllTerms(int docID,
			String docName, Map<Integer, Double> queryVector,
			double avaerageLengthOfDoc, boolean isTFIdf) {
		HashMap<Integer, Double> docVector = new HashMap<Integer, Double>();
		int totalDocs = docIds.size();
		for (Map.Entry<Integer, Double> entry : queryVector.entrySet()) {
			int numberOfDocsContaining = 0;
			double tfIdf = 1.0;
			int termId = entry.getKey();
			if (isTFIdf) {
				// Map<Integer, HashMap<Integer, Integer>> docOccurence
				for (Map.Entry<Integer, HashMap<Integer, Integer>> singleDoc : docOccurence
						.entrySet()) {
					if (singleDoc.getValue().get(termId) != 0) {
						numberOfDocsContaining = numberOfDocsContaining + 1;
					}
				}
				tfIdf = Math.log(totalDocs / numberOfDocsContaining);
				// System.out.println(tfIdf);
				// System.out.println(totalDocs);
				// System.out.println(numberOfDocsContaining);
			}
			HashMap<Integer, Integer> termIDOcur = docOccurence.get(docID);
			if (!(termIDOcur.containsKey(termId))) {
				docVector.put(termId, 0.0);
			} else {
				int occurence = termIDOcur.get(termId);
				Double vector = 0.0;
				if (recordOfDoc.get(docID) != null) {
					vector = (occurence / (occurence + 0.5 + 1.5 + (recordOfDoc
							.get(docID) / avaerageLengthOfDoc))) * tfIdf;
				}
				docVector.put(termId, vector);
			}
		}
		return docVector;
	}

	/*---------------------------------------------------------------------------------*/

	public void tfIdf() {
		try {
			okapiTF();
		} catch (Exception e) {
		}
	}

	/*---------------------------------------------------------------------------------*/
	// to do
	public void okapiBM25() throws Exception {
		double k1 = 1.2;
		int k2 = 100;
		double b = 0.75;
		
		int D = docIds.size();
		double averageLengthOfDocs = totalDocumentsLength / docIds.size();
		//System.out.println(stemmedQuery);
		for (Map.Entry<Integer, ArrayList<String>> entry : stemmedQuery
				.entrySet()) {
			LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
			ArrayList<String> q = entry.getValue();
			//System.out.println(q);
			HashSet<Integer> docs = getAllDocToProcess(q);
			int queryId = entry.getKey();
			int count = 0;
			ArrayList<String> terms = entry.getValue();
			for (Integer docId : docs) {
				double finalScoreOfDoc = 0.0;
				// int docid = doc.getKey();
				HashMap<Integer, Integer> fMap = docTermFreq.get(docId);
				Integer freq = 0;
				if (recordOfDoc.get(docId) != null) {
					freq = recordOfDoc.get(docId);
				}
				double K = k1 * ((1 - b) + b * (freq / averageLengthOfDocs));
				ArrayList<Double> scoreOfEachTerm = new ArrayList<>();
				
				for (int i = 0; i < terms.size(); i++) {
					
					String singleTerm = terms.get(i);
					Double score = 0.0;
					int termId = getTermId(singleTerm);
					int dfi = termInDoc.get(termId);
					//System.out.println(docOccurence.size());

					double tfdi = 0.0;
					if (fMap.containsKey(termId)) {
						tfdi = fMap.get(termId);
					}
					int tfqi = Collections.frequency(terms, singleTerm);
					score = Math.log((D + 0.5) / (dfi + 0.5))
							* (((1 + k1) * tfdi) / (K + tfdi))
							* (((1 + k2) * tfqi) / (k2 + tfqi));
					scoreOfEachTerm.add(score);
				}
				for (int j = 0; j < scoreOfEachTerm.size(); j++) {
					finalScoreOfDoc = finalScoreOfDoc + scoreOfEachTerm.get(j);
				}
				scores.put(docId, finalScoreOfDoc);

				// System.out.println(queryId + " " + 0 + " " +
				// docIds.get(docId)
				// + " " + count + " " + finalScoreOfDoc + " " + "run1");
			}
			scores= (LinkedHashMap<Integer, Double>) sortByComparator(scores);
			for(Entry<Integer, Double> e: scores.entrySet()){
				System.out.println(queryId + " " + 0 + " " +
				docIds.get(e.getKey())
				 + " " + (++count) + " " + e.getValue() + " " + "run1");
			}
		}

	}

	// to do
	public void languageModelWithLaplaceSmoothing() throws Exception {
		getVocabSize();
		for (Map.Entry<Integer, ArrayList<String>> entry : stemmedQuery
				.entrySet()) {
			LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
			int count = 0;
			int queryId = entry.getKey();
			ArrayList<String> terms = entry.getValue();
			HashSet<Integer> docs=getAllDocToProcess(terms);
			for (Integer docid : docs) {
				double finalScoreOfDoc = 0.0;
				Integer freq = 0;
				HashMap<Integer, Integer> fMap = docTermFreq.get(docid);
				if (recordOfDoc.get(docid) != null) {
					freq = recordOfDoc.get(docid);
				}
				ArrayList<Double> scoreOfEachTerm = new ArrayList<>();
				
				for (int i = 0; i < terms.size(); i++) {
					
					String singleTerm = terms.get(i);
					Double score = 0.0;
					int termId = getTermId(singleTerm);
					double tfdi = 0.0;
					if (fMap.containsKey(termId)) {
						tfdi = fMap.get(termId);
					}
					int deno = (freq + V);
					double nemo = (tfdi + 1);
					score = Math.log((nemo * 1.0) / deno);
					scoreOfEachTerm.add(score);
				}
				for (int j = 0; j < scoreOfEachTerm.size(); j++) {
					finalScoreOfDoc = finalScoreOfDoc + scoreOfEachTerm.get(j);
				}
				scores.put(docid, finalScoreOfDoc);
				
			}
			scores= (LinkedHashMap<Integer, Double>) sortByComparator(scores);
			ArrayList<String> printing =new ArrayList<String>();
			int c=0;
			for(Entry<Integer, Double> e: scores.entrySet()){
				String s=queryId + " " + 0 + " " +
 						docIds.get(e.getKey())
 						 + " " + (++count) + " " + e.getValue() + " " + "run1";
 				printing.add(c,s);
 				c=c+1;
				System.out.println(queryId + " " + 0 + " " +
				docIds.get(e.getKey())
				 + " " + (++count) + " " + e.getValue() + " " + "run1");
			}
			createDocid(printing);
		}

	}

	public static void getVocabSize() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
			String s;
			int size = 0;
			while ((s = br.readLine()) != null) {
				size = size + 1;
			}
			V = size;
			br.close();
		} catch (Exception e) {
			//System.out.println(e);
		}
	}

	public void createDocid(ArrayList<String> printing) {
		try {
			int i = 1;
			File file = new File(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\run.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (String s:printing) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public void languageModelWithJelinekMercerSmoothing() throws Exception {
		// freqInCor
		double lembda = 0.2;
		for (Map.Entry<Integer, ArrayList<String>> entry : stemmedQuery
				.entrySet()) {
			int count=0;
			LinkedHashMap<Integer, Double> scores = new LinkedHashMap<Integer, Double>();
			int queryId = entry.getKey();
			ArrayList<String> terms = entry.getValue();
			HashMap<Integer, Double> smoothing=new HashMap<Integer, Double>();
			for(String term:terms){
				double docSum=0.0;
				double termSum=0.0;
				
				int termId=getTermId(term);
				double smooth=0.0;
				for(Entry<Integer, String> e: docIds.entrySet()){
					int docId=e.getKey();
					if(docTermFreq.containsKey(docId)){
					HashMap<Integer, Integer> freqMap=docTermFreq.get(docId);
					
					int docC=0;
					
					if(recordOfDoc.containsKey(docId)){
						docC=recordOfDoc.get(docId);
						}
					
					
					double freq=0.0;
					if(freqMap.containsKey(termId)){
						//System.out.println(1);
						freq=freqMap.get(termId);
						}
					docSum+=docC;
					termSum+=freq;
				}}
				smooth=(termSum/docSum)*(1-lembda);
				//System.out.println(smooth);
				
				smoothing.put(termId, smooth);
				
			}//System.out.println(smoothing);
			HashSet<Integer> docs=getAllDocToProcess(terms);
			for (Integer docId : docs) {
				HashMap<Integer, Integer> fMap=docTermFreq.get(docId);
				double finalScoreOfDoc = 0.0;
				Integer freq = 0;
				if (recordOfDoc.get(docId) != null) {
					freq = recordOfDoc.get(docId);
					if(freq==0){
						continue;
					}
				}
				for (int i = 0; i < terms.size(); i++) {
					
					String singleTerm = terms.get(i);
					
					int termId =getTermId(singleTerm);
					double tfdi = 0.0;
					if (fMap.containsKey(termId)) {
						tfdi = fMap.get(termId);
					}
					
					double smoothF=smoothing.get(termId);
					double smoothed=smoothF+0.2*(tfdi/freq);
					double smoothedLogged=Math.log(smoothed)/Math.log(2);
					finalScoreOfDoc=finalScoreOfDoc+smoothedLogged;
					}
				scores.put(docId, finalScoreOfDoc);
			}
			scores= (LinkedHashMap<Integer, Double>) sortByComparator(scores);
			ArrayList<String> printing =new ArrayList<String>();
			int c=0;
 			for(Entry<Integer, Double> e: scores.entrySet()){
 				String s=queryId + " " + 0 + " " +
 						docIds.get(e.getKey())
 						 + " " + (++count) + " " + e.getValue() + " " + "run1";
 				printing.add(c,s);
 				c=c+1;
				System.out.println(queryId + " " + 0 + " " +
				docIds.get(e.getKey())
				 + " " + (++count) + " " + e.getValue() + " " + "run1");
			}
 			createDocid(printing);
		}
	}
}
				
