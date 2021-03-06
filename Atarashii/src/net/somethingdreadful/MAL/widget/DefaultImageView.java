/*
 * 	SickStache is a android application for managing SickBeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	SickStache is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.somethingdreadful.MAL.widget;

import java.net.URI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import net.somethingdreadful.MAL.R;
import net.somethingdreadful.MAL.helper.ImageCache;
import net.somethingdreadful.MAL.task.FetchImageTask;

public class DefaultImageView extends ImageView {

	private static final String logName = "DefaultImageView";
	
	private static Bitmap defaultBitmap;
	
	public int defaultResource;
	
	protected FetchImageTask task;
	
	public DefaultImageView(Context context) {
		super(context);
	}
	
	public DefaultImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DefaultImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable pic = this.getDrawable();
		if ( pic != null ) {
			int height = pic.getIntrinsicHeight();
			int width = pic.getIntrinsicWidth();
			double aspect = (double)(width) / height;
			
			heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)(Math.ceil( (double)(View.MeasureSpec.getSize(widthMeasureSpec)) / aspect )), View.MeasureSpec.EXACTLY);
			
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setImage(URI url)
	{
		// we are asking for a new uri so we do not want the current
		if ( task != null ) {
			task.cancel(true);
		}
		Bitmap bitmap = ImageCache.getSingleton(getContext()).getFromMemory(url.toString());
		if ( bitmap != null ) {
			// I dupe this statement mainly because i don't want to start a task before i set the bitmap
			// granted the task currently doens't actually run before i would assign the bitmap
			// but i don't want to assume it will
			this.setImageBitmap(bitmap);
		} else {
			//if ( defaultBitmap == null )
			//	defaultBitmap = BitmapFactory.decodeResource(this.getResources(), defaultResource);
			this.setImageBitmap(defaultBitmap);
			task = new FetchImageTask( ImageCache.getSingleton(getContext()), url, this.getWidth(), this.getHeight() ){
				@Override
				protected void onPostExecute(Bitmap result) {
					super.onPostExecute(result);
					if ( result != null ) {
						Drawable last = DefaultImageView.this.getDrawable();
						DefaultImageView.this.setImageBitmap(result);
						if ( !(last instanceof BitmapDrawable) || ((BitmapDrawable)last).getBitmap() == null ) {
							DefaultImageView.this.setAnimation(AnimationUtils.loadAnimation(getContext(), R.animator.image_fade_in));
						}
					}
				}};
				task.execute();
		}
	}
}
