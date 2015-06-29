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
 * <p>Copyright: Copyright (c) 2004 Colin Barr�-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageAbsNormalizeTo255Strategy extends ImageConversionStrategy {
	
 	/**
	 * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
	 */
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		double oldR = 0.0;
		double oldG = 0.0;
		double oldB = 0.0;
		double oldA = 0.0;
		
		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				oldR = curPixelDouble.getRed();
				oldG = curPixelDouble.getGreen();
				oldB = curPixelDouble.getBlue();
				oldA = curPixelDouble.getAlpha();				
				
				newImage.setPixel(x, y, new Pixel((int)(normalizeTo255(oldR, oldR, oldG, oldB)),
												  (int)(normalizeTo255(oldG, oldR, oldG, oldB)),
												  (int)(normalizeTo255(oldB, oldR, oldG, oldB)),
												  (int)(normalizeTo255(oldA, oldA, oldA, oldA))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
	}
	
	/**
	 * Normaliser les valeurs a 255
	 */
	// Cette methode est inspire a partir du site web suivant :
	// http://www.roborealm.com/help/Normalize%20Color.php
	private double normalizeTo255(double value, double oldR, double oldG, double oldB) {

		double newValue = Math.abs(value) / (Math.abs(oldR) + Math.abs(oldG) + Math.abs(oldB)); 
				
		return newValue * 255.0;
	}
}