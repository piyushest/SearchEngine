import java.util.Map;


public class Indexer {
    public static void main(String []args)
    {
    	
    	Tokenizer tokens=new Tokenizer();
    	tokens.createTokens(args[0]);
    	InvertingIndex i = new InvertingIndex();
    	i.createTermIndex(Tokenizer.stemmedWords.size());
    	
    }
}
