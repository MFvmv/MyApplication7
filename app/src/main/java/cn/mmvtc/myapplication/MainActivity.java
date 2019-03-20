package cn.mmvtc.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
//import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zzhoujay.richtext.RichText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


    public class MainActivity extends FragmentActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

        private String studentName;
        private static String cookie;
        private String name;
        private String url = "http://jwc.mmvtc.cn/";
        private static String refererUrl = "";
        private static String infoUrl = "";
        private static String scoreUrl = "";
        private ViewPager pager;
        private RadioGroup radioGroup;
        private RadioButton rb_me, rb_score,rb_enroll;
        private ImageButton ib_refresh;
        private TextView tv_studentName;
        private FragmentAdapter adapter;
        private boolean flag = false;


        //----------------------------------
        private NavigationView nav;
        private DrawerLayout daw_layout;
        private ImageButton ib_touxiang;
        private boolean isLogin = false;

        //----------------------------------




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            RichText.initCacheDir(this);
            Fresco.initialize(this);

//            Intent intent = new Intent(MainActivity.this,ContentActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("html","xsadd");
//            intent.putExtras(bundle);
//            startActivity(intent);

            Intent intent = getIntent();//获取intent传过来的数据
            if(intent.getStringExtra("name") != null){
             name = intent.getStringExtra("name");
             studentName = intent.getStringExtra("studentName");
             cookie = intent.getStringExtra("cookie");
             infoUrl = url + intent.getStringExtra("infoUrl");
             scoreUrl = url + intent.getStringExtra("scoreUrl");
             refererUrl = "http://jwc.mmvtc.cn/xs_main.aspx?xh=" + name;
             isLogin = true;
            }



        init();//初始化控件
        }
        final PopupWindow popupWindow = new PopupWindow();
        //初始化控件
        private void init() {
            //------------------------------------------
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup,null);
            popupWindow.setContentView(view);
            nav = findViewById(R.id.nav_view);
            daw_layout = findViewById(R.id.daw_layout);
            daw_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ib_touxiang = findViewById(R.id.touxiang);
            ib_touxiang.setOnClickListener(this);

            if (isLogin){
                nav.getMenu().getItem(1).setVisible(true);
                nav.getMenu().getItem(2).setVisible(true);
            }else {
                nav.getMenu().getItem(0).setVisible(true);
            }

            //nav.getMenu().getItem(1).setVisible(true);
            //RichText.fromHtml("ghgh").into(tv_html);
            nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    if(item.getItemId() == R.id.it_login){
                        Intent intent = new Intent(cn.mmvtc.myapplication.MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(cn.mmvtc.myapplication.MainActivity.this,MyActivity.class);
                        Bundle bundle = new Bundle();
                        switch (item.getItemId()){
                            case R.id.it_me:
                                bundle.putInt("select",1);
                                break;
                            case R.id.it_score:
                                bundle.putInt("select",2);
                                break;
                            case R.id.it_about:
                                bundle.putInt("select",3);
                                break;
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    return true;

                }
            });


            //------------------------------------------

            radioGroup = (RadioGroup) findViewById(R.id.rg_tab_bar);
            rb_me = (RadioButton) findViewById(R.id.rb_me);
            rb_score = (RadioButton) findViewById(R.id.rb_score);
            rb_enroll = findViewById(R.id.rb_enroll);
            ib_refresh = (ImageButton) findViewById(R.id.ib_refresh);
            tv_studentName = (TextView) findViewById(R.id.tv_studentName);

            tv_studentName.setText(studentName);
            ((TextView)nav.getHeaderView(0).findViewById(R.id.tv_name)).setText(studentName);
            rb_me.setChecked(true);
            radioGroup.setOnCheckedChangeListener(this);

            List<Fragment> fragments = new ArrayList<Fragment>();//设置fragment
            //-------------------------------------------
            SchoolFragment fragment = new SchoolFragment();
            fragment.setMainActivity(this);
            ComputerFragment fragment1 = new ComputerFragment();
            fragment1.setMainActivity(this);
            //EnrollFragment fragment2 = new EnrollFragment();
            AdultFragment fragment2 = new AdultFragment();
            fragment2.setMainActivity(this);
            //Fragment1.getnetworkData("http://www.mmvtc.cn/templet/default/ShowArticle.jsp?id=47054","<!--具体内容标题部分开始-->","</SPAN></SPAN></P></DIV></DIV>");
            fragments.add(fragment);
            fragments.add(fragment1);
            fragments.add(fragment2);
            //fragments.add(new ScoreFragment());
            //-------------------------------------------
            adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);//初始化adapter

            pager = (ViewPager) findViewById(R.id.viewpager);//设置ViewPager
            pager.setOffscreenPageLimit(1);
            pager.setAdapter(adapter);
            pager.setCurrentItem(0);
            pager.setOnPageChangeListener(this);

            ib_refresh.setOnClickListener(new View.OnClickListener() {//刷新按钮事件，没效果
                @Override
                public void onClick(View arg0) {
                    adapter.notifyDataSetChanged();
                }
            });


        }
        @Override
        public void onPageScrollStateChanged(int arg0) {//ViewPager滑动事件
            if (arg0 == 2) {
                switch (pager.getCurrentItem()) {
                    case 0:
                        rb_me.setChecked(true);
                        break;

                    case 1:
                        rb_score.setChecked(true);
                        break;
                    case 2:
                        rb_enroll.setChecked(true);
                        break;
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageSelected(int arg0) {}

        @Override
        public void onCheckedChanged(RadioGroup arg0, int arg1) {//单选按钮选择事件
            switch (arg1) {
                case R.id.rb_me:
                    pager.setCurrentItem(0);
                    break;
                case R.id.rb_score:
                    pager.setCurrentItem(1);
                    break;
                case R.id.rb_enroll:
                    pager.setCurrentItem(2);
                    break;
            }
        }


        public static String getRefererUrl() {//跨activity传值
            return refererUrl;
        }

        public static String getInfoUrl() {
            return infoUrl;
        }

        public static String getScoreUrl() {
            return scoreUrl;
        }

        public static String getCookie() {
            return cookie;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {//双击退出软件
            if(keyCode==KeyEvent.KEYCODE_BACK){
                if(flag==false){
                    flag=true;
                    Toast.makeText(getApplicationContext(), "再按一次退出软件", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flag=false;
                        }
                    }, 2000);
                }else{
                    finish();
                    System.exit(0);
                }
            }
            return false;
        }



        public void  sendMsg(Message msg){
            handler.sendMessage(msg);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.touxiang:
                    if (daw_layout.isDrawerOpen(nav)){
                        daw_layout.closeDrawer(nav);
                    }else{
                        daw_layout.openDrawer(nav);
                    }
                    break;

            }
        }

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    case 4://关闭popupWindow
                        popupWindow.dismiss();
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                        break;
                    case 1:
                        //Log.d("xxx","222");
                        Intent intent = new Intent(cn.mmvtc.myapplication.MainActivity.this,ContentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("html",(String) msg.obj);
                        intent.putExtras(bundle);
                        //Log.d("xxx",(String) msg.obj);
                        popupWindow.dismiss();
                        startActivity(intent);
                        break;
                    case 2://开启popupWindow
                        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
                        break;
                }
            }
        };




        public void getnetworkData(final String url, final MFCallback back) {
            handler.sendEmptyMessage(2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("xxx","ok");
                        HttpGet httpGet = new HttpGet(url);
                        httpGet.setHeader("Cookie", cookie);
                        HttpClient client = new DefaultHttpClient();
                        HttpResponse httpResponse = client.execute(httpGet);
                        Log.d("xxx","ok1");
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            String str = EntityUtils.toString(httpResponse.getEntity());
                            //Log.d("xxx","----"+content);
                            Log.d("xxx","ok2");
                            if (back != null) {
                                Log.d("xxx","back ！= null");
                                str = back.back(str);
                            }
                            //Log.d("xxx",str+"xxx---");
                            Message msg = new Message();
                            msg.obj = str;
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.obj = "网络连接失败";
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();




        }









    }

