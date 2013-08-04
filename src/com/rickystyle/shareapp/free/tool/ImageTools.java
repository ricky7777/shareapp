package com.rickystyle.shareapp.free.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 圖的相關工具
 * @author Ricky
 */
public class ImageTools {

    public static Bitmap DrawableToBitmap(Drawable drawable) {
	Bitmap.Config c = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

	Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), c);

	Canvas canvas = new Canvas(bitmap);
	drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	drawable.draw(canvas);
	return bitmap;
    }
}
