package cn.mmvtc.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//        在手机端模拟浏览器登录服务器的过程。让服务器认为手机登录就和在浏览器登录一样。
//                1.获得cookie
//                2.将用户名，密码，验证码提交给服务器。
//                3.成功后，获取个人信息链接，和学生学习成绩链接。
//                4.输入正确的用户名，密码，验证码，点击登录按钮，向服务器提交数据


public class LoginActivity extends Activity implements OnClickListener {

    private EditText et_name, et_password, et_vertify;
    private ImageView iv_vertify;
    private Button btn_login;
    private CheckBox cb_isSave;
    private String urlStr = "http://jwc.mmvtc.cn/CheckCode.aspx";
    private String loginUrl = "http://jwc.mmvtc.cn/default2.aspx";
    private String cookie = "";
    private String name = "";
    private String password = "";
    private String vertify = "";
    private String studentName = "";
    private String infoUrl = "";
    private String scoreUrl = "";
    private boolean flag = false;

    private TextView tv_login_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_login_back = findViewById(R.id.tv_login_back);
        tv_login_back.setOnClickListener(this);

        init();// 初始化控件
        read();// 读取保存的账号密码

        new Thread(vertifyRun).start();// 获取验证码
    }

    // 读取保存的账号密码
    private void read() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String strName = sp.getString("name", null);
        String strPassword = sp.getString("password", null);
        if (!(TextUtils.isEmpty(strName) && TextUtils.isEmpty(strPassword))) {// 账号密码不为空
            et_name.setText(strName);
            et_password.setText(strPassword);
        }
    }

    // 保存账号密码
    private void save(String name, String password) {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("password", password);
        editor.commit();
    }

    // 初始化控件
    private void init() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_vertify = (EditText) findViewById(R.id.et_vertify);
        iv_vertify = (ImageView) findViewById(R.id.iv_vertify);
        btn_login = (Button) findViewById(R.id.btn_login);
        cb_isSave = (CheckBox) findViewById(R.id.cb_isSave);

        iv_vertify.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    private void loginFail(String tip) {
        Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_LONG).show();
        et_vertify.setText("");
        new Thread(refreshRun).start();
    }

    // handler更新界面
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 显示验证码
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv_vertify.setImageBitmap(bitmap);
                    break;
                case 2:// 验证码错误
                    loginFail("验证码错误");
                    break;
                case 3:// 密码错误
                    loginFail("密码错误");
                    break;
                case 4:// 用户不存在
                    loginFail("用户名不存在或未按照要求参加教学活动");
                    break;
                case 5:// 登陆成功
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG)
                            .show();

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);// activity携带数据跳转，数据没进行封装
                    intent.putExtra("name", name);
                    intent.putExtra("studentName", studentName);
                    intent.putExtra("cookie", cookie);
                    intent.putExtra("infoUrl", infoUrl);
                    intent.putExtra("scoreUrl", scoreUrl);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
            }
        }
    };

    // 登陆
    public void doLogin() {
        name = et_name.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vertify = et_vertify.getText().toString().trim();

        if (name.equals("") || password.equals("")) {
            Toast.makeText(this, "学号或者密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (vertify.length() != 4) {
            Toast.makeText(this, "请输入4位验证码", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(loginRun).start();
    }

    // 提交数据执行登陆
    private void login() {
        HttpPost httpPost = new HttpPost(loginUrl);//登录网址
        httpPost.setHeader("Cookie", cookie);// 设置cookie

        List<NameValuePair> list = new ArrayList<NameValuePair>();// 设置post提交的数据
        list.add(new BasicNameValuePair("__VIEWSTATE",
                "dDw3OTkxMjIwNTU7Oz5qFv56B08dbR82AMSOW+P8WDKexA=="));
        list.add(new BasicNameValuePair("TextBox1", name));// 用户名
        list.add(new BasicNameValuePair("TextBox2", password));// 密码
        list.add(new BasicNameValuePair("TextBox3", vertify));// 验证码
        list.add(new BasicNameValuePair("RadioButtonList1", "学生"));
        list.add(new BasicNameValuePair("Button1", ""));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {// 状态200为成功
                String content = EntityUtils.toString(httpResponse.getEntity());// 获取服务器响应内容
                checkLogin(content);// 判断登陆结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断用户登陆结果
    private void checkLogin(String content) {
        Message msg = new Message();
        if (content.indexOf("验证码不正确") != -1) {
            msg.what = 2;
        } else if (content.indexOf("密码错误") != -1) {
            msg.what = 3;
        } else if (content.indexOf("用户名不存在或未按照要求参加教学活动") != -1) {
            msg.what = 4;
        } else if (content.indexOf("欢迎您") != -1) {
            Document html = Jsoup.parse(content);

            Element nameEle = html.getElementById("xhxm");//javascript dom
            studentName = nameEle.text();
            System.out.println("studentName ===="+studentName);//控制台看到测试结果

            Elements info = html.select("a[href^=xsgrxx.aspx?xh=]");// 获取个人信息链接

            infoUrl = info.get(0).attr("href");

            Elements score = html.select("a[href^=xscjcx.aspx?xh=]");// 获取成绩链接

            scoreUrl = score.get(0).attr("href");

            msg.what = 5;

            if (cb_isSave.isChecked()) {// 是否保存账号密码
                save(name, password);
            }
        }
        handler.sendMessage(msg);// handler更新
    }

    // 打开软件获取验证码和cookie
    private void getVertify() {
        try {
            HttpGet httpGet = new HttpGet(urlStr);//网络请求，get方法，对验证码发出请求，
            HttpClient client = new DefaultHttpClient();//httpURLConnection，，AsyncHttpClient进行网络链接
            HttpResponse httpResponse = client.execute(httpGet);//服务器响应的反馈回到客户端的信息。
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                cookie = httpResponse.getFirstHeader("set-cookie").getValue();// 得到cookie
                InputStream is = httpResponse.getEntity().getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);// 得到bitmap格式验证码
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 刷新验证码
    private void refreshVertify() {
        try {
            HttpGet httpGet = new HttpGet(urlStr);
            httpGet.setHeader("Cookie", cookie);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                InputStream is = httpResponse.getEntity().getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable vertifyRun = new Runnable() {

        @Override
        public void run() {
            getVertify();
        }
    };
    Runnable refreshRun = new Runnable() {

        @Override
        public void run() {
            refreshVertify();
        }
    };
    Runnable loginRun = new Runnable() {

        @Override
        public void run() {
            login();
        }
    };

    // 点击事件
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;

            case R.id.iv_vertify:
                new Thread(refreshRun).start();
                break;
            case R.id.tv_login_back:
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {// 双击退出软件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (flag == false) {
                flag = true;
                Toast.makeText(getApplicationContext(), "再按一次退出软件",
                        Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flag = false;
                    }
                }, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }
}
