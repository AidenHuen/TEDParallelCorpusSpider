	import java.io.BufferedReader;
	import java.io.InputStreamReader;
	import java.io.RandomAccessFile;
	import java.net.HttpURLConnection;
	import java.net.URL;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.regex.*;
	
public class TedCrawl {
	    static Pattern patternForTime = Pattern.compile("<data class=.?talk-transcript__para__time.?>(.*?)</data>");//匹配时间
	    static Pattern patternForContent = Pattern.compile("<span class=.?talk-transcript__fragment.? data-time='.*?' id='.*?'>(.*?)</span>");//匹配正文
	    static Pattern patternForClass= Pattern.compile("<p class=.?talk-transcript__para.?>(.*?)</p>");//匹配正文所在类
	    
		public static void append(String file, String str, String code)throws Exception{
			RandomAccessFile raf=new RandomAccessFile(file, "rw");
			raf.seek(raf.length());
			raf.write(str.getBytes(code));
			raf.close();
		}
		
		public static String getPage(String url)throws Exception{
			String result=null;
			HttpURLConnection huc=(HttpURLConnection) new URL(url).openConnection();
			huc.setRequestMethod("GET");
			huc.setUseCaches(true);
			huc.connect();
			if(huc.getResponseCode()==HttpURLConnection.HTTP_OK){
				BufferedReader bf=new BufferedReader(new InputStreamReader(huc.getInputStream(), "utf-8"));
				StringBuffer sb=new StringBuffer();
				String line=null;
				while((line=bf.readLine())!=null){				
					sb.append(line);
				}
				result=sb.toString();
				bf.close();
			}else if(huc.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND){
				System.err.println("HTTP_NOT_FOUND");
			}
			return result;	
		}
		
		public static List<String> getURL(String address) throws Exception{
			/*
			 * 获取url
			 */
			String hostPage = getPage(address);

			Pattern pattern = Pattern.compile("<div class='talk-link'>.*?talks/(.*?).language=");
//			System.out.println(hostPage);
			Matcher matcher = pattern.matcher(hostPage);
			List<String> urls =new ArrayList<String>();
			while (matcher.find()){
				if(!urls.contains(matcher.group(1))){
					urls.add(matcher.group(1));
				}
			}
			return urls;
		}
		
		public static List<TalkContent>  getTalkContent(String page){
			/*
			 * 获取演讲 时间，正文
			 */
			 List<TalkContent> result = new ArrayList<TalkContent>();
			Matcher Classmatcher = patternForClass.matcher(page);

			while(Classmatcher.find()){
				String Class= Classmatcher.group(1);
				String time="";
				String content="";
				Class = Class.replace("\n", " ").replace("\r", "");
				Matcher Timematcher = patternForTime.matcher(Class);
				if(Timematcher.find()){
					time = Timematcher.group(1);
				}
				Matcher contentmatcher = patternForContent.matcher(Class);
				while(contentmatcher.find()){
					String sentence = contentmatcher.group(1);
//					System.out.println(sentence);
					content+=sentence;
				}
//				System.out.println(Class);
//				System.out.println(time+" "+content);
				result.add(new TalkContent(time,content));
			}
//			System.out.println("One Talk OK");
			return result;
		}
		
		private static boolean timeJudegement(List<TalkContent> mainTalk,List<TalkContent> subTalk){
			/*
			 * 基于时间，判断语料是否平行
			 */
			if(mainTalk.size()!=subTalk.size()){
				return false;
			}
			for(int i=0;i<mainTalk.size();i++){
				if(!mainTalk.get(i).time.equals(subTalk.get(i).time)){
					return false;
				}
			}
			return true;
		}
		public static void saveParallelCorpus(String mainLang,String subLang,int pageNum,String saveAddress) throws Exception{
			/*
			 * 根据语言，以及爬取页数，以及存储地址，爬取语料。并保存为平行语料的形式
			 */
			for(int num=1;num<=pageNum;num++){
				String address= "https://www.ted.com/talks?language="+mainLang+"&page="+num+"&sort=newest";
				List<String> urls = getURL(address);
				String head="https://www.ted.com/talks/";
				String malay_tail = "/transcript?language="+mainLang;
				String en_tail = "/transcript?language="+subLang;
				for(String url:urls){
					String main_page;
					try{
						main_page = getPage(head+url+malay_tail);
					}
					catch(Exception e){
						System.out.println("no main website");
						continue;
					}
					String sub_page = "";
					try{
						sub_page = getPage(head+url+en_tail);
					}
					catch(Exception e){
						System.out.println("no sub website");
						continue;
					}
//					System.out.println(page);
					System.out.println(head+url+malay_tail);
					List<TalkContent>mainTalk;
					try{
						mainTalk = getTalkContent(main_page);
					}
					catch(Exception e){
						System.out.println("no mainTalk website");
						continue;
					}
					List<TalkContent> subTalk;
					try{
						subTalk = getTalkContent(sub_page);
					}
					catch(Exception e){
						System.out.println("no subTalk website");
						continue;
						
					}

					if(timeJudegement(mainTalk,subTalk)){
						for(int i=0;i<mainTalk.size();i++){
							System.out.println(mainTalk.get(i).time);
							System.out.println(mainTalk.get(i).sentences);
							System.out.println(subTalk.get(i).sentences);
							String page = "时间："+mainTalk.get(i).time+"\n"+mainLang+":"+mainTalk.get(i).sentences+"\n"+subLang+":"+subTalk.get(i).sentences+"\n";
							append(saveAddress+"\\"+url+".txt",page,"utf-8");
							System.out.println("save success!!");
						}
					}

				}	
			}

		}

		public static void main(String[] args)throws Exception{
			saveParallelCorpus("zh-cn","en",64,"zh_en");
		}
}
