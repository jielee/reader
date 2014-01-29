package com.android.rssreader.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.android.rssreader.model.News;
/**
 * 
 * @author Jie Lee
 * Esta clase utilitaria esta realizando mucho trabajo, seria mejor otro nombre para ella o cambiar el codigo de download para otra clase. 
 */
public class ImageUtil implements Runnable {

	private String imageUrl;

	public static final int MINIMUM_WIDTH = 20;
	public static final int MINIMUM_HEIGHT = 20;
	public static final int IMAGE_WIDTH = 157;
	public static final int IMAGE_HEIGHT = 108;

	private static final int MAX_CACHE_SIZE_IN_BYTES = 3145728;

	public interface OnImageLoaderListener {
		void onImageLoaded(String imageCacheName);
	}

	private String cacheName;
	private static Context context;

	private static Map<String, ImageUtil> currentRunningImageLoaders;
	private static ThreadPoolExecutor executor;

	private static int cacheSize;

	private static OnImageLoaderListener OnImageLoaderListener;

	private ImageUtil(String imageUrl, String cacheName) {
		this.imageUrl = imageUrl;
		this.cacheName = cacheName;
	}

	// Aqui comienza la descarga si no estuviera siendo realizada.
	public static void start(String imageUrl, ImageView imageView,
			String cacheName) {
		if (ImageUtil.executor != null) {
			;
			if (ImageUtil.currentRunningImageLoaders.get(imageUrl) == null) {
				ImageUtil loader = new ImageUtil(imageUrl, cacheName);
				ImageUtil.currentRunningImageLoaders.put(imageUrl, loader);
				ImageUtil.executor.execute(loader);
			}

		}
	}
	
	// Iniciacion del  threadpollexecutor
	public static synchronized void initialize(Context context) {
		ImageUtil.context = context;
		ImageUtil.cacheSize = ImageUtil.getCacheSize();
		if (ImageUtil.executor == null) {
			ImageUtil.executor = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(7);
			ImageUtil.executor.setMaximumPoolSize(7);
			ImageUtil.currentRunningImageLoaders = new HashMap<String, ImageUtil>();
		}
	}

	@Override
	public void run() {
		this.downloadImage();
		ImageUtil.currentRunningImageLoaders.remove(this.imageUrl);
	}

	// Verifica si la imagen ya esta en la cache
	public static boolean isCached(String cacheName) {
		File file = new File(ImageUtil.context.getFilesDir() + "/cache/thumbs",
				cacheName);
		return file.exists();
	}

	// Busca la imagen de la cache para evitar el inicio de una nueva tarea de descarga en el getView del adapter. 
	public static Bitmap getImageFromCache(String cacheName) {
		String cacheFilePath = ImageUtil.context.getFilesDir()
				+ "/cache/thumbs/" + cacheName;
		@SuppressWarnings("deprecation")
		BitmapDrawable drawable = new BitmapDrawable(cacheFilePath);
		return drawable.getBitmap();
	}

	// Fue creado para verificar la conectividad y buscar las noticias del banco de datos (database (proxima Release) ;) 
	// Por tanto, puedo evitar forceclose y mostrar un mensaje amigable para el usuario. 
	public static boolean verifyConnectivity(Context context) {
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getApplicationContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();

			return networkInfo != null && networkInfo.isAvailable()
					&& networkInfo.isConnected();
		} else {
			return false;
		}
	}

	// Apenas decarga la imagen y salvala en la cache.
	protected Bitmap downloadImage() {
		try {
			if (ImageUtil.isCached(this.cacheName)) {
				return ImageUtil.getImageFromCache(this.cacheName);
			} else if (ImageUtil.verifyConnectivity(context)) {

				Bitmap bmp = doHttpGetImageBitMap(this.imageUrl);

				if (bmp != null) {

					String cacheFilePath = getCacheFileName(this.cacheName);

					Bitmap scaledBitmap = adjustBitmapSize(bmp);

					scaledBitmap = ImageUtil.cacheImage(scaledBitmap,
							cacheFilePath, this.imageUrl);

					ImageUtil.OnImageLoaderListener
							.onImageLoaded(this.cacheName);

					return scaledBitmap;

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCacheFileName(String cacheName) {
		String cacheFilePath = ImageUtil.context.getFilesDir()
				+ "/cache/thumbs/" + cacheName;
		return cacheFilePath;
	}

	// Un simple http request para obtener el input stream de la imagen.
	public static Bitmap doHttpGetImageBitMap(String imageUrl) throws IOException, ClientProtocolException {
		HttpGet httpRequest = new HttpGet(imageUrl);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream in = bufHttpEntity.getContent();
		Bitmap btmp = BitmapFactory.decodeStream(in);
		in.close();
		return btmp;
	}

	// Va a llamar la activity principal y notificar el rssadapter on UiThread.
	public static void setOnImageLoadedListener(
			OnImageLoaderListener onImageLoadedListener) {
		ImageUtil.OnImageLoaderListener = onImageLoadedListener;
	}

	
	public static int getCacheSize() {
		int size = 0;
		File cacheDirectory = new File(ImageUtil.context.getFilesDir()
				+ "/cache/thumbs");
		if (cacheDirectory.exists()) {
			File[] files = cacheDirectory.listFiles();
			if (files != null) {
				for (File file : files) {
					size += file.length();
				}
			}
		}
		return size;
	}

	//Ajusta el tamano de la imagen para caber en un pequeno container.yo no hice el layout para tablet,por falta de tiempo y de recursos.No consigo rodar el AVD propiamente.
	// En mi ordenador
	public static Bitmap adjustBitmapSize(Bitmap bmp) {
		int imageWidth = bmp.getWidth();
		int imageHeight = bmp.getHeight();

		Bitmap scaledBitmap = null;

		if ((imageWidth > IMAGE_WIDTH) || (imageHeight > IMAGE_HEIGHT)) {

			if (imageWidth > IMAGE_WIDTH) {
				imageHeight = imageHeight
						- (((imageWidth - IMAGE_WIDTH) * imageHeight) / imageWidth);
				imageWidth = IMAGE_WIDTH;
			}
			if (imageHeight > IMAGE_HEIGHT) {
				imageWidth = imageWidth
						- (((imageHeight - IMAGE_HEIGHT) * imageWidth) / imageHeight);
				imageHeight = IMAGE_HEIGHT;
			}

			scaledBitmap = Bitmap.createScaledBitmap(bmp, imageWidth,
					imageHeight, false);
			bmp.recycle();
		} else {
			scaledBitmap = bmp;
		}

		return scaledBitmap;
	}

	// Escribe la imagen.
	public static Bitmap cacheImage(Bitmap bitmap, String cacheFilePath,
			String imageUrl) {
		ImageUtil.clearCache();

		File cacheDirectory = new File(ImageUtil.context.getFilesDir()
				+ "/cache/thumbs");
		cacheDirectory.mkdirs();

		Bitmap cachedBitmap = bitmap;

		if (cachedBitmap != null) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(cacheFilePath);
				cachedBitmap.compress(CompressFormat.PNG, 100, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.flush();
						fos.close();
						File cacheFile = new File(cacheFilePath);
						if (cacheFile.exists()) {
							ImageUtil.cacheSize += cacheFile.length();
						}
					} catch (Exception e) {
						try {
							fos.close();
						} catch (IOException e1) {
							e.printStackTrace();
						}

					}
				}
			}
		}
		return cachedBitmap;
	}
	
	
	public static void sortByDate(final List<News> adapterList) {
		Collections.sort(adapterList, new Comparator<News>() {

			public int compare(final News arg0, final News arg1) {
				return arg0.getDateTime() < arg1.getDateTime() ? +1 : (arg0.getDateTime() > arg1.getDateTime() ? -1 : 0);
			}

		});

	}

	public static void clearCache() {
		File cacheDirectory = new File(ImageUtil.context.getFilesDir()
				+ "/cache/thumbs");
		if (cacheDirectory.exists()) {
			File[] files = cacheDirectory.listFiles();
			if (files != null) {
				Arrays.sort(files, new Comparator<File>() {
					@Override
					public int compare(File file1, File file2) {
						return file1.lastModified() < file2.lastModified() ? 1
								: -1;
					}
				});
				int i = 0;
				while (ImageUtil.cacheSize > ImageUtil.MAX_CACHE_SIZE_IN_BYTES) {
					ImageUtil.cacheSize -= files[i].length();
					files[i].delete();
					i++;
				}
			}
		}
	}
}
