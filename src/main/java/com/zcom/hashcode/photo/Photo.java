package com.zcom.hashcode.photo;

import java.util.Set;

public class Photo {

	public final int id;
	public final Direction direction;
	public final Set<String> tags;
	
	public Photo(int id, Direction direction, Set<String> tags) {
		this.id = id;
		this.direction = direction;
		this.tags = tags;
	}
	
	public Set<String> getTags() {
		return tags;
	}

	public int getId() {
		return id;
	}

	public Direction getDirection() {
		return direction;
	}
	
}
