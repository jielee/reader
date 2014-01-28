package com.android.rssreader.database;

import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.android.rssreader.model.News;


public class DBNews extends DBTable<News> {
	
	private static final String TITLE = "Title";
	private static final String DESCRIPTION = "Description";
	private static final String IMAGE_URL = "ImageURL";
	private static final String DATE = "Date";
	private static final String LINK_URL = "linkURL";

	public DBNews(Context context) {
		super(context);
	}

	@Override
	public void save(News news) {
		String title = news.getTitle();
		String linkUrl = news.getNewsUrl();
		String description = news.getDescription();
		String imageUrl = news.getImageUrl();
		Long dateTime = news.getDateTime();
		insert(news, "INSERT INTO News(Title, Description, ImageURL, Date, linkURL) VALUES(?, ?, ?, ?, ?);", title, description, imageUrl, dateTime, linkUrl);

	}
	
	@Override
	public List<News> getAll() {
		return getList("SELECT * FROM News");
	}

	@Override
	public News getFromCursor(Cursor cursor) {
		News news = new News();
		news.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
		news.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
		news.setImageUrl(cursor.getString(cursor.getColumnIndex(IMAGE_URL)));
		news.setDateTime(cursor.getLong(cursor.getColumnIndex(DATE)));
		news.setNewsUrl(cursor.getString(cursor.getColumnIndex(LINK_URL)));

		return news;
	}


}