package com.zcom.hashcode.photo;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Slide {

	public final Set<Photo> photos;
	
	public Slide(Set<Photo> photos) {
		this.photos = photos;
	}
	
	public Slide(Photo photo) {
		this.photos = Collections.singleton(photo);
	}

	public Set<String> getTags() {
		return photos.stream()
				.map(Photo::getTags)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}
	
}
