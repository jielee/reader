package com.android.rssreader.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.rssreader.util.ImageUtil;

/**
 * 
 * @author Jie Lee
 * 
 * Esta tarea fue creada para descargar la imagen en la NewsDetailActivity para exhibir una imagen mayor que la del adapters. 
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	
	private String url;
	private final WeakReference<ImageView> reference;
	private final WeakReference<ProgressBar> progressReference;
	
	
	public DownloadImageTask(String url, ImageView ref, ProgressBar progressRef) {
		this.reference = new WeakReference<ImageView>(ref);
		this.progressReference = new WeakReference<ProgressBar>(progressRef);
		this.url = url;

		
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			return ImageUtil.doHttpGetImageBitMap(this.url);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
        if (reference != null) {
            ImageView imageView = reference.get();
            ProgressBar progress = progressReference.get();
            if (imageView != null) {
            	progress.setVisibility(View.GONE);
                imageView.setImageBitmap(result);
            }
        }
	}

}
