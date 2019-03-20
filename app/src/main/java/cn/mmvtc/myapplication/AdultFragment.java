package cn.mmvtc.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AdultFragment extends BaseFragment {

    String navigation[] ={"招生信息->","1240","资源下载->","1248","函授教育->","1241","在线培训->","1564"};
    final String testGit = " ";
    String url = "";
    @Override
    public String initurl_banner() {
        isStartList = true;
        isJumpHref = false;
        return "http://www.mmvtc.cn/templet/skb/";
    }

    public  void LoadNavigation(){
        for (int i  = 0;i<navigation.length;i = i+2){
            data.add(navigation[i]);
            hrefdata.add("01"+i+"http://www.mmvtc.cn/templet/crjyb/ShowClass.jsp?id="+navigation[i+1]+"&pn=");
        }
    }


    //只执行一次
    @Override
    public void initBannerBack(String str) {
        isStart = true;
        Log.d("xxx","initBannerBack");
        AdultClickBack = new MFCallback(){
            @Override
            public String back(String s) {
                s = s.substring(1);
                if(s.charAt(0) == '1'){
                    refreshLayout.setEnableLoadmore(true);
                    s = s.substring(1);
                    data.clear();
                    hrefdata.clear();
                    data.add("<-返回");
                    hrefdata.add("02");
                    url = s.substring(1);
                    Log.d("xxxu","url = "+url);
                    currentPage = 1;
                    updata(1);
                }else if(s.charAt(0) == '2'){
                    refreshLayout.setEnableLoadmore(false);
                    Log.d("xxx","返回");
                    data.clear();
                    hrefdata.clear();
                    LoadNavigation();
                    adapter.notifyDataSetChanged();
                } else {
                    Uri uri = Uri.parse(s);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                return null;
            }
        };

        LoadNavigation();
        content.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        isEmployBannerOnClick = false;
        list_path.add("http://www.mmvtc.cn/templet/crjyb/iamges/0001.png");
        list_title.add("");
        list_href.add("");
        Log.d("xxx","endinitBannerBack");
    }

    @Override
    public void ListupdataBack(String str) {
        //Log.d("xxx","str = "+str);
        if(isStart){
            Log.d("xxx","ListupdataBack");
            if(lastPage == 0){
                Document document = Jsoup.parse(str);
                Element element = document.getElementById("htmlPageCount");
                lastPage = Integer.parseInt(element.html());
                //Log.d("xxx",lastPage+"");
            }

            str = "<h4>" + StringUtils.substringBetween(str,"<h4>","<div id=\"page\">");

            String temp = StringUtils.substringAfter(str,"<a");
            temp = StringUtils.substringAfter(temp,"<a");
            while (!temp.equals("")){
                String Bstr = StringUtils.substringBetween(temp,"<strong>","</font>");
                data.add(Bstr != null?Bstr:StringUtils.substringBetween(temp,"\">","</a>"));

                String tmp = StringUtils.substringBetween(temp,"href=\"","\"");
                if(tmp.charAt(0) =='/') tmp = "http://www.mmvtc.cn"+tmp;
                hrefdata.add("0"+tmp);
                Log.d("xxxh","temp = "+temp);
                temp = StringUtils.substringAfter(temp,"<a");
            }
    }

    }
    @Override
    public String updataUrl(int page) {
        return url+page;
    }


    //-------------------不使用-----------------------
    @Override
    public String listback(String s) {
        return null;
    }
    @Override
    public String BannerBack(String str) {
        return null;
    }
    //-----------------------------------------------

}
