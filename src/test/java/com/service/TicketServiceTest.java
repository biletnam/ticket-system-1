package com.service;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.*;
import com.repo.AdminRepository;
import com.repo.SeatHoldRepository;
import com.repo.SeatRepository;
import com.service.impl.BestSeatFindingServiceImpl;
import com.service.impl.TicketServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;

import org.junit.Assert;


public class TicketServiceTest {

	@InjectMocks
	TicketServiceImpl impl;
	
	@Mock
	SeatHoldRepository seatHoldRepository;
	
	@Mock
	SeatRepository seatRepository;
	
	@Mock
	AdminRepository adminRepository;
	
	@Mock
	BestSeatFindingServiceImpl ticketSystemUtils;
	Admin admin;
	SeatHold seatHold;
	
	@Before
	public void setUp() {
		initMocks(this);
		admin = new Admin();
		admin.setBestSeatCriteria("TOPTOBOTTOM");
		admin.setExpiryTimeInMinutes(5);
		admin.setTotalNumberOfSeats(100);
		
		seatHold = new SeatHold();
		seatHold.setCustomerEmail("test@tes.com");
		seatHold.setExpired(false);
		seatHold.setHoldPlacedTime(Instant.now());
		seatHold.setNumberOfSeats(3l);
		seatHold.setStatus("status");
		seatHold.setId(1);
	}
	
	@Test
	public void testSaveAdminProperties(){
		Admin resAdmin = new Admin();
		BeanUtils.copyProperties(admin, resAdmin);
		resAdmin.setId(1);
		when(adminRepository.save(admin)).thenReturn(resAdmin);
		Admin resp = impl.saveAdminProperties(admin);
		Assert.assertTrue(resp.getId() == 1);

	}
	
	@Test
	public void testNumSeatsAvailable(){
		when(adminRepository.findFirstByOrderByIdDesc()).thenReturn(admin);
		int resp = impl.numSeatsAvailable();
		Assert.assertTrue(resp == 100);
	}
	
	@Test
	public void testFindAndHoldSeats(){
		List<SeatHold> expiredSeatHoldList = new ArrayList<SeatHold>();
		expiredSeatHoldList.add(seatHold);
		when(seatHoldRepository.findByHoldStatusExpiry(isA(String.class), isA(Instant.class))).thenReturn(expiredSeatHoldList);
		when(seatHoldRepository.save(isA(SeatHold.class))).thenReturn(seatHold);

		when(seatRepository.findBySeatHoldId(isA(Integer.class))).thenReturn(getSeatList());
		when(adminRepository.findFirstByOrderByIdDesc()).thenReturn(admin);
		Seat seat = new Seat();
		List<Seat> reservedSeats = new ArrayList<Seat>();
		reservedSeats.add(seat);
		when(seatRepository.findByStatusIn(Arrays.asList(new String[]{SeatStatus.HOLD.toString(), SeatStatus.RESERVED.toString()}))).thenReturn(reservedSeats);
		when(seatHoldRepository.save(seatHold)).thenReturn(seatHold);
		when(seatRepository.save(isA(List.class))).thenReturn(new ArrayList());

		when(ticketSystemUtils.findBestSeatsDecremental(isA(List.class), isA(Integer.class), isA(Integer.class))).thenReturn(getSeatNumberList());
		SeatHold resp = impl.findAndHoldSeats(3, "test@gmail.com");
		Assert.assertNotNull(resp);
		Assert.assertTrue(resp.getNumberOfSeats() == 3);
	}
	
	@Test
	public void testFindAndHoldSeatsBottomToTop(){
		admin.setBestSeatCriteria("BOTTOMTOTOP");
		List<SeatHold> expiredSeatHoldList = new ArrayList<SeatHold>();
		expiredSeatHoldList.add(seatHold);
		when(seatHoldRepository.findByHoldStatusExpiry(isA(String.class), isA(Instant.class))).thenReturn(expiredSeatHoldList);
		when(seatHoldRepository.save(isA(SeatHold.class))).thenReturn(seatHold);
		when(seatRepository.findBySeatHoldId(isA(Integer.class))).thenReturn(getSeatList());
		when(adminRepository.findFirstByOrderByIdDesc()).thenReturn(admin);
		when(seatRepository.findByStatusIn(Arrays.asList(new String[]{SeatStatus.HOLD.toString(), SeatStatus.RESERVED.toString()}))).thenReturn(getSeatList());
		when(seatHoldRepository.save(seatHold)).thenReturn(seatHold);
		when(seatRepository.save(isA(List.class))).thenReturn(new ArrayList());
		when(ticketSystemUtils.findBestSeatsDecremental(isA(List.class), isA(Integer.class), isA(Integer.class))).thenReturn(getSeatNumberList());
		SeatHold resp = impl.findAndHoldSeats(3, "test@gmail.com");
		Assert.assertNotNull(resp);
		Assert.assertTrue(resp.getNumberOfSeats() == 3);
	}
	
	@Test
	public void testUpdateSeats(){
		when(seatRepository.save(isA(Seat.class))).thenReturn(new Seat());
		List<Seat> seatList = new ArrayList<Seat>();
		seatList.add(new Seat(3, SeatStatus.FREE.toString(), 1));
		seatList.add(new Seat(2, SeatStatus.FREE.toString(), 1));
		when(seatRepository.findBySeatHoldId(isA(Integer.class))).thenReturn(seatList);

		impl.updateSeat(seatHold.getId(), SeatStatus.HOLD.toString(), "abcd");

		verify(seatRepository, Mockito.atLeast(1)).save(isA(List.class));

	}
	
	@Test
	public void testReserveSeats(){
		when(seatHoldRepository.findOne(1)).thenReturn(seatHold);
		when(adminRepository.findFirstByOrderByIdDesc()).thenReturn(admin);
		String str = impl.reserveSeats(1, "test@test.com");
		Assert.assertNotNull(str);
		verify(seatHoldRepository, Mockito.atLeast(1)).findOne(1);
	}

	public List<Seat> getSeatList() {
		List<Seat> seatList = new ArrayList<Seat>();
		seatList.add(new Seat(3, SeatStatus.FREE.toString(), 1));
		seatList.add(new Seat(2, SeatStatus.FREE.toString(), 1));
		return seatList;
	}

	public List<Integer> getSeatNumberList() {
		List<Integer> seatNumberList = new ArrayList<Integer>();
		seatNumberList.add(97);
		seatNumberList.add(96);
		seatNumberList.add(95);
		return seatNumberList;
	}
}
