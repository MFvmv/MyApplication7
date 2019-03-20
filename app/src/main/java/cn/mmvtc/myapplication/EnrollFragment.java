package cn.mmvtc.myapplication;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;

public class EnrollFragment extends BaseFragment {



    @Override
    public String initurl_banner() {
        return "http://websites.mmvtc.cn:808/zsw/index.php?url=site/index";
    }
    @Override
    public void initBannerBack(String str) {
        isEmployBannerOnClick = false;
        str = StringUtils.substringBetween(str,"<div class=\"b-img\">","</div>");
        Document document = Jsoup.parse(str);
        Elements elements = document.getElementsByTag("a");


        for (int i = 0;i<elements.size();i++){
            String path =  StringUtils.substringBetween(elements.get(i).attr("style"),"(",")");
            Log.d("xxx","path = "+path);
            list_path.add(path);
            list_title.add("");
            list_href.add("");
        }
    }
    public static String toUtf8(String str) {
                 String result = null;
                try {
                         result = new String(str.getBytes("gbk"), "gbk");
                     } catch (UnsupportedEncodingException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                 return result;
    }
    @Override
    public void ListupdataBack(String str) {
        //isJumpHref = false;

        if(lastPage == 0){
            String temp = StringUtils.substringBetween(str,"<div class=\"pagination m-auto clearfix\">","</ul>");
            Document document = Jsoup.parse(temp);
            Elements elements = document.getElementsByTag("a");
            lastPage = Integer.parseInt(elements.get(elements.size()-2).html());
            Log.d("xxx","lastPage = "+lastPage);
            //System.exit(0);
        }
        str = StringUtils.substringBetween(str,"<div class=\"list-content\">","</ul>");
        Document document = Jsoup.parse(str);
        Elements elements = document.getElementsByTag("a");
        for (int i = 0 ;i<elements.size();i++){
            data.add(StringUtils.substringAfter(elements.get(i).html(),">"));
            String temp = elements.get(i).attr("href");
            hrefdata.add(temp.charAt(0) == '/'?"http://websites.mmvtc.cn:808"+temp:temp);
            Log.d("xxxc","hrefdata.get(i) = " +hrefdata.get(i));
            Log.d("xxxc","data.get(i) = " +data.get(i));
        }
    }

    @Override
    public String updataUrl(int page) {
        return "http://websites.mmvtc.cn:808/zsw/index.php?url=site/partList&groups_id=8&page="+page;
    }
    @Override
    public String listback(String s) {
        Log.d("xxxc","s = "+s);
        String content = StringUtils.substringBetween(s, "<div class=\"article-content\">", "&nbsp;<br />");
        Log.d("xxxc","content = "+content);
        content = StringUtils.replace(content,"src=\"/","src=\"http://www.mmvtc.cn/");
        return content;
    }

    @Override
    public String BannerBack(String str) {
        return null;
    }
}
