package net.somethingdreadful.MAL.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import net.somethingdreadful.MAL.helper.ImageCache;

import java.net.URI;

public class FetchImageTask extends SickTask<Void,Void,Bitmap> {

	protected URI url;
	protected int width;
	protected int height;
	
	private ImageCache cache;
	
	public FetchImageTask( ImageCache cache, URI url, int width, int height )
	{
		super();
		this.cache = cache;
		this.url = url;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public String getTaskLogName() {
		return "FetchBannerTask";
	}
	
	@Override
	protected Bitmap doInBackground(Void... arg0) {
		try {
			// Hopefully these URI's are all canonical or constent
			String key = url.toString();
			
			Bitmap bitmap = cache.getFromMemory(key);
			// if the bitmap was not in the memory cache
			if ( bitmap == null ) {
				// check if it is on the disk
				if ( cache.inDisk(key) == true ) {
					bitmap = cache.getFromDisk(key);
				}
				// if it wasn't on the disk then finally go get to url
				if ( bitmap == null ) {
					bitmap = BitmapFactory.decodeStream(url.toURL().openStream());
					if ( bitmap != null )
						cache.put(key, bitmap);
				}
			}
			// if we have a bitmap scale it
//			if ( bitmap != null && width > 0 && height > 0 ) {
//				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//			}
			return bitmap;
		} catch (Exception e) {
			error=e;
			return null;
		}
	}
	
}
