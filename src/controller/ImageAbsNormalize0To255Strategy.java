/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package controller;

import model.*;

/**
 * <p>Title: ImageAbsNormalizeTo255Strategy</p>
 * <p>Description: Image-related strategy</p>
 * <p>Copyright: Copyright (c) 2004 Colin Barré-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageAbsNormalize0To255Strategy extends ImageConversionStrategy {
	
	private double minR = 0;
	private double minG = 0;
	private double minB = 0;
	private double minA = 0;
	
	private double maxR = 0;
	private double maxG = 0;
	private double maxB = 0;
	private double maxA = 0;
	
	private double newMin = 0;
	private double newMax = 255;
	
 	/**
	 * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
	 */
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		findMinMaxValue(image);
		
		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				newImage.setPixel(x, y, new Pixel((int)(normalizeTo255(curPixelDouble.getRed(), minR, maxR)),
												  (int)(normalizeTo255(curPixelDouble.getGreen(), minG, maxG)),
												  (int)(normalizeTo255(curPixelDouble.getBlue(), minB, maxB)),
												  (int)(normalizeTo255(curPixelDouble.getAlpha(), minA, maxA))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
	}
	
	/**
	 * Trouve la valeur minimum et maximum des composante R, G, B et A
	 * @param image
	 */
	private void findMinMaxValue(ImageDouble image){
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		PixelDouble curPixelDouble = null;
		
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				if (Math.abs(curPixelDouble.getRed()) < minR){
					minR = Math.abs(curPixelDouble.getRed());
				} else if (Math.abs(curPixelDouble.getGreen()) < minG){
					minG = Math.abs(curPixelDouble.getGreen());
				} else if (Math.abs(curPixelDouble.getBlue()) < minB){
					minB = Math.abs(curPixelDouble.getBlue());
				} else if (Math.abs(curPixelDouble.getAlpha()) < minA){
					minA = Math.abs(curPixelDouble.getAlpha());
				}
				
				if (Math.abs(curPixelDouble.getRed()) > maxR){
					maxR = Math.abs(curPixelDouble.getRed());
				} else if (Math.abs(curPixelDouble.getGreen()) > maxG){
					maxG = Math.abs(curPixelDouble.getGreen());
				} else if (Math.abs(curPixelDouble.getBlue()) > maxB){
					maxB = Math.abs(curPixelDouble.getBlue());
				} else if (Math.abs(curPixelDouble.getAlpha()) > maxA){
					maxA = Math.abs(curPixelDouble.getAlpha());
				}
			}
		}
	}
	

	// Cette methode est inspire a partir du site web suivant :
	// http://stackoverflow.com/questions/695084/how-do-i-normalize-an-image
	private double normalizeTo255(double value, double min, double max) {
		double newRange = newMax - newMin;
		
		double scale = (value - min) / (max - min);
		double newValue = (newRange * scale) + newMin; 
				
		return newValue;
	}
}