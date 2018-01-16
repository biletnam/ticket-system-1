package com.util;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import com.service.impl.BestSeatFindingServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

public class BestSeatFindingServiceTest {

	@InjectMocks
	BestSeatFindingServiceImpl impl;
	
	@Before
	public void setup(){
		initMocks(this);
	}
	
	@Test
	public void testFindBestSeatsDecremental(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(100);
		list.add(98);
		list.add(95);
		List<Integer> respList = impl.findBestSeatsDecremental(list, 3, 100);
		Assert.assertTrue(respList.size() == 3);
		Assert.assertTrue(respList.contains(94) && respList.contains(93) && respList.contains(92));
	}
	
	@Test
	public void testFindBestSeatsIncrementalCaseTwo(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(10);
		list.add(9);
		list.add(8);
		list.add(4);
		list.add(3);
		List<Integer> respList = impl.findBestSeatsIncremental(list, 5, 10);
		Assert.assertTrue(respList.size() == 5);
		Assert.assertTrue(respList.contains(7) && respList.contains(2) && respList.contains(5));
	}
	
	@Test
	public void testFindBestSeatsIncremental(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(4);
		List<Integer> respList = impl.findBestSeatsIncremental(list, 3, 100);
		Assert.assertTrue(respList.size() == 3);
		Assert.assertTrue(respList.contains(7) && respList.contains(5) && respList.contains(6));
	}
	
	@Test
	public void testFindBestSeatsDecrementalCaseTwo(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(4);
		list.add(7);
		list.add(8);
		List<Integer> respList = impl.findBestSeatsDecremental(list, 5, 10);
		Assert.assertTrue(respList.size() == 5);
		Assert.assertTrue(respList.contains(10) && respList.contains(3) && respList.contains(6));
	}
}
