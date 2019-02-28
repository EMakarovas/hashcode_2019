package com.zcom.hashcode.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.zcom.hashcode.photo.Photo;
import com.zcom.hashcode.photo.Slide;
import com.zcom.hashcode.photo.Slideshow;

public class HashCodeFileWriter {

	public void writeToOutputFile(String filePath, Slideshow slideshow) {
		try(final FileWriter fw = new FileWriter(filePath)) {
			final List<Slide> slides = slideshow.slides;
			fw.write(String.valueOf(slides.size()));
			slides.stream().forEach(slide -> {
				final String idString = slide.photos.stream()
						.map(Photo::getId)
						.map(String::valueOf)
						.collect(Collectors.joining(" "));
				try {
					fw.write("\n");
					fw.write(idString);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
