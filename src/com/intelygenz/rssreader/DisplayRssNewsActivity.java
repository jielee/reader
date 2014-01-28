package com.intelygenz.rssreader;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.intelygenz.rssreader.model.News;
import com.intelygenz.rssreader.tasks.LoadRssTask;
import com.intelygenz.rssreader.ui.RSSAdapter;
import com.intelygenz.rssreader.util.ImageUtil;
import com.intelygenz.rssreader.util.ImageUtil.OnImageLoaderListener;

public class DisplayRssNewsActivity extends Activity implements
		OnImageLoaderListener {

	private ListView listReader;
	private RSSAdapter rssAdapter;
	private TextView errorText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_rss_news);
		errorText = (TextView)findViewById(R.id.reader_error);
		ImageUtil.setOnImageLoadedListener(this);
		listReader = (ListView) findViewById(R.id.reader_list_news);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_rss_news, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ImageUtil.verifyConnectivity(this)) {
			new LoadRssTask(this,"http://ep00.epimg.net/rss/elpais/portada.xml").execute();
			errorText.setVisibility(View.GONE);
		}else{
			errorText.setVisibility(View.VISIBLE);
			
		}
	}
	//called by async task
	public void loadNews(List<News> result) {
		ImageUtil.sortByDate(result);
		rssAdapter = new RSSAdapter(this, result);
		listReader.setAdapter(rssAdapter);
		listReader.setOnItemClickListener(rssAdapter);
	}

	@Override
	public void onImageLoaded(String imageCacheName) {
		if (this.rssAdapter != null) {
			this.runOnUiThread((new Runnable() {

				@Override
				public void run() {
					rssAdapter.notifyDataSetChanged();
				}
			}));
		}
	}
}
