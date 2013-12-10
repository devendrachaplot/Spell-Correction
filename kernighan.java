import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.HashMap;
 
public class kernighan {

	public static int insertion[][];
	public static int deletion[][];
	public static int substitution[][];
	public static int transposition[][];
	public static int occur[];
	public static int occurPair[][];
		
	public static void main(String[] args) {
 		// System.out.println(findEditDistance("abonadon","abandon"));
		BufferedReader br = null;
		insertion = new int[27][27];
		deletion = new int[27][27];
		substitution = new int[27][27];
		transposition = new int[27][27];
		occur = new int[27];
		occurPair = new int[27][27];
		
 		Map<String,Integer> correctWordsList = new HashMap<String,Integer>();
		try {
			String wordList;
 
			br = new BufferedReader(new FileReader("./wikiSpellList"));
			while ((wordList = br.readLine()) != null) {
				String incorrect = wordList.split("-")[0];
				//System.out.println(incorrect);
				String correct = wordList.split(">")[1];
				while(correct.contains(","))
				{
					String correct1 = correct.split(", ")[0];
					learncorrect(correct1);
					learnmatrix(incorrect, correct1);
					if(correctWordsList.get(correct1)!=null)
						correctWordsList.put(correct1, correctWordsList.get(correct1)+1);
					else
						correctWordsList.put(correct1,1);
					correct = correct.split(", ")[1];
				}
				learnmatrix(incorrect, correct);
				learncorrect(correct);
				if(correctWordsList.get(correct)!=null)
					correctWordsList.put(correct, correctWordsList.get(correct)+1);
				else
					correctWordsList.put(correct,1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//fold-specific training 
		//System.out.println("HashMap created");
		//printMatrices();
		ArrayList<String> inputString = new ArrayList<String>();
		try {
			String wordList;
 
			br = new BufferedReader(new FileReader("./testcases"));
			while ((wordList = br.readLine()) != null) {
				inputString.add(wordList.split("-")[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		for(String i: inputString){
			double best_score = 0.0;
			String best_match = "";
			for(Map.Entry<String, Integer> entry : correctWordsList.entrySet()) {
				if(findEditDistance(i, entry.getKey())<=2) {
					//System.out.println(entry.getKey());
					//System.out.print(findScore(i, entry.getKey())+" ");
					//System.out.println(entry.getValue());
					double cur_score = entry.getValue()*findScore(i, entry.getKey());
					//System.out.println(cur_score);
					if(cur_score>best_score)
					{
						best_score = cur_score;
						best_match = entry.getKey();
					}
				}
			}
			System.out.println(best_match);
		}
	}
	
	public static double findScore(String incorrect, String guess)
	{
		errorType et = findMismatch(incorrect, guess);
		int x = ((int)et.x)-97;
		int y = ((int)et.y)-97;
		int type = et.error_type;
		if(x<0||x>26)
			x=26;
		if(y<0||y>26)
			y=26;
		if(type==0)
			return (double)insertion[x][y]/occur[x];
		else if(type==1)
			return (double)deletion[x][y]/occurPair[x][y];
		else if(type==2)
			return (double)substitution[x][y]/occur[y];
		else
			return (double)transposition[x][y]/occurPair[x][y];
	}

	public static void learncorrect(String correct) {
		int len = correct.length();
		for(int j=0;j<len;j++)
		{
			int x = (int) correct.charAt(j) - 97;
			if(x<0||x>26)
				x=26;
			occur[x]++;
		}
		for(int j=0;j<len-1;j++)
		{
			int x = (int) correct.charAt(j) - 97;
			if(x<0||x>26)
				x=26;
			int y = (int) correct.charAt(j+1) - 97;
			if(y<0||y>26)
				y=26;
			occurPair[x][y]++;	
		}
	}
	
	public static void printMatrices() {
		System.out.println("Insertion Matrix");
		for(int k=0;k<27;k++) {
			for(int l=0;l<27;l++) {
				System.out.print(insertion[k][l]+"\t");
			}
			System.out.println();
		}
		System.out.println("Deletion Matrix");
		for(int k=0;k<27;k++) {
			for(int l=0;l<27;l++) {
				System.out.print(deletion[k][l]+"\t");
			}
			System.out.println();
		}
		System.out.println("Substitution Matrix");
		for(int k=0;k<27;k++) {
			for(int l=0;l<27;l++) {
				System.out.print(substitution[k][l]+"\t");
			}
			System.out.println();
		}
		System.out.println("Transposition Matrix");
		for(int k=0;k<27;k++) {
			for(int l=0;l<27;l++) {
				System.out.print(transposition[k][l]+"\t");
			}
			System.out.println();
		}
		System.out.println("Pair Matrix");
		for(int k=0;k<27;k++) {
			for(int l=0;l<27;l++) {
				System.out.print(occurPair[k][l]+"\t");
			}
			System.out.println();
		}
		System.out.println("Occurence vector");
		for(int k=0;k<27;k++) {
			System.out.print(occur[k]+"\t");
		}
		System.out.println();
	}
	
	public static void learnmatrix(String incorrect, String correct) {
		errorType et = findMismatch(incorrect, correct);
		int x = ((int)et.x)-97;
		int y = ((int)et.y)-97;
		int type = et.error_type;
		if(x<0||x>26)
			x=26;
		if(y<0||y>26)
			y=26;
		if(type==0)
			insertion[x][y]++;
		else if(type==1)
			deletion[x][y]++;
		else if(type==2)
			substitution[x][y]++;
		else
			transposition[x][y]++;
	}
	
	public static errorType findMismatch(String incorrect, String correct) {
		int inc = incorrect.length();
		int cor = correct.length();
		incorrect = incorrect.toLowerCase();
		correct = correct.toLowerCase();
		
		errorType et = new errorType();
		//insertion
		if(inc==cor+1)
		{
			int i = 0;
			while((i<cor-1)&&(incorrect.charAt(i)==correct.charAt(i)))
				i++;
			if(i==cor-1)
			{
				et.x = correct.charAt(cor-1);
				et.y = incorrect.charAt(inc-1);
			}
			else
			{
				if(i>0)
				{
					et.x = correct.charAt(i-1);
					et.y = incorrect.charAt(i);
				}
			}
			et.error_type = 0;
		}
		
		//deletion		
		if(inc==cor-1)
		{
			int i = 0;
			while((i<inc)&&(incorrect.charAt(i)==correct.charAt(i)))
				i++;
			if(i==inc)
			{
				et.x = incorrect.charAt(inc-1);
				et.y = correct.charAt(cor-1);
			}
			else
			{
				if(i>0)
				{
					et.x = correct.charAt(i-1);
					et.y = correct.charAt(i);
				}
			}
			et.error_type = 1;
		}
		
		//substitution and transposition
		if(inc==cor)
		{
			int i=0;
			while((i<inc)&&(incorrect.charAt(i)==correct.charAt(i)))
				i++;
			et.x = incorrect.charAt(i);
			et.y = correct.charAt(i);
			if(((i+1<inc)&&(incorrect.charAt(i+1)==correct.charAt(i)))||(i+1==inc))
				et.error_type = 3;
			else
				et.error_type = 2;
		}
		return et;
	}
	
	public static int findEditDistance(String s, String t){
		int m=s.length();
		int n=t.length();
		int[][]d=new int[m+1][n+1];
		for(int i=0;i<=m;i++){
		  d[i][0]=i;
		}
		for(int j=0;j<=n;j++){
		  d[0][j]=j;
		}
		for(int j=1;j<=n;j++){
		  for(int i=1;i<=m;i++){
		    if(s.charAt(i-1)==t.charAt(j-1)){
		      d[i][j]=d[i-1][j-1];
		    }
		    else{
		      d[i][j]=min((d[i-1][j]+1),(d[i][j-1]+1),(d[i-1][j-1]+2));
		    }
		  }
		}
		return(d[m][n]);
	}	

	public static int min(int a,int b,int c){
		return(Math.min(Math.min(a,b),c));
	}
}

class errorType
{
	// 0 for insertion, 1 for deletion, 2 for substitution, 3 for transposition
	public int error_type;
	public char x;
	public char y;
}
