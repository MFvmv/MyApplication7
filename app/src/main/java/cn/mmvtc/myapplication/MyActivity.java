package cn.mmvtc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends FragmentActivity {
    private ViewPager view_pager;
    private TextView tv_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        view_pager = findViewById(R.id.view_pager);
        List<Fragment> fragments = new ArrayList<Fragment>();//设置fragment
        Intent intent = getIntent();
        switch (intent.getExtras().getInt("select")){
            case 1:
                fragments.add(new MeFragment());
                break;
            case 2:
                fragments.add(new ScoreFragment());
                break;
            case 3:
                fragments.add(new AboutMyFragment());
                break;
        }
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        //view_pager.setOffscreenPageLimit(1);
        view_pager.setAdapter(adapter);
        view_pager.setCurrentItem(0);
        tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
