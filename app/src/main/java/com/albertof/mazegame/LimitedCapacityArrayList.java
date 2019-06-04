package com.albertof.mazegame;

import java.util.*;
public class LimitedCapacityArrayList<T> extends ArrayList<T> {
	private int maxCapacity;
	public LimitedCapacityArrayList(int maxC) {
		super();
		this.maxCapacity = maxC;
	}
	  @Override
	  public boolean add(T e) {
	      if (this.size() == maxCapacity) {
	          super.remove(0);
	      }
	      return super.add(e);
	  }
	}