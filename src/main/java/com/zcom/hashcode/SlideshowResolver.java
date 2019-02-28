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
		
		final List<Slide> finalSlides = new ArrayList<>();
		finalSlides.add(currentSlide);
		for(String tag : currentSlide.getTags()) {
			final Set<Slide> slidez = slidesByTag.get(tag);
			if(slidez!=null) {
				slidez.remove(currentSlide);
			}
		}
		slides.remove(currentSlide);
		
		while(!slides.isEmpty()) {
			final Slide bestSlide = getHighestScoreSlideFromMap(slides, slidesByTag, currentSlide);
			finalSlides.add(bestSlide);
			currentSlide = bestSlide;
			for(String tag : bestSlide.getTags()) {
				final Set<Slide> slidez = slidesByTag.get(tag);
				if(slidez!=null) {
					slidez.remove(bestSlide);
				}
			}
			slides.remove(currentSlide);
		}
		
		return new Slideshow(finalSlides);
	}
	
	private Map<String, Set<Slide>> generateSlidesByTag(List<Slide> slides) {
		final Map<String, Set<Slide>> map = new HashMap<>();
		
		for(Slide slide : slides) {
			for(String tag : slide.getTags()) {
				Set<Slide> set = map.get(tag);
				if(set==null) {
					set = new HashSet<>();
					map.put(tag, set);
				}
				set.add(slide);
			}
		}
		final Set<String> tagsToRemove = new HashSet<String>();
		for(String tag : map.keySet()) {
			if(map.get(tag).size()>100) {
				tagsToRemove.add(tag);
			}
		}
		for(String s : tagsToRemove) {
			map.remove(s);
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
		final int intersection = calculateIntersection(tagsA, tagsB);
		return Math.min(
				intersection, 
				Math.min(
						tagsA.size()-intersection,
						tagsB.size()-intersection));
	}
	
	private int calculateIntersection(Set<String> a, Set<String> b) {
		int existsInBoth = 0;
		for(String aa : a) {
			if(b.contains(aa)) {
				existsInBoth++;
			}
		}
		return existsInBoth;
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
			if(currentScore>highestScore) {
				highestScore = currentScore;
				bestSlide = slide;
			}
		}
		return bestSlide;
	}
	
	private Set<Slide> generateSlidesWithVerticalPhotos(Set<Photo> photos) {
		final List<Photo> photoList = new ArrayList<>(photos);
		final Set<Slide> returnSet = new HashSet<>();
		while(photoList.size()>1) {
			final Photo a = photoList.get(0);
			photoList.remove(a);
			Photo b = getPhotoWithLeastCorrespondingTags(a, photoList);
			returnSet.add(new Slide(new HashSet<>(Arrays.asList(a, b))));
			photoList.remove(b);
		}
		return returnSet;
	}
	
	private Photo getPhotoWithLeastCorrespondingTags(Photo a, List<Photo> photoList) {
		int lowestCorrespondingNumber = Integer.MAX_VALUE;
		Photo bestPhoto = null;
		for(int i=0; i<photoList.size(); i++) {
			final Photo b = photoList.get(i);
			final Set<String> aTags = new HashSet<>(a.getTags());
			final Set<String> bTags = new HashSet<>(b.tags);
			aTags.retainAll(bTags);
			final int correspondingTagsCount = aTags.size();
			if(correspondingTagsCount==0) {
				return b;
			}
			if(correspondingTagsCount<lowestCorrespondingNumber) {
				lowestCorrespondingNumber = correspondingTagsCount;
				bestPhoto = b;
			}
		}
		return bestPhoto;
	}
	
}
