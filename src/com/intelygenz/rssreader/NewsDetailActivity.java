package com.intelygenz.rssreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.intelygenz.rssreader.model.News;
import com.intelygenz.rssreader.tasks.DownloadImageTask;
import com.intelygenz.rssreader.util.ImageUtil;

public class NewsDetailActivity extends Activity {

	private TextView title;
	private TextView description;
	private ImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		title = (TextView)findViewById(R.id.title);
		description =(TextView)findViewById(R.id.description);
		description.setEllipsize(TruncateAt.END);
		image = (ImageView) findViewById(R.id.image);
		image.setVisibility(View.VISIBLE);
		TextView link = (TextView)findViewById(R.id.linkButton);
		ProgressBar imageProgressBar = (ProgressBar) findViewById(R.id.loader);
		
		
		Intent i = getIntent();
		
		
		if(i != null){
			News news = (News)i.getSerializableExtra("news");
			title.setText(news.getTitle());
			if(news.getImageUrl()==null || news.getImageUrl().isEmpty()){
				findViewById(R.id.imageContainer).setVisibility(View.GONE);
			}else{
				String urlSplited[] = news.getImageUrl().split("\\/");
				String cacheName = urlSplited[urlSplited.length - 1];
				new DownloadImageTask(news.getImageUrl(), image, imageProgressBar).execute();
			}
			description.setText(Html.fromHtml(news.getContent()));
			link.setVisibility(View.VISIBLE);
			link.setOnTouchListener(new LinkButtonTouchListener(news.getNewsUrl()));
			
		}
	}
	
	
	private final class LinkButtonTouchListener implements View.OnTouchListener {
		private final String item;

		private LinkButtonTouchListener(String url) {
			this.item = url;
		}

		@SuppressLint("ShowToast")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (ImageUtil.verifyConnectivity(getApplicationContext())) {
					try {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(this.item.trim()));
						startActivity(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(), "ERRO", 500);
				}
			}
			return true;
		}
	}


}
