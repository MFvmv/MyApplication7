package cn.mmvtc.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

import java.io.File;

public class ContentActivity extends Activity implements View.OnClickListener {
    private TextView tv_content_back;
    private TextView tv_html;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        tv_html = findViewById(R.id.tv_html);
        tv_content_back = findViewById(R.id.tv_content_back);
        tv_content_back.setOnClickListener(this);
        Intent intent  = getIntent();
        Bundle bundle = intent.getExtras();
        //tv_html.setText(bundle.getString("html"));
        RichText.fromHtml(bundle.getString("html")+" ").into(tv_html);


    }




    @Override
    protected void onDestroy() {
        RichText.clear(this);
        RichText.recycle();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_content_back:
                finish();
                break;
        }
    }
}
