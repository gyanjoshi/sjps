package com.example.projectx.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {
	
	public static BufferedImage resizeImage(File imageFile, int targetWidth, int targetHeight) throws IOException {
		BufferedImage originalImage = getBufferedImage(imageFile);
		
	    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	    graphics2D.dispose();
	    return resizedImage;
	}
	
	public static BufferedImage getBufferedImage(File imageFile) throws IOException {
        BufferedImage sourceImage = ImageIO.read(imageFile);
		return sourceImage;

        
    }
	
	public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height){
	    BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
	    return croppedImage;
	}
	
	public static void main(String[] args) {
		
		String absoultePath = "D:\\FileStore\\Profile\\pragyani_Profile.jpg" ;
		
		File f = new File(absoultePath);
		
		File outFile = new File("D:\\\\FileStore\\\\Profile\\\\pragyani_Profile_cropped.jpg");
		
		BufferedImage bimg, cropped;
		
 
        if (f.exists()) {
        	try {
				bimg = getBufferedImage(f);
				cropped = cropImage(bimg, 0,0,50,50);
				
				ImageIO.write(cropped, "jpg", outFile);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}
