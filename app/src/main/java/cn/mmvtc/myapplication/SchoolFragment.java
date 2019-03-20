package cn.mmvtc.myapplication;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SchoolFragment extends BaseFragment {

    @Override
    public String initurl_banner() {
        return "http://www.mmvtc.cn/templet/default/aboutme.html";
    }

    @Override
    public String listback(String s) {
        String content = StringUtils.substringBetween(s, "<!--具体内容标题部分开始-->", "<div class=\"bdsharebuttonbox ml-20\">");
        content = StringUtils.remove(content, "<span class=\"mr-10\">文章来源:</span>");
        content = StringUtils.replace(content,"src=\"/","src=\"http://www.mmvtc.cn/");
        //Log.d("xxxc",content);
        return content;
    }

    @Override
    public String updataUrl(int page) {
        return "http://www.mmvtc.cn/templet/default/ShowClassPage.jsp?id=915&pn="+page;
    }

    @Override
    public void initBannerBack(String str) {
        isStart  = true;
        str = StringUtils.substringBetween(str,"<ul class=\"subChannelList list-unstyled\">","</ul>");
        Document document = Jsoup.parse(str);
        Elements elements = document.getElementsByTag("a");
        for (int i = 0;i<elements.size();i = i+2){
            String href =  elements.get(i).attr("href");
            String path = "http://www.mmvtc.cn/templet/default/"+elements.get(i).getElementsByTag("img").attr("src");
            String title = elements.get(i+1).html();
            list_path.add(path);
            list_title.add(title);
            list_href.add(href);
            //Log.d("xxx","path = "+path);
        }
    }

    @Override
    public void ListupdataBack(String str) {
        if(lastPage == 0){
            Document document = Jsoup.parse(str);
            Element element = document.getElementById("htmlPageCount");
            lastPage = Integer.parseInt(element.html());
            //Log.d("xxx",lastPage+"");
        }
        str = StringUtils.substringBetween(str,"<!--具体内容开始-->","<div id=\"page\">");
        Document document = Jsoup.parse(str);
        Elements elements = document.getElementsByTag("a");
        for (int i = 0 ;i<elements.size();i++){
            data.add(elements.get(i).html());
            String temp = elements.get(i).attr("href");
            hrefdata.add(temp.charAt(0) == '/'?"http://www.mmvtc.cn"+temp:temp);
        }
        //Log.d("xxx", String.valueOf(elements.get(0).attr("href"))+"----");
        //Log.d("xxx", String.valueOf(elements.get(0).html())+"----");
    }



    @Override
    public String BannerBack(String str) {
        String content = StringUtils.substringBetween(str, "<!--具体内容标题部分开始-->", "<!--内容页结束-->");
        //Log.d("xxx","back:"+content);
        content = StringUtils.remove(content, "<span class=\"mr-10\">文章来源:</span>");
        return content;
    }
}
