package com.android.rssreader.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.rssreader.NewsDetailActivity;
import com.android.rssreader.model.News;
import com.android.rssreader.util.ImageUtil;
import com.intelygenz.rssreader.R;

public class RSSAdapter extends ArrayAdapter<News> implements OnItemClickListener {

	private Activity activity;

	public RSSAdapter(Activity activity, List<News> newsList) {
		super(activity, R.layout.rss_adapter, newsList);
		this.activity = activity;
		ImageUtil.initialize(activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rss_adapter, null);
		}
		News item = this.getItem(position);

		if (item != null) {

			TextView itemTitle = (TextView) convertView.findViewById(R.id.title);

			RelativeLayout imageContainer = (RelativeLayout) convertView.findViewById(R.id.imageContainer);
			ProgressBar imageProgressBar = (ProgressBar) convertView.findViewById(R.id.loader);
			ImageView itemImage = (ImageView) convertView.findViewById(R.id.image);
			TextView descrition = (TextView) convertView.findViewById(R.id.description);
			itemImage.setImageBitmap(null);
			if (item.getTitle().trim().equals("")) {
				itemTitle.setVisibility(View.GONE);
			} else {
				itemTitle.setVisibility(View.VISIBLE);
				itemTitle.setText(Html.fromHtml(item.getTitle().trim()));
			}

			imageContainer.setVisibility(View.VISIBLE);

			if (item.getImageUrl() == null || item.getImageUrl().equalsIgnoreCase("")) {
				imageContainer.setVisibility(View.GONE);
			} else {
				imageProgressBar.setVisibility(View.VISIBLE);
				String urlSplited[] = item.getImageUrl().split("\\/");
				String cacheName = urlSplited[urlSplited.length - 1];

				//Verifica si ya esta en la cache economizando el tiempo de usuario.
				if (ImageUtil.isCached(cacheName)) {
					itemImage.setImageBitmap(ImageUtil.getImageFromCache(cacheName));
					imageProgressBar.setVisibility(View.GONE);
				} else {
					//Inicio el download,porque no una async task?
					ImageUtil.start(item.getImageUrl(), itemImage, cacheName);
				}
				
			}

			descrition.setText(item.getDescription());
		}

		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		News news = this.getItem(position);
		Intent i = new Intent(this.activity, NewsDetailActivity.class);
		i.putExtra("news", news);
		this.activity.startActivity(i);
		
	}
}
