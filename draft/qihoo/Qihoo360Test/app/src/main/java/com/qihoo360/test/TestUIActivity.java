package com.qihoo360.test;

import android.app.Activity;
import android.os.Bundle;

import com.qihoo360.test.view.TestView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mashuai-s on 2017/7/10.
 */

public class TestUIActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_test_layout);

        List<RecogRate> items = new ArrayList<>();
        items.add(new RecogRate(25, 25, R.color.calltag_pop_green_text_color, R.mipmap.statistics_agency));
        items.add(new RecogRate(25, 10, R.color.calltag_pop_yellow_text_color, R.mipmap.statistics_cheat));
        items.add(new RecogRate(25, 5, R.color.calltag_pop_red_text_color, R.mipmap.statistics_delivery));
        items.add(new RecogRate(25, 5, R.color.my_tag_comment_num_color, R.mipmap.statistics_dubious));
        items.add(new RecogRate(25, 5, R.color.calltag_pop_green_text_color, R.mipmap.statistics_agency));
        items.add(new RecogRate(25, 10, R.color.calltag_pop_yellow_text_color, R.mipmap.statistics_cheat));
        items.add(new RecogRate(25, 25, R.color.calltag_pop_red_text_color, R.mipmap.statistics_delivery));
        items.add(new RecogRate(25, 25, R.color.my_tag_comment_num_color, R.mipmap.statistics_dubious));
        TestView view = (TestView) findViewById(R.id.testview_id);
        view.setDataSource(items);
    }
}