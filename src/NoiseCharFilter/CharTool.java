package NoiseCharFilter;
import java.util.HashMap;
import java.util.HashSet;

public class CharTool{
	/**
	 * 全标点符号处理
	 */
	private static char[] punctuations={'。','！','？','；','，','、','•','.','．',',',';','：','∶',':','?','!','¨','…','｀','＇','′','＂','"','″','〃','┄','┈','＿','—','～','／','│',
										'“','”','‘','’','「','」','『','』','〔','〕','〈','〉','《','》','〖','〗','【','】','（','）','［','］','｛','｝','＜','＞','(',')','[',']','{','}','<','>',
										'+','＋','–','-','_','±','*','×','=','~','/','\\','@','#','$','%','&','°','®'};
	private static HashSet<Character> punctuation=new HashSet<Character>();
	static{
		for(int i=0; i<punctuations.length; i++) punctuation.add(punctuations[i]);
	}
	
	public static boolean isPunctuation(char cha){
		boolean isPunctuation=false;
		if(punctuation.contains(cha)) isPunctuation=true;
		return isPunctuation;
	}

	public static boolean isPunctuation(String str){
		boolean isPunctuation=false;
		if(str.length()==1) if(isPunctuation(str.charAt(0))) isPunctuation=true;
		return isPunctuation;
	}
	
	public static boolean containsPunctuation(String str){
		boolean containsPunctuation=false;
		for(char cha:str.toCharArray()){
			if(isPunctuation(cha)){
				containsPunctuation=true;
				break;
			}
		}
		return containsPunctuation;
	}
	
	public static boolean startsWithPunctuation(String str){
		boolean startsWithPunctuation=false;
		if(punctuation.contains(str.toCharArray()[0])) startsWithPunctuation=true;
		return startsWithPunctuation;
	}
	
	/**
	 * 格式符处理
	 */
	private static final char[] formats={'\r','\n','\f','\t','\b','　'};	
	
	public static String formatReplaceSpace(String str){
		for(char cha:formats) str=str.replace(cha, ' ');
		return str;
	}
	
	public static String formatStr(String str){
		str=formatReplaceSpace(str);
		str=removeTwoSpace(str);
		str=removeStartSpace(str);
		str=removeEndSpace(str);
		return str;
	}
	
	/**
	 * 空格符处理
	 */
	private static HashSet<Character> format=new HashSet<Character>();
	static{
		for(char c:formats) format.add(c);
		format.add(' ');
	}
	
	public static boolean isSpace(char cha){
		boolean isSpace=false;
		if(format.contains(cha)) isSpace=true;
		return isSpace;
	}
	
	public static boolean isSpace(String str){
		boolean isSpace=false;
		if(str.length()==1){
			if(isSpace(str.charAt(0))) isSpace=true;
		}
		return isSpace;
	}
	
	public static String removeTwoSpace(String str){
		while(str.contains("  ")) str=str.replace("  ", " ");
		return str;
	}	
	
	public static String removeStartSpace(String str){
		while(str.startsWith(" ")) str=str.replaceFirst(" ", "");
		return str;
	}
	
	public static String removeEndSpace(String str){
		return str.trim();
	}
	
	/**
	 * 数字字符全角(SBC)半角(DBC)双向转换
	 */
	private static char[] numberSBCs={'０','１','２','３','４','５','６','７','８','９'};
	private static char[] numberDBCs={'0','1','2','3','4','5','6','7','8','9'};
	private static HashMap<Character, Character> toNumberDBC=new HashMap<Character, Character>();
	private static HashMap<Character, Character> toNumberSBC=new HashMap<Character, Character>();	
	static{
		for(int i=0; i<numberSBCs.length; i++){
			toNumberDBC.put(numberSBCs[i], numberDBCs[i]);
			toNumberSBC.put(numberDBCs[i], numberSBCs[i]);
		}
	}

	private static char toNumberDBC(char chaSBC){
		if(toNumberDBC.containsKey(chaSBC)) chaSBC=toNumberDBC.get(chaSBC);
		return chaSBC;
	}
	
	public static String toNumberDBC(String strSBC){
		String strDBC="";
		for(char chaSBC:strSBC.toCharArray()) strDBC=strDBC+toNumberDBC(chaSBC);
		return strDBC;
	}
	
	private static char toNumberSBC(char chaDBC){
		if(toNumberSBC.containsKey(chaDBC)) chaDBC=toNumberSBC.get(chaDBC);
		return chaDBC;
	}
	
	public static String toNumberSBC(String strDBC){
		String strSBC="";
		for(char chaDBC:strDBC.toCharArray()) strSBC=strSBC+toNumberSBC(chaDBC);
		return strSBC;
	}
	
	public static boolean isNumber(char cha){
		boolean isNumber=false;
		if(toNumberDBC.containsKey(cha)||toNumberSBC.containsKey(cha)) isNumber=true;
		return isNumber;
	}
	
	public static boolean isNumber(String str){
		boolean isNumber=false;
		if(str.length()==1) if(isNumber(str.charAt(0))) isNumber=true;
		return isNumber;
	}
	
	public static boolean containsNumber(String str){
		boolean containsNumber=false;
		for(char cha:str.toCharArray()){
			if(isNumber(cha)){
				containsNumber=true;
				break;
			}
		}
		return containsNumber;
	}
	
	/**
	 * 带圈数字，字体(MS Mincho)
	 * ⓿ ❶❷❸❹❺❻❼❽❾❿ ⓫⓬⓭⓮⓯⓰⓱⓲⓳⓴
	 * ⓪ ①②③④⑤⑥⑦⑧⑨⑩ ⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳
	 * ㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚ ㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵ ㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿
	 */
	private static char[] numberCircles={'⓪','①','②','③','④','⑤','⑥','⑦','⑧','⑨','⑩','⑪','⑫','⑬','⑭','⑮','⑯','⑰','⑱','⑲','⑳','㉑','㉒','㉓','㉔','㉕','㉖','㉗','㉘','㉙','㉚','㉛','㉜','㉝','㉞','㉟','㊱','㊲','㊳','㊴','㊵','㊶','㊷','㊸','㊹','㊺','㊻','㊼','㊽','㊾','㊿'};
	
	public static String toNumberCircle(int i){
		String toNumberCircle;
		if(i<51) toNumberCircle=numberCircles[i]+"";
		else toNumberCircle="("+i+")";
		return toNumberCircle;
	}
	
	public static void main(String[] args)throws Exception{
		System.out.println(toNumberCircle(0)+toNumberCircle(50)+toNumberCircle(51));
	}
}