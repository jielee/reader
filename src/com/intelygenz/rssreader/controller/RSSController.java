package com.intelygenz.rssreader.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.intelygenz.rssreader.model.News;
/**
 * 
 * @author Jie Lee
 * Responsable por descargar y hacer el parse del rss
 * No ejecute findBugs, e.printStackTrace no es una buena opcion.No tuve tiempo para tratar las exepciones propiamente.
 * To the next release (Pagination)
 */
public class RSSController extends DefaultHandler {

	private static String info = "http://ep00.epimg.net/rss/elpais/portada.xml";
	private StringBuilder chars;
	private boolean inItem = false;
	private int ignoredTag = 0;
	private List<News> listNews = new ArrayList<News>();
	private News currentNews;

	public static List<News> loadRss(final String url, final String encode) {

		RSSController rss = new RSSController();

		try {
			InputStream input = new URL(info).openConnection().getInputStream();
			String response = convertStreamToString(input, encode);
			InputSource is = null;

			is = new InputSource(new ByteArrayInputStream(response.getBytes()));

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			XMLReader xmlreader;
			parser = factory.newSAXParser();
			xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler(rss);
			xmlreader.parse(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return rss.listNews;
	}

	private static String convertStreamToString(InputStream is, String encode) {

		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(is, encode), 8);
			String line = null;

			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return sb.toString();
	}

	@Override
	public void startElement(String uri, String qname, String localName, Attributes attributes) throws SAXException {
		this.chars = new StringBuilder();

		if ("item".equalsIgnoreCase(localName)) {
			inItem = true;
			this.currentNews = new News();

		} else if ("enclosure".equalsIgnoreCase(localName)) {
			this.currentNews.setImageUrl(attributes.getValue("url"));
		} else {
			this.ignoredTag++;
		}
	}

	@Override
	public void endElement(String uri, String qName, String localName) throws SAXException {
		if (this.ignoredTag > 0) {
			if ("title".equalsIgnoreCase(localName)) {
				if (this.inItem) {
					this.currentNews.setTitle(this.chars.toString());
				}

			} else if ("link".equalsIgnoreCase(localName)) {
				if (this.inItem) {

					this.currentNews.setNewsUrl(this.chars.toString());

				}
			} else if ("description".equalsIgnoreCase(localName)) {
				if (this.inItem) {
					String itemBody = this.chars.toString();
					itemBody = itemBody.replaceAll("\\<img.[^>]*?>", " ");
					itemBody = (itemBody).toString();
					this.currentNews.setDescription(itemBody);

				}

			} else if ("content:encoded".equalsIgnoreCase(localName)) {
				if (this.inItem) {
					this.currentNews.setContent(this.chars.toString());
				}

			} else if ("pubDate".equalsIgnoreCase(localName)) {
				if (this.inItem) {
					SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
					Date date = new Date();
					try {
						date = format.parse(this.chars.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					this.currentNews.setDateTime(date.getTime());
				}

			} else if ("item".equalsIgnoreCase(localName)) {

				if (currentNews.getTitle() != null && currentNews.getDescription() != null && !currentNews.getTitle().trim().isEmpty()
						&& !currentNews.getDescription().trim().isEmpty()) {
					listNews.add(currentNews);
				}
				this.inItem = false;
				this.currentNews = null;
			}

		} else {
			this.ignoredTag--;
		}

		this.chars = null;
	}

	@Override
	public void characters(final char ch[], final int start, final int length) {
		if (this.chars != null) {
			this.chars.append(ch, start, length);
		}
	}
}
