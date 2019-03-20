package cn.mmvtc.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public abstract  class BaseFragment extends Fragment {

     ListView lv_school;
     RefreshLayout refreshLayout;
     MainActivity content = null;
     ArrayAdapter<String> adapter;
     ArrayList<String> data = new ArrayList<>();
     ArrayList<String> hrefdata = new ArrayList<>();
     Banner banner;
     int currentPage = 1;
     int lastPage = 0;
    ArrayList<String> list_path  = new ArrayList<>();
    ArrayList<String> list_title = new ArrayList<>();
    ArrayList<String> list_href = new ArrayList<>();
    boolean isEmployBannerOnClick = true;
    boolean isEmployListOnClick = true;
    boolean isJumpHref = true;
    String url_banner = "";

    boolean isStart = false;

    boolean isStartList = false;
    MFCallback AdultClickBack = null;

    public void setMainActivity(MainActivity mainActivity) {
        this.content = mainActivity;
    }
    public abstract String initurl_banner();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_school, container, false);
        //---------------------------------
        initBanner(view);
        //---------------------------------
        if(!isStart && !isStartList){
                    updata(1);

        }

        //Log.d("xxxa","我执行了");



        lv_school = view.findViewById(R.id.lv_school);

        adapter = new ArrayAdapter<String>(
                content, android.R.layout.simple_list_item_1, data);

        lv_school.setAdapter(adapter);
        if(isEmployListOnClick)
        lv_school.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(content, hrefdata.get(position), Toast.LENGTH_SHORT).show();
                String str = hrefdata.get(position);
                if(str.indexOf("http://www.mmvtc.cn") == -1 && isJumpHref){
                    Uri uri = Uri.parse(str);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else if(str.charAt(0) == '0'){
                    Log.d("xxx","AdultClickBack");
                    AdultClickBack.back(str);
                } else {
                    content.getnetworkData(str,new MFCallback(){
                        @Override
                        public String back(String s) {
                            return listback(s);
                        }
                    });
                }
                Log.d("xxx","点击执行完");

            }
        });


        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.finishRefresh(true);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if(currentPage>=lastPage){
                    refreshlayout.finishLoadmore(false);
                    Toast.makeText(content,"没有更多内容了" , Toast.LENGTH_SHORT).show();
                }
                updata(++currentPage);
            }
        });

        return view;
    }


    public  void initBanner(View view){
        url_banner = initurl_banner();
        banner = view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setDelayTime(3000);
        if(isStart){
            setbanner();
        }else {
            getBannerData();
        }
    }

    public void setbanner(){
        banner.setImages(list_path);
        banner.setBannerTitles(list_title);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        if(isEmployBannerOnClick)
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    content.getnetworkData(list_href.get(position),schoolBack);
                }
            });
    }

    public abstract String listback(String s);

    public abstract void initBannerBack(String str);
    public void getBannerData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpGet httpGet = new HttpGet(url_banner);
                    httpGet.setHeader("Cookie", content.getCookie());


                    HttpClient client = new DefaultHttpClient();

                    HttpResponse httpResponse = client.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        String str = EntityUtils.toString(httpResponse.getEntity());
                        //------------------
                        initBannerBack(str);
                        //------------------
                        //Log.d("xxx",str);
                        content.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置图片集合
                                setbanner();
                            }
                        });
                    }else {
                        MFToast("网络连接失败");
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }).start();
    }
    public abstract void ListupdataBack(String str);
    public abstract String updataUrl(int page);
    public void updata(final int page){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("xxx","updata url = "+updataUrl(page));
                try {
                    HttpGet httpGet = new HttpGet(updataUrl(page));
                    //设置请求的报文头部的编码
                    httpGet.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));

                    //设置期望服务端返回的编码
                    httpGet.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
                    httpGet.setHeader("Cookie", content.getCookie());
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse httpResponse = client.execute(httpGet);
                    Log.d("xxx","----");
                    if(httpResponse.getStatusLine().getStatusCode() == 200){

                        String str = EntityUtils.toString(httpResponse.getEntity());
                        //------------
                        ListupdataBack(str);
                        //------------
                        content.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                refreshLayout.finishLoadmore(true);
                            }
                        });
                    }else {
                        MFToast("网络连接失败");
                        content.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishLoadmore(false);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void MFToast(final String str){
        content.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(content, str, Toast.LENGTH_LONG).show();
            }
        });
    }
    public  abstract String  BannerBack(String str);
    public MFCallback schoolBack = new MFCallback() {
        @Override
        public String back(String s) {
            return BannerBack(s);
        }
    };





}
