package com.zcom.hashcode.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.zcom.hashcode.photo.Direction;
import com.zcom.hashcode.photo.Photo;

public class FileReader {

	public Set<Photo> parseInputFile(File f) {
		try {
			final Set<Photo> returnSet = new HashSet<Photo>();
			final Scanner sc = new Scanner(f);
			final int numberOfPhotos = sc.nextInt();
			for(int id=0; id<numberOfPhotos; id++) {
				final Direction direction = Direction.getByLetter(sc.next());
				final int numberOfTags = sc.nextInt();
				final Set<String> tags = new HashSet<String>(2);
				for(int j=0; j<numberOfTags; j++) {
					tags.add(sc.next());
				}
				returnSet.add(new Photo(id, direction, tags));
			}
			sc.close();
			return returnSet;
		} catch (FileNotFoundException e) {
			// ignore
			throw new RuntimeException(e);
		}
	}
	
}
