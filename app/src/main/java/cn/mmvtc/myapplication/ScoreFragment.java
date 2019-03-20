package cn.mmvtc.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreFragment extends Fragment {

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> list = new ArrayList<Map<String,String>>();
    private String refererUrl = "";
    private String scoreUrl = "";
    private String cookie = "";
    private String viewstate = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        refererUrl = MainActivity.getRefererUrl();
        scoreUrl = MainActivity.getScoreUrl();
        cookie = MainActivity.getCookie();

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new SimpleAdapter(getActivity(), list, R.layout.score_item, new String[]{"xueqi","className","score"}, new int[]{R.id.xueqi,R.id.className,R.id.score});
        listView.setAdapter(adapter);

        new Thread(runnable).start();

        return view;
    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            getViewstate();
            getScore();
            handler.sendEmptyMessage(0);
        }
    };

    private void getViewstate() {//获取检验密钥，因为每个人的不一样，所以要动态获取
        HttpGet httpGet = new HttpGet(scoreUrl);
        httpGet.setHeader("Cookie", cookie);//设置cookie
        httpGet.setHeader("Referer", refererUrl);//设置上一个网页的网址
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(httpResponse.getEntity());
                Document html = Jsoup.parse(content);
                Elements e = html.select("input[name=__VIEWSTATE]");//这里的到密钥
                viewstate = e.get(0).attr("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getScore() {

        HttpPost httpPost = new HttpPost(scoreUrl);
        httpPost.setHeader("Cookie", cookie);//设置cookie
        httpPost.setHeader("Referer", refererUrl);//设置上一个网页的网址
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:47.0) Gecko/20100101 Firefox/47.0");//设置浏览器信息，教务网没有检验浏览器，这里可以不用写

        List<NameValuePair> data = new ArrayList<NameValuePair>();//设置post提交的数据
        data.add(new BasicNameValuePair("__EVENTTARGET",""));
        data.add(new BasicNameValuePair("__EVENTARGUMENT",""));
        data.add(new BasicNameValuePair("__VIEWSTATE",viewstate));
        data.add(new BasicNameValuePair("ddlXN",""));
        data.add(new BasicNameValuePair("ddlXQ",""));
        data.add(new BasicNameValuePair("ddl_kcxz",""));
        data.add(new BasicNameValuePair("btn_zcj","历年成绩"));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(httpResponse.getEntity());

                getScoreItem(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getScoreItem(String html) {//解析html获取数据
        Document content = Jsoup.parse(html);
        Element ScoreList = content.getElementById("Datagrid1");
        Elements tr = ScoreList.getElementsByTag("tr");
        for (int i = 1; i < tr.size(); i++) {
            Elements td = tr.get(i).getElementsByTag("td");
            String xueqi = td.get(1).text();
            String className = td.get(3).text();
            String score = td.get(8).text();
            Map<String, String> map = new HashMap<String, String>();
            map.put("xueqi", xueqi);
            map.put("className", className);
            map.put("score", score);
            list.add(map);
        }
    }
}
