package com.zcom.hashcode;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import com.zcom.hashcode.files.FileReader;
import com.zcom.hashcode.files.HashCodeFileWriter;
import com.zcom.hashcode.photo.Photo;
import com.zcom.hashcode.photo.Slideshow;

public class Main {

	public static void main(String[] args) throws URISyntaxException {
		final String inputResourceName = args[0];
		final String outputFileName = args[1];
		final Set<Photo> photos = new FileReader().parseInputFile(new File(Main.class.getResource(inputResourceName).toURI()));
		
		final Slideshow slideshow = new SlideshowResolver().resolveSlideshow(photos);
		
		new HashCodeFileWriter().writeToOutputFile(outputFileName, slideshow);
	}
	
}
