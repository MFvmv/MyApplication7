package cn.mmvtc.myapplication;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ComputerFragment extends BaseFragment {
    @Override
    public String initurl_banner() {
            return "http://www.mmvtc.cn/templet/jsjgcx/index.jsp";
    }
    @Override
    public String BannerBack(String str) {
        String content = "<div class=\"newsTitle text-center\">"+StringUtils.substringBetween(str, "<div class=\"newsTitle text-center\">", "<div class=\"returnList\">");
        content = StringUtils.replace(content,"src=\"/","src=\"http://www.mmvtc.cn/");
        return content;
    }
    @Override
    public void initBannerBack(String str) {
        isStart  = true;
        str = StringUtils.substringBetween(str,"<div class=\"tabs-content\">","<ul class=\"tabs\" data-tab>");
        Document document = Jsoup.parse(str);
        Elements elements = document.getElementsByTag("a");
        for (int i = 0;i<elements.size();i++){
            String href ="http://www.mmvtc.cn" + elements.get(i).attr("href");
            String path = "http://www.mmvtc.cn"+elements.get(i).getElementsByTag("img").attr("src");
            String title = elements.get(i).getElementsByTag("img").attr("alt");
            list_path.add(path);
            list_title.add(title);
            list_href.add(href);
//            Log.d("xxx","path = "+path);
//            Log.d("xxx","title = "+title);
//            Log.d("xxx","href = "+href);
        }
        list_href.remove(1);
        list_title.remove(1);
        list_path.remove(1);
    }

    @Override
    public String updataUrl(int page) {
        return "http://www.mmvtc.cn/templet/jsjgcx/ShowClass.jsp?id=1246&pn="+page;
    }
    @Override
    public void ListupdataBack(String str) {
        if(lastPage == 0){
            Document document = Jsoup.parse(str);
            Element element = document.getElementById("htmlPageCount");
            lastPage = Integer.parseInt(element.html());
            //Log.d("xxx",lastPage+"");
        }
        str = StringUtils.substringBetween(str,"<div class=\"cbox\">"," <div class=\"pageInfo\">");
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
    public String listback(String s) {
        return BannerBack(s);
    }

}
