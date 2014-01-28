package com.android.rssreader.tasks;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.http.SslCertificate.DName;
import android.os.AsyncTask;

import com.android.rssreader.DisplayRssNewsActivity;
import com.android.rssreader.controller.RSSController;
import com.android.rssreader.model.News;
import com.intelygenz.rssreader.R;

public class LoadRssTask extends AsyncTask<Void, Integer, List<News>> {

	private ProgressDialog progressDialog;
	private DisplayRssNewsActivity activity;
	private String url;
	
	
	public LoadRssTask(DisplayRssNewsActivity activity, String url) {
		this.activity = activity;
		this.url = url;
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.progressDialog = ProgressDialog.show(this.activity, null, activity.getString(R.string.cargando));
	}
	
	
	@Override
	protected List<News> doInBackground(Void... params) {
		return RSSController.loadRss(this.url, "UTF-8");
	}
	
	@Override
	protected void onPostExecute(List<News> result) {
		super.onPostExecute(result);
		this.progressDialog.dismiss();
		this.activity.loadNews(result);
	}

}
