package cn.mmvtc.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.zzhoujay.richtext.RichText;

public class TextActivity extends Activity {
    private TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        tv = findViewById(R.id.tv);
        RichText.fromHtml("sadasda").into(tv);

    }
}
