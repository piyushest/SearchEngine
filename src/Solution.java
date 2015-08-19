import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Solution {
	public static Map<String,String> cellIdentificationWithSink=new HashMap<String,String>();
	public static Map<String,Integer> basin=new HashMap<String,Integer>();
	static int [][]landAltitudes;
	public static List<String> uniqueSinks=new ArrayList<String>(); 
    public static void main(String args[] ) throws Exception {
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        int row=0;
        int rowsColumn=Integer.parseInt(br.readLine());
        int [][]landAltitude=new int[rowsColumn][rowsColumn];
        landAltitudes=landAltitude;
        //setting up the cells value in the landAltitude
        while((line=br.readLine())!=null){
          String[] value=line.split(" ");
          for(String s:value){
        	  for(int i=0;i<rowsColumn;i++){
        		  landAltitude[row][i]=Integer.parseInt(s); 
        	  }
          }
          row=row+1;
        }
        setLevels(rowsColumn);
        arrangeAllSinks();
        getUniqueSinks();
        findTotalCount();
        finalResult();
        
    }
    
    public static void setLevels(int row){
    	String name=new String();
    	for(int i=0;i<row;i++){
    		for(int j=0;j<row;j++){
    			name= Integer.toString(i)+Integer.toString(j);
    			String sink=findSink(i,j,row);
    			cellIdentificationWithSink.put(name, sink);
    		}
    		
    	}
    	
    }
    public static void getUniqueSinks(){
    	for (Map.Entry<String,String> term : cellIdentificationWithSink.entrySet()){
    		String cell=term.getValue();
    		if(basin.containsKey(cell)){
    			continue;
    		}
    		else{
    			basin.put(cell, 0);
    		}
    	}
    }
    //puts all the values of basins
    public static void findTotalCount(){
    	for (Map.Entry<String,Integer> term : basin.entrySet()){
        	String key=term.getKey();
        	int value=term.getValue();
        	for (Map.Entry<String,String> term1 : cellIdentificationWithSink.entrySet()){
        		String a=term1.getValue();
        		if(a.equals(key)){
        			value=value+1;
        		}
        	}
        	basin.put(key, value);
     }
    }
    
    //iterate over the map to check the iterative sinks
    //ex: 3->1 and 5->3 => 5->1
    public static void arrangeAllSinks(){
    	int length=cellIdentificationWithSink.size();
    	for (Map.Entry<String,String> term : cellIdentificationWithSink.entrySet()){
    		String key=term.getKey();
    		String value=term.getValue();
    		if(cellIdentificationWithSink.containsKey(value)){
    			String newSink=cellIdentificationWithSink.get(value);
    			cellIdentificationWithSink.put(key, newSink);
    		}
    	}
    }
    
    //finds the sink in the neighbour
    public static String findSink(int i,int j,int size){
    	int startRow;
    	int endRow;
    	
    	int actualSize=size-1;
    	
    	if(i+1>actualSize){
    		endRow=i;
    	}
    	else{
    		endRow=i+1;
    	}
    	
    	if(!(i-1<0)){
    	    startRow=i;
    	}
    	else{
    		startRow=i-1;
    	}
    	
    	int startColumn;
    	int endColumn;
    	if(j+1>actualSize){
    		endColumn=j;
    	}
    	else{
    		endColumn=j+1;
    	}
    	if(!(j-1<0)){
    		startColumn=j-1;
    	}
    	else{
    		startColumn=j;
    	}
    	int min=0;
    	String column=new String();
    	
    	for(int counter=startRow;counter<endRow;counter++){
    		for(int innerCounter=startColumn;innerCounter<endColumn;innerCounter++){
    			int temp= landAltitudes[counter][innerCounter];
    			if(min>=temp){
    				min=temp;
    				column=Integer.toString(counter)+Integer.toString(innerCounter);
    			}
    		}
    		
    	}
    	
    	return column;
    }
    
    public static void finalResult(){
    	List<Integer> a=new ArrayList<Integer>(basin.values());
    	Collections.sort(a);
    	Collections.reverse(a);
    	for(int i=0;i<a.size();i++){
    		System.out.print(a.get(i));
    	    if(!(i==a.size()-1)){
    	    	System.out.print(" ");
    	    }	
    	}
    	
    }
     
        	
     

}