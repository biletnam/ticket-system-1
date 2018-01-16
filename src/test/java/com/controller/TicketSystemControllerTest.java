package com.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;

import static org.mockito.Mockito.when;


import com.repo.SeatHoldRepository;
import com.repo.SeatRepository;
import com.service.TicketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.Admin;
import com.SeatHold;


import org.junit.Assert;


@RunWith(SpringRunner.class)
@WebMvcTest(value = TicketSystemController.class, secure = false)
public class TicketSystemControllerTest {

	@InjectMocks
	TicketSystemController controller;

	@MockBean
	private TicketService ticketService;

	@MockBean
	private SeatRepository seatRepository;

	@MockBean
	private SeatHoldRepository seatHoldRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	Admin admin;

	@Before
	public void setup() {
		initMocks(this);
		admin = new Admin();
		admin.setBestSeatCriteria("TOPTOBOTTOM");
		admin.setTotalNumberOfSeats(100);
		admin.setExpiryTimeInMinutes(5);
	}
	
	@Test
	public void testGetTotalSeats() throws Exception{
		when(ticketService.numSeatsAvailable()).thenReturn(90);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/ticketSystem/retreiveTotalSeats").accept(
				MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String str=result.getResponse().getContentAsString();
		Assert.assertTrue(str.equals("90"));
	}
	
	 byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
	
	@Test
	public void testSetAdminProperties() throws Exception{
		Admin adminResp = new Admin();
		BeanUtils.copyProperties(admin, adminResp);
		adminResp.setId(1);
		when( ticketService.saveAdminProperties(isA(Admin.class))).thenReturn(adminResp);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/ticketSystem/setAdminValues")
				.accept(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(admin))
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		
		
		assertEquals("http://localhost/ticketSystem/setAdminValues/1",
				response.getHeader(HttpHeaders.LOCATION));

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}
	
	@Test
	public void testAddHold() throws Exception{
		Map<String, String> req = new HashMap<String, String>();
		req.put("numSeats", "3");
		req.put("customerEmail", "test@test.com");
		SeatHold seatHold = new SeatHold();
		seatHold.setId(1);
		when(ticketService.findAndHoldSeats(isA(Integer.class), isA(String.class))).thenReturn(seatHold);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/ticketSystem/addHold")
				.accept(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(req))
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		
		
		assertEquals("http://localhost/ticketSystem/addHold/1",
				response.getHeader(HttpHeaders.LOCATION));

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}
	
	@Test
	public void testAddHoldInvalidReq() throws Exception{
		Map<String, String> req = new HashMap<String, String>();
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/ticketSystem/addHold")
				.accept(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(req))
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}
	
	@Test
	public void testReserve() throws Exception{
		Map<String, String> req = new HashMap<String, String>();
		req.put("seatHoldId", "3");
		req.put("customerEmail", "test@test.com");
		when(ticketService.reserveSeats(isA(Integer.class), isA(String.class))).thenReturn("success");
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/ticketSystem/reserve")
				.accept(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(req))
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		String str=response.getContentAsString();

		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		Assert.assertTrue(str.equals("success"));
	}
	
	@Test
	public void testReserveInvalidReq() throws Exception{
		Map<String, String> req = new HashMap<String, String>();
		req.put("seatHoldId", "3");
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/ticketSystem/reserve")
				.accept(MediaType.APPLICATION_JSON).content(convertObjectToJsonBytes(req))
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
}
