package NoiseCharFilter;
import java.util.HashMap;
import java.util.HashSet;

public class EngCharTool{
	/**
	 * 英语句终标点
	 */
	private static final char[] sentencePunctuations={'!','?',';'};
	private static HashSet<Character> sentencePunctuation=new HashSet<Character>();
	static{
		for(char cha:sentencePunctuations) sentencePunctuation.add(cha);
	}

	public static boolean isSentencePunctuation(char cha){
		boolean isSentencePunctuation=false;
		if(sentencePunctuation.contains(cha)) isSentencePunctuation=true;
		return isSentencePunctuation;
	}
	
	/**
	 * 处理'.'
	 */
	private static String[] abbreviations={"Dr", "Mr", "Ms", "Mt", " Dr", " Mr", " Ms", " Mt", "Mrs", "e.g", "a.m", "p.m", "A.M", "P.M", "A.D", "B.C", "U.S", "U.N"};
	private static HashSet<String> abbreviation=new HashSet<String>();
	static{
		for(String str:abbreviations) abbreviation.add(str);
	}
	
	private static boolean isSentencePunctuation(char pre3, char pre2, char pre1, char post1){
		boolean isSentencePunctuation=false;
		if(post1==Character.UNASSIGNED){
			isSentencePunctuation=true;
		}else if(post1==' '){
			String pre="";
			if((pre3!=Character.UNASSIGNED)&&(pre2!=Character.UNASSIGNED)&&(pre1!=Character.UNASSIGNED)){
				pre=pre+pre3+pre2+pre1;
				if(!abbreviation.contains(pre)) isSentencePunctuation=true;
			}else if((pre3==Character.UNASSIGNED)&&(pre2!=Character.UNASSIGNED)&&(pre1!=Character.UNASSIGNED)){
				pre=pre+pre2+pre1;
				if(!abbreviation.contains(pre)) isSentencePunctuation=true;
			}
		}
		return isSentencePunctuation;
	}
	
	public static boolean isSentencePunctuation(char pre3, char pre2, char pre1, char current, char post1){
		boolean isSentencePunctuation=false;
		if(isSentencePunctuation(current)) isSentencePunctuation=true;
		else if(current=='.') isSentencePunctuation=isSentencePunctuation(pre3, pre2, pre1, post1);
		return isSentencePunctuation;
	}

	/**
	 * 英汉标点转换
	 */
	private static final char[] engPunctuations={'.','!','?',';',',','(',')'};
	private static final char[] chiPunctuations={'。','！','？','；','，','（','）'};
	private static HashMap<Character, Character> toChiPunctuation=new HashMap<Character, Character>();
	static{
		for(int i=0; i<engPunctuations.length; i++) toChiPunctuation.put(engPunctuations[i], chiPunctuations[i]);
	}
	
	public static char toChiPunctuation(char cha){
		if(toChiPunctuation.containsKey(cha)) cha=toChiPunctuation.get(cha);
		return cha;
	}
	
	/**
	 * 英语字母字符全角(SBC)半角(DBC)双向转换
	 */
	private static char[] letterSBCs={'Ａ','Ｂ','Ｃ','Ｄ','Ｅ','Ｆ','Ｇ','Ｈ','Ｉ','Ｊ','Ｋ','Ｌ','Ｍ','Ｎ','Ｏ','Ｐ','Ｑ','Ｒ','Ｓ','Ｔ','Ｕ','Ｖ','Ｗ','Ｘ','Ｙ','Ｚ','ａ','ｂ','ｃ','ｄ','ｅ','ｆ','ｇ','ｈ','ｉ','ｊ','ｋ','ｌ','ｍ','ｎ','ｏ','ｐ','ｑ','ｒ','ｓ','ｔ','ｕ','ｖ','ｗ','ｘ','ｙ','ｚ'};
	private static char[] letterDBCs={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	private static HashMap<Character, Character> toLetterDBC=new HashMap<Character, Character>();
	private static HashMap<Character, Character> toLetterSBC=new HashMap<Character, Character>();
	static{
		for(int i=0; i<letterSBCs.length; i++){
			toLetterDBC.put(letterSBCs[i], letterDBCs[i]);
			toLetterSBC.put(letterDBCs[i], letterSBCs[i]);
		}
	}

	private static char toLetterDBC(char chaSBC){
		if(toLetterDBC.containsKey(chaSBC)) chaSBC=toLetterDBC.get(chaSBC);
		return chaSBC;
	}
	
	public static String toLetterDBC(String strSBC){
		String strDBC="";
		for(char chaSBC:strSBC.toCharArray()) strDBC=strDBC+toLetterDBC(chaSBC);
		return strDBC;
	}
	
	private static char toLetterSBC(char chaDBC){
		if(toLetterSBC.containsKey(chaDBC)) chaDBC=toLetterSBC.get(chaDBC);
		return chaDBC;
	}
	
	public static String toLetterSBC(String strDBC){
		String strSBC="";
		for(char chaDBC:strDBC.toCharArray()) strSBC=strSBC+toLetterSBC(chaDBC);
		return strSBC;
	}

	public static boolean isLetter(char cha){
		boolean isNumber=false;
		if(toLetterDBC.containsKey(cha)||toLetterSBC.containsKey(cha)) isNumber=true;
		return isNumber;
	}
	
	public static boolean isLetter(String str){
		boolean isLetter=false;
		if(str.length()==1) if(isLetter(str.charAt(0))) isLetter=true;
		return isLetter;
	}

	public static boolean containsLetter(String str){
		boolean containsLetter=false;
		for(char cha:str.toCharArray()){
			if(isLetter(cha)){
				containsLetter=true;
				break;
			}
		}
		return containsLetter;
	}

	public static void main(String[] args)throws Exception{
		String strSBC="I ａm a bｏy.";
		System.out.println(strSBC);
		System.out.println(toLetterDBC(strSBC));
	}
}