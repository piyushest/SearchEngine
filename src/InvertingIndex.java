import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.*;

public class InvertingIndex {
	// TERMID,DOCID,POSITION
	static Map<Integer, TreeMap<Integer, List<Integer>>> invertedIndex = new TreeMap<Integer, TreeMap<Integer, List<Integer>>>();
	// List will contain two element first is no of docs and next is total
	// number of occurence in corpus
	static Map<Integer, List<Integer>> totalOccurence = new HashMap<Integer, List<Integer>>();
    public void createTermIndex(int termIds) {
		readData(new File(
				"doc_index.txt"));
		
		try {
			File file = new File(
					"term_index.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			// iterating through the map for termid
			for (Map.Entry<Integer, TreeMap<Integer, List<Integer>>> entry : invertedIndex
					.entrySet()) {
				
				int termid=entry.getKey();
				bw.write(Integer.toString(termid));
				bw.write("\t");
				int totalDocs = 0;
				// docid List to check the last docid
				List<Integer> docids = new ArrayList<Integer>();
				// termids with positions in a doc
				int iteration=0;
				boolean isFirst =true;
				Map<Integer, List<Integer>> termIDPositionInDoc = new TreeMap<Integer, List<Integer>>();
				termIDPositionInDoc = invertedIndex.get(termid);
				int totalSizeInCorpus = 0;
				//got the doc and its positions
				if (termIDPositionInDoc != null) {
					totalDocs = termIDPositionInDoc.size();
				}
				
				for(Map.Entry<Integer, List<Integer>> doc : termIDPositionInDoc
						.entrySet())
				{
					
					totalSizeInCorpus = totalSizeInCorpus
							+ doc.getValue().size();
				   int docID = doc.getKey();
				   List<Integer> positions = doc.getValue();
				   docids.add(iteration, docID);
				   
				   if(isFirst){
					   bw.write(Integer.toString(docID));
					   bw.write(":");
					   bw.write(Integer.toString(positions.get(0)));
					   bw.write("\t");
					   iteration=iteration+1;
					   isFirst=false;
					}
				   else{
					   if(iteration>0){
					   int inDocID = docids.get(iteration)-docids.get(iteration-1);
					   bw.write(Integer.toString(inDocID));
					   bw.write(":");
					   bw.write(Integer.toString(positions.get(0)));
					   bw.write("\t");
					   iteration=iteration+1;
					   }
				   }
				    
				    for(int i=1;i<positions.size();i++){
				    	int diff = positions.get(i)-positions.get(i-1);
				    	bw.write("0:"+Integer.toString(diff));
				    	bw.write("\t");
				    	
				    }
				   
				}
				bw.newLine();
			    List<Integer> a = new ArrayList<Integer>();
                a.add(0, totalDocs);
				a.add(1, totalSizeInCorpus);
				totalOccurence.put(termid, a);
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		createTermInfo(new File(
				"term_index.txt"),
				termIds);
	}

	public void readData(File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				List<Integer> positions = new ArrayList<Integer>();
				String s[] = strLine.split("\\t");
				int docid = Integer.parseInt(s[0]);
				int termid = Integer.parseInt(s[1]);
				int length = s.length;
				for (int j = 2; j < length; j++) {
                    positions.add(Integer.parseInt(s[j]));
				}
				Collections.sort(positions);
				if (invertedIndex.containsKey(termid)) {
					//docid and positions
					TreeMap<Integer, List<Integer>> termIDPosition = invertedIndex
							.get(termid);
					termIDPosition.put(docid, positions);
					invertedIndex.put(termid, termIDPosition);
				} else {
					TreeMap<Integer, List<Integer>> termIDPosition = new TreeMap<Integer, List<Integer>>();
					termIDPosition.put(docid, positions);
					invertedIndex.put(termid, termIDPosition);
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println(e);

		}
	}

	public void createTermInfo(File file, int termIds) {
		try {
			File newFile = new File(
					"term_info.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (Map.Entry<Integer, List<Integer>> term : totalOccurence
					.entrySet()) {
				int termId=term.getKey();
				bw.write(Integer.toString(termId));
				bw.write("\t");
				RandomAccessFile fileChecker = new RandomAccessFile(file, "rw");
				fileChecker.seek(termId);
				long pointer = fileChecker.getFilePointer();
				bw.write(Long.toString(pointer));
				bw.write("\t");
				List<Integer> record = totalOccurence.get(termId);
				//total occurence in corpus
				bw.write(Integer.toString(record.get(1)));
				bw.write("\t");
				//total docs
				bw.write(Integer.toString(record.get(0)));
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
