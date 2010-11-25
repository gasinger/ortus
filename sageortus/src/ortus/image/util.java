/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jphipps
 */
public class util extends ortus.vars {

	public static HashMap<String,String> GetImageInfo(String imagename) {
//		ortus.api.DebugLog(LogLevel.Trace, "GetImageInfo: for file: " + imagename);
		File imgfile = new File(imagename);
		if (!imgfile.exists()) {
			ortus.api.DebugLog(LogLevel.Error,"GetImageInfo: file not found");
			return null;
		}
		Image img;
		HashMap<String,String> imginfo = new HashMap<String,String>();

		try {
			img = ImageIO.read(imgfile);
		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error,"GetImageSize: Exception: ",ex);
			return null;
		}
//		ortus.api.DebugLog(LogLevel.Trace2,"completed ImageIO");
		try {
			imginfo.put("width",String.valueOf(img.getWidth(null)));
			imginfo.put("height",String.valueOf(img.getHeight(null)));
		} catch ( Exception e) {
			ortus.api.DebugLog(LogLevel.Error,"GetImageInfo: height/width string conversion filaed",e);
			return null;
		}
//		ortus.api.DebugLog(LogLevel.Trace2,"returning from imageinfo");
		return imginfo;
	}

	public static InputStream scaleImage(String imgname, String scaledimage, int p_width, int p_height) throws Exception {

		File imgfile = new File(imgname);
		if (!imgfile.exists()) {
			return null;
		}
		Image image = (Image) ImageIO.read(imgfile);

		int thumbWidth = p_width;
		int thumbHeight = p_height;

		// Make sure the aspect ratio is maintained, so the image is not skewed
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}

		// Draw the scaled image
		BufferedImage thumbImage = new BufferedImage(thumbWidth,
			thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		// Write the scaled image to the outputstream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
		int quality = 100; // Use between 1 and 100, with 100 being highest quality
		quality = Math.max(0, Math.min(quality, 100));
		param.setQuality((float) quality / 100.0f, false);
		encoder.setJPEGEncodeParam(param);
		encoder.encode(thumbImage);
		ImageIO.write(thumbImage, "jpeg", out);

		// Read the outputstream into the inputstream for the return value
		ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

		return bis;
	}

        public static void scale(String src, int width, int height, String dest) {
boolean preserveAlpha=src.contains(".png");
 int imageType = !preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage originalimage;
        try {
            originalimage = ImageIO.read(new File(src));

    BufferedImage scaledBI = new BufferedImage(width, height, imageType);
    Graphics2D g = scaledBI.createGraphics();
    if (preserveAlpha) {
       g.setComposite(AlphaComposite.Src);}


    g.drawImage(originalimage, 0, 0,width, height, null);
    g.dispose();

            ImageIO.write(scaledBI, "png", new File(dest));
        } catch (IOException ex) {
             ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Scaling Poster error: " + ex.getMessage());
           
        }



}
}
