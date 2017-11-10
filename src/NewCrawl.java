import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
public class NewCrawl{
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
			BufferedReader bf=new BufferedReader(new InputStreamReader(huc.getInputStream(), "gbk"));
			StringBuffer sb=new StringBuffer();
			String line=null;
			while((line=bf.readLine())!=null){				
				sb.append(line+"\n");
			}
			result=sb.toString();
			bf.close();
		}else if(huc.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND){
			System.err.println("HTTP_NOT_FOUND");
		}
		return result;	
	}
	
	public static List<String> getURL(String url) throws Exception{
		/**
		 * 通过两层的抓取，获取新闻url
		 */
		String hostPage = getPage(url);
		Pattern pattern = Pattern.compile("http://(\\w){0,5}\\.163\\.com/(\\d){2}(.)*?\\.html");
		System.out.println(hostPage);
		Matcher matcher = pattern.matcher(hostPage);
		List<String> urls =new ArrayList<String>();
		while (matcher.find()){
			if(!urls.contains(matcher.group())){
				System.out.println(matcher.group());
				urls.add(matcher.group());
			}
		}
		try{
			for(int i=0;i<urls.size();i++){
				String page = getPage(urls.get(i));
				matcher = pattern.matcher(page);
				while (matcher.find()){
					if(!urls.contains(matcher.group())){
						System.out.println(matcher.group());
						urls.add(matcher.group());
					}
				}
			}
		}
		catch(Exception e){
		}
		return urls;
	}

	public static List<NewInfo> getNewsInfo(List<String> urls) throws Exception{
		/**
		 * 匹配新闻内容，包括标题及正文
		 */
		Pattern pattern0 = Pattern.compile("(?s)<div class=\"post_text\" id=\"endText\"(.*?)</div>");//匹配正文所在类
		Pattern pattern1 = Pattern.compile("(?s)<p>(.*?)</p>");//匹配正文
		Pattern titlePattern =  Pattern.compile("<title>(.*?)</title>");//匹配新闻标题
		List<NewInfo> result = new ArrayList<NewInfo>();
		for(String url:urls){
			try{
					String source = getPage(url);
	//				System.out.println(source);
					Matcher titleMatcher = titlePattern.matcher(source);
					Matcher matcher = pattern0.matcher(source);
					String title;
					if(titleMatcher.find()){
						title = titleMatcher.group(1);
						title = title.replace("/", " ");
					}
					else{
						continue;
					}
					String content = "";
					if(matcher.find()){
						System.out.println("\n-----------------------------------------------\n");
						String term = matcher.group();
						matcher = pattern1.matcher(term);
						
						while(matcher.find()){
							content+=matcher.group(1)+"\n";
						}
						System.out.println(content);
					}
					else{
						continue;
					}
					result.add(new NewInfo(url,title,content));
				}
			catch(Exception e){
			}
		}
		return result;
	}
	public static void main(String[] args)throws Exception{
		List<String> urls = getURL("http://news.163.com/");
//		append("pages/testPage.txt", getPage("http://vietnam.vnanet.vn/vietnamese/tien-hanh-ca-phau-thuat-ung-thu-da-day-dau-tien-bang-robot/287870.html"), "UTF-8");
		for(String url:urls){
			System.out.println(url);
		}
		List<NewInfo> newInfoList = getNewsInfo(urls);
		for(NewInfo newInfo:newInfoList){
			String content = "";
			content+="新闻标题："+newInfo.title+"\n";
			content+= "新闻url："+newInfo.url+"\n";
			content+= "新闻正文：\n"+newInfo.content;
			append("pages/"+newInfo.title+".txt",content,"utf-8");
		}

			
		
	}
}