import java.util.ArrayList;
import java.util.Map;

public class ScoringFunctionsMain {

	public static void main(String[] args)throws Exception {

		ScoringFunctions s = new ScoringFunctions();
		s.getAllDocName();
		s.getListingForDocuments();
		s.calculateTheOccurenceOfTerms();
		s.setTheOccurence();
		s.setDocTermFreq();
		s.setTermInDocMap();
		//s.allstemmed();
		
		
		
		if (args[1] == "TF" && args[0] == "--score") {
		s.okapiTF();
		} else if (args[1] == "TF-IDF" && args[0] == "--score") {
			s.TFIDF();
		} else if (args[1] == "BM25" && args[0] == "--score") {
			s.okapiBM25();
		} else if (args[1] == "Laplace" && args[0] == "--score") {
			s.languageModelWithLaplaceSmoothing();
		} else if (args[1] == "JM" && args[0] == "--score") {
		s.languageModelWithJelinekMercerSmoothing();
		} else {
			System.out.println("Wrong Input " + args[0] + " " + args[1]);
		}

	}

}
