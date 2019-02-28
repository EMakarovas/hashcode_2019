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
	
	final Set<Integer> usedIndexes = new HashSet<Integer>();

	public Slideshow resolveSlideshow(Set<Photo> photos) {
		final List<Slide> slides = generateSlides(photos);
		final int[][] matrix = generateScoreMatrix(slides);
		int currentIndex = getInitialSlideIndex(slides, matrix);
		
		final List<Slide> finalSlides = new ArrayList<Slide>();
		finalSlides.add(slides.get(currentIndex));
		usedIndexes.add(currentIndex);
		
		while(usedIndexes.size()!=slides.size()) {
			final int bestIndex = getHighestScoreIndex(matrix[currentIndex]);
			if(bestIndex==FAKE_INDEX) {
				break;
			}
			finalSlides.add(slides.get(bestIndex));
			usedIndexes.add(bestIndex);
			currentIndex = bestIndex;
		}
		
		return new Slideshow(finalSlides);
	}
	
	private int[][] generateScoreMatrix(List<Slide> slides) {
		final int[][] matrix = new int[slides.size()][slides.size()];
		
		for(int i=0; i<slides.size(); i++) {
			for(int j=0; j<slides.size(); j++) {
				final int score = calculateScore(slides.get(i), slides.get(j));
				matrix[i][j] = score;
			}
		}
		return matrix;
	}
	
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
	
	private int getInitialSlideIndex(List<Slide> slides, int[][] matrix) {
		return 0;
	}
	
	private int getHighestScoreIndex(int[] scores) {
		int highestIndex = FAKE_INDEX;
		int highestScore = -1;
		for(int i=0; i<scores.length; i++) {
			final int currentScore = scores[i];
			if(currentScore>highestScore && !usedIndexes.contains(i)) {
				highestScore = currentScore;
				highestIndex = i;
			}
		}
		return highestIndex;
	}
	
}
