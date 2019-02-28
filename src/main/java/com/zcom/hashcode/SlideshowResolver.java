package com.zcom.hashcode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.zcom.hashcode.photo.Direction;
import com.zcom.hashcode.photo.Photo;
import com.zcom.hashcode.photo.Slide;
import com.zcom.hashcode.photo.Slideshow;

public class SlideshowResolver {
	
	private static final int FAKE_INDEX = -1;
	
	public Slideshow resolveSlideshow(Set<Photo> photos) {
		final List<Slide> slides = generateSlides(photos);
		Slide currentSlide = getInitialSlide(slides);
		
		final List<Slide> finalSlides = new ArrayList<Slide>();
		finalSlides.add(currentSlide);
		slides.remove(currentSlide);
		
		while(!slides.isEmpty()) {
			final Slide bestSlide = getHighestScoreSlide(slides, currentSlide);
			finalSlides.add(bestSlide);
			currentSlide = bestSlide;
			slides.remove(currentSlide);
		}
		
		return new Slideshow(finalSlides);
	}
//	
//	private short[][] generateScoreMatrix(List<Slide> slides) {
//		final short[][] matrix = new short[slides.size()][slides.size()];
//		
//		for(int i=0; i<slides.size(); i++) {
//			for(int j=0; j<slides.size(); j++) {
//				final int score = calculateScore(slides.get(i), slides.get(j));
//				matrix[i][j] = (short) score;
//			}
//		}
//		return matrix;
//	}
	
	private List<Slide> generateSlides(Set<Photo> photos) {
		final Map<Direction, Set<Photo>> photosByDirection = photos.stream()
				.collect(Collectors.groupingBy(Photo::getDirection, Collectors.toSet()));
		
		
		
		
		return photosByDirection.entrySet().stream()
				.map(entry -> {
					final Direction direction = entry.getKey();
					switch(direction) {
					case HORIZONTAL:
						return entry.getValue().stream()
								.map(Slide::new)
								.collect(Collectors.toSet());
					case VERTICAL:
						return new HashSet<Slide>(0);
								
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
	
	private Slide getHighestScoreSlide(List<Slide> slides, Slide currentSlide) {
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
	
}
