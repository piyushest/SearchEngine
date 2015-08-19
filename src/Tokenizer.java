import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.EnglishSnowballStemmerFactory;
import org.tartarus.snowball.util.StemmerException;
//termid starts from 1
//filenumber starts from 0

public class Tokenizer {
	static Map<String, Integer> stopWords = new HashMap<String, Integer>();
	public static Pattern p = Pattern.compile("\\w+(\\.?\\w+)*");
	public static int termid = 1;
	public static Map<String, Integer> stemmedWords = new HashMap<String, Integer>();
	// File Number,Term Positions
	public static Map<Integer, Map<String, List<Integer>>> docIndex = new HashMap<Integer, Map<String, List<Integer>>>();
   HashMap<Character,Integer> a =new HashMap<>();
   StringBuilder s =new StringBuilder("hello");
   
	public void createTokens(String file) {
		int i = 0;
		// long startTime =System.nanoTime();
		File directory = new File(
				file);
		Map<String, Integer> fileNames = new HashMap<String, Integer>();

		File filenames[] = directory.listFiles();

		Map<String, Integer> stopWords = getStopWords("stop_list");
        //System.out.println(filenames.length);
		for (int j = 0; j < filenames.length; j++) {
			fileNames.put(filenames[j].getName(), j);
			// System.out.println(filenames[j]);
			readEachLine(filenames[j], stopWords, j);
			i = i + 1;
			System.out.println(i);
		}
		// long stopTime = System.nanoTime();
		// System.out.println(stopTime-startTime);
		createDocid(fileNames);
		createTermId();
		createDocIndex();
		System.out.println("done");

	}

	public void readEachLine(File file, Map<String, Integer> stopWords,
			int fileNumber) {
		try {
			FileInputStream stream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(stream);
			boolean startAdding = false;
			String fullFile = new String();

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int count=0;
			while ((strLine = br.readLine()) != null) {
				if (!startAdding) {
					if (strLine.equals("")) {
						count=count+1;
					}
					if(count==2){
						startAdding=true;
					}
				}
				if (startAdding) {
					fullFile += strLine;
				}
			}
			in.close();
			extractText(fullFile, stopWords, fileNumber);
		} catch (Exception e) {

		}
	}

	public void extractText(String file, Map<String, Integer> stopWords,
			int fileNumber) {
		Map<String, List<Integer>> termIdPosition = new HashMap<String, List<Integer>>();
		int position = 0;
		

		Document doc = Jsoup.parse(file);
		String text = doc.body().text();
		Matcher match = p.matcher(text);
		// termId starts from 1
		while (match.find()) {
			position = position + 1;
			String word = match.group().toLowerCase();
			if (!stopWords.containsKey(word)) {
				try {
					String s = EnglishSnowballStemmerFactory.getInstance()
							.process(word);
					if (termIdPosition.containsKey(s)) {
						List<Integer> term = termIdPosition.get(s);
						term.add(position);
						termIdPosition.put(s, term);
					} else {
						List<Integer> positionHandler = new ArrayList<Integer>();
						positionHandler.add(position);
						termIdPosition.put(s, positionHandler);
					}
					if (!stemmedWords.containsKey(s)) {
						stemmedWords.put(s, termid);
						termid = termid + 1;
					}
				} catch (StemmerException e) {
					e.printStackTrace();
				}
			}
		}
		docIndex.put(fileNumber, termIdPosition);
		// System.out.println("size of stop words"+stopWords.size());
	}

	public Map<String, Integer> getStopWords(String file) {
		
		try {
			FileInputStream stream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String word;
			while ((word = br.readLine()) != null) {
				stopWords.put(word, 1);
			}
			in.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return stopWords;
	}

	public void createDocid(Map<String, Integer> names) {
		try {
			int i = 1;
			File file = new File(
					"docids.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Map.Entry<String, Integer> entry : names.entrySet()) {
				bw.write(Integer.toString(entry.getValue()) + "\t"
						+ entry.getKey());
				i = i + 1;
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void createTermId() {
		try {
			File file = new File(
					"termids.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Map.Entry<String, Integer> entry : stemmedWords.entrySet()) {
				bw.write(Integer.toString(entry.getValue()) + "\t"
						+ entry.getKey());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void createDocIndex() {
		try {
			File file = new File(
					"doc_index.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Map.Entry<Integer, Map<String, List<Integer>>> entry : docIndex
					.entrySet()) {
				int size = 0;
				int filenumber = entry.getKey();
				bw.write(Integer.toString(entry.getKey()));
				for (Map.Entry<String, List<Integer>> entryForWord : entry
						.getValue().entrySet()) {
					size = size + 1;
					int sizeOfMap = entry.getValue().size();
					bw.write("\t");
					System.out.println(stemmedWords.get(entryForWord.getKey()));
					bw.write(Integer.toString(stemmedWords.get(entryForWord
							.getKey())));
					for (Integer e : entryForWord.getValue()) {
						bw.write("\t");
						bw.write(Integer.toString(e));
					}
					bw.newLine();
					if (size != sizeOfMap) {
						bw.write(Integer.toString(filenumber));
					}
				}
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}