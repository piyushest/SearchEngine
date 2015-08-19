import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.tartarus.snowball.EnglishSnowballStemmerFactory;

public class ReadIndex {
	static int totalTerms = 0;

	public static void main(String[] args) {
		int length = args.length;
		System.out.println(length);

		if (length == 2) {
			if (args[0] == "--doc") {
				getListingForDocuments(args[1]);

			} else if (args[0] == "--term") {
				getListingForTerm(args[1]);
			} else {
				System.out.println("Wrong set of command");
			}
		}
		if (length == 4) {
			if (args[0] == "--term" && args[2] == "--doc") {
				getInvertedListForTheTerm(args[3], args[1]);
			} else {
				System.out.println("Wrong set of command");
			}
		}
	}

	private static void getInvertedListForTheTerm(String docName,
			String termName) {
		try {
			String term = EnglishSnowballStemmerFactory.getInstance().process(
					termName);
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
			BufferedReader br1 = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\docids.txt"));
			String read;
			int offSet = 0;
			String nameOfDoc = docName;
			int termId = 0;
			boolean isDocAvailable = false;
			int docId = 0;
			boolean isTermAvailable = false;
			int termFreqInDoc = 0;
			ArrayList<Integer> positions = new ArrayList<Integer>();

			while ((read = br1.readLine()) != null) {
				String split[] = read.split("\\t");
				if (split[1].equals(docName)) {
					docId = Integer.parseInt(split[0]);
					isDocAvailable = true;
					break;
				}
			}
			br1.close();
			while ((read = br.readLine()) != null) {
				String split[] = read.split("\\t");
				if (split[1].equals(term)) {
					termId = Integer.parseInt(split[0]);
					isTermAvailable = true;
					break;
				}
			}
			br.close();

			if (!(isDocAvailable && isTermAvailable)) {
				System.err.println("Unable to find Doc or Term");
				return;
			}
			BufferedReader br2 = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\term_info.txt"));

			while ((read = br2.readLine()) != null) {
				String split[] = read.split("\\t");
				int termIndex = Integer.parseInt(split[0]);
				if (termIndex == termId) {
					offSet = Integer.parseInt(split[1]);
					break;
				}
			}
			br2.close();

			RandomAccessFile ra = new RandomAccessFile(new File(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\term_index.txt"),
					"r");
			ra.seek(offSet);
			String termInfo = ra.readLine();
			String splitTerms[] = termInfo.split("\\t");
			int sum = 0;

			for (int i = 1; i < splitTerms.length; i++) {
				String split[] = splitTerms[i].split(":");
				sum += Integer.parseInt(split[0]);
				if (sum >= docId) {
					if (!(sum == docId)) {
						System.err
								.println("term does not exists in the given document");
						return;
					} else {
						int posSum = 0;
						for (int j = i; j < splitTerms.length; j++) {
							String split1[] = splitTerms[j].split(":");
							int doc = Integer.parseInt(split1[0]);

							if (doc == 0 || sum == docId) {
								posSum = posSum + Integer.parseInt(split1[1]);
								++termFreqInDoc;
								positions.add(posSum);
								sum += Integer.parseInt(split1[0]);
							} else {
								break;
							}

						}
						break;
					}
				}
			}
			System.out.println("Inverted list for term: " + termName);
			System.out.println("In document: " + nameOfDoc);
			System.out.println("TERMID: " + termId);
			System.out.println("DOCID: " + docId);
			System.out.println("Term frequency in document: " + termFreqInDoc);
			System.out.print("Positions: ");
			int i = 1;
			for (int num : positions) {
				System.out.print(num);
				if (i != positions.size()) {
					System.out.print(",");
				}
				i = i + 1;
			}

			// System.out.println("\b\b");
			System.out.println();
			ra.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}
    
	public static void getListingForTerm(String string) {
		int termId = 0;
		boolean found = false;
		int documentsContainingTerm = 0;
		int frequencyIncorpus = 0;
		int offSet = 0;
		try {
			String s = EnglishSnowballStemmerFactory.getInstance().process(
					string);
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\termids.txt"));
			String read;
			while ((read = br.readLine()) != null) {
				String split[] = read.split("\\t");
				if (split[1].equals(s)) {
					termId = Integer.parseInt(split[0]);
					found = true;
					break;
				}
			}
			br.close();
			if (found) {
				BufferedReader br1 = new BufferedReader(new FileReader(
						"C:\\Users\\Piyush\\Desktop\\Project 2\\term_info.txt"));
				while ((read = br1.readLine()) != null) {
					String split[] = read.split("\\t");
					int termIndex = Integer.parseInt(split[0]);
					if (termIndex == termId) {
						offSet = Integer.parseInt(split[1]);
						frequencyIncorpus = Integer.parseInt(split[2]);
						documentsContainingTerm = Integer.parseInt(split[3]);
					}
				}
				br1.close();
				System.out.println("Listing for term: " + s);
				System.out.println("TERMID: " + termId);
				System.out.println("Number of documents containing term: "
						+ documentsContainingTerm);
				System.out.println("Term frequency in corpus: "
						+ frequencyIncorpus);
				System.out.println("Inverted list offset: " + offSet);
			} else {
				System.err.println("Unable to find the term");

			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void getListingForDocuments(String docName) {
		int docId = 0;
		int distinctTerms = 0;// Distinct terms: 25
		int totalTerms = 0;// Total terms: 501
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\docids.txt"));
			BufferedReader br1 = new BufferedReader(new FileReader(
					"C:\\Users\\Piyush\\Desktop\\Project 2\\doc_index.txt"));
			String str;
			boolean docFound = false;
			while ((str = br.readLine()) != null) {
				String s[] = str.split("\\t");
				if (s[1].equals(docName)) {
					docId = Integer.parseInt(s[0]);
					docFound = true;
					break;
				}
			}
			if (docFound) {
				while ((str = br1.readLine()) != null) {
					String afterSplit[] = str.split("\\t");
					if (Integer.parseInt(afterSplit[1]) == docId) {
						int perTermId = afterSplit.length - 2;
						totalTerms = totalTerms + perTermId;
						distinctTerms = distinctTerms + 1;
					}
				}

				System.out.println("Listing for document:  " + docName);
				System.out.println("DOCID: " + docId);
				System.out.println("Distinct terms: " + distinctTerms);
				System.out.println("Total terms: " + totalTerms);
				br.close();
				br1.close();
			} else {
				System.err.println("Document not Found");
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
