package com.tbt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by bradley on 31-08-2016.
 */
public class PenPointActivity extends AppCompatActivity {
    TextView articleTextView, articleHeader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pen_point_article_activity);
        articleTextView = (TextView) findViewById(R.id.pen_point_article_text_view);
        articleHeader = (TextView) findViewById(R.id.pen_point_article_header);
        Bundle extras = getIntent().getExtras();
        String article = extras.getString("article");
        String articleName = extras.getString("articleName");
        SpannableString string = new SpannableString(article);
        string.setSpan(new RelativeSizeSpan(4.0f), 0, article.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        articleHeader.setText(articleName);
        articleTextView.setText(string);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
