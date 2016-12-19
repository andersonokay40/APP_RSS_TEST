package br.com.apprsstest;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class MainShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main_show);

        WebView w = (WebView) findViewById(R.id.webview);
        String content = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /><head><body>";
        content += globalvariaveis.mylist.get(globalvariaveis.selecionado) + "</body></html>";
        content = Html.fromHtml(content).toString();
        w.loadData(content, "text/html", "UTF-8");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
