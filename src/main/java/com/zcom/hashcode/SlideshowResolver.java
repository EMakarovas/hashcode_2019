package com.zcom.hashcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.zcom.hashcode.photo.Direction;
import com.zcom.hashcode.photo.Photo;
import com.zcom.hashcode.photo.Slide;
import com.zcom.hashcode.photo.Slideshow;

public class SlideshowResolver {
		
	public Slideshow resolveSlideshow(Set<Photo> photos) {
		final List<Slide> slides = generateSlides(photos);
		final Map<String, Set<Slide>> slidesByTag = generateSlidesByTag(slides);
		
		Slide currentSlide = getInitialSlide(slides);
		
		final List<Slide> finalSlides = new ArrayList<Slide>();
		finalSlides.add(currentSlide);
		for(String tag : currentSlide.getTags()) {
			slidesByTag.get(tag).remove(currentSlide);
		}
		slides.remove(currentSlide);
		
		while(!slides.isEmpty()) {
			final Slide bestSlide = getHighestScoreSlideFromMap(slides, slidesByTag, currentSlide);
			finalSlides.add(bestSlide);
			currentSlide = bestSlide;
			for(String tag : bestSlide.getTags()) {
				slidesByTag.get(tag).remove(bestSlide);
			}
			slides.remove(currentSlide);
		}
		
		return new Slideshow(finalSlides);
	}
	
	private Map<String, Set<Slide>> generateSlidesByTag(List<Slide> slides) {
		final Map<String, Set<Slide>> map = new HashMap<String, Set<Slide>>();
		
		for(Slide slide : slides) {
			for(String tag : slide.getTags()) {
				Set<Slide> set = map.get(tag);
				if(set==null) {
					set = new HashSet<Slide>();
					map.put(tag, set);
				}
				set.add(slide);
			}
		}
		return map;
	}
	
	private List<Slide> generateSlides(Set<Photo> photos) {
		final Map<Direction, Set<Photo>> photosByDirection = photos.stream()
				.collect(Collectors.groupingBy(Photo::getDirection, Collectors.toSet()));

		return photosByDirection.entrySet().stream()
				.map(entry -> {
					final Direction direction = entry.getKey();
					final Set<Photo> photosWithDirection = entry.getValue();
					switch(direction) {
					case HORIZONTAL:
						return photosWithDirection.stream()
								.map(Slide::new)
								.collect(Collectors.toSet());
					case VERTICAL:
						return generateSlidesWithVerticalPhotos(photosWithDirection);
					default:
						throw new RuntimeException();
					}
				})
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
				
	}
	
	private int calculateScore(Slide a, Slide b) {
		final Set<String> tagsA = a.getTags();
		final Set<String> tagsB = b.getTags();
		final Set<String> commonSet = new HashSet<String>(tagsA);
		commonSet.retainAll(tagsB);
		final Set<String> aDifference = new HashSet<String>(tagsA);
		aDifference.removeAll(tagsB);
		tagsB.removeAll(tagsA);
		return Math.min(commonSet.size(), Math.min(aDifference.size(), tagsB.size()));
	}
	
	private Slide getInitialSlide(List<Slide> slides) {
		return slides.get(0);
	}
	
	private Slide getHighestScoreSlideFromMap(Collection<Slide> allSlides, Map<String, Set<Slide>> slidesByTag, Slide currentSlide) {
		final Set<Slide> allSlidesToCheck = currentSlide.getTags().stream()
				.map(tag -> Optional.ofNullable(slidesByTag.get(tag)).orElse(Collections.emptySet()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
		final Slide highestScoreSlide = getHighestScoreSlide(allSlidesToCheck, currentSlide);
		if(highestScoreSlide==null) {
			return allSlides.iterator().next();
		} else {
			return highestScoreSlide;
		}
	}
		
	private Slide getHighestScoreSlide(Collection<Slide> slides, Slide currentSlide) {
		Slide bestSlide = null;
		int highestScore = -1;
	
		for(Slide slide : slides) {
			final int currentScore = calculateScore(slide, currentSlide);
			if(currentScore>1) {
				return slide;
			}
			if(currentScore>highestScore) {
				highestScore = currentScore;
				bestSlide = slide;
			}
		}
		return bestSlide;
	}
	
	private Set<Slide> generateSlidesWithVerticalPhotos(Set<Photo> photos) {
		final List<Photo> photoList = new ArrayList<Photo>(photos);
		final Set<Slide> returnSet = new HashSet<Slide>();
		while(photoList.size()>1) {
			final Photo a = photoList.get(0);
			final Photo b = photoList.get(1);	
			returnSet.add(new Slide(new HashSet<Photo>(Arrays.asList(a, b))));
			photoList.remove(a);
			photoList.remove(b);
		}
		return returnSet;
	}
	
}
