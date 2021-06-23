package com.igorlucas.controller;

import static com.igorlucas.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.igorlucas.builder.BeerDTOBuilder;
import com.igorlucas.dto.BeerDTO;
import com.igorlucas.dto.QuantityDTO;
import com.igorlucas.exceptions.BeerNotFoundException;
import com.igorlucas.service.BeerService;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

	private static final String BEER_API_URL_PATH = "/api/v1/beers";
	private static final long VALID_BEER_ID = 1L;
	private static final long INVALID_BEER_ID = 2l;
	private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
	private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

	private MockMvc mockMvc;

	@Mock
	private BeerService beerService;

	@InjectMocks
	private BeerController beerController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(beerController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setViewResolvers((s, locale) -> new MappingJackson2JsonView(null)).build();
	}

	@Test
	void whenPOSTIsCalledThenABeerIsCreated() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

		// then

		mockMvc.perform(post(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON).content(asJsonString(beerDTO)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.name", is(beerDTO.getName())))
				.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));

	}

	@Test
	void whenPOSTIsCalledWithoutRequiredFieldThenAErrorIsReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		beerDTO.setBrand(null);

		// then

		mockMvc.perform(post(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON).content(asJsonString(beerDTO)))
				.andExpect(status().isBadRequest());

	}

	@Test
	void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

		// then

		mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(beerDTO.getName())))
				.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));

	}

	@Test
	void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

		// then

		mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	void whenGETListWithBeersIsCalledThenStatusOkIsReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

		// then

		mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
				.andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
	}

	@Test
	void whenGETListWithoutBeersIsCalledThenStatusOkIsReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

		// then

		mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void whenDELETEIsCalledWithValidIdThenNoContentShouldBeReturned() throws Exception {

		// given

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when

		Mockito.doNothing().when(beerService).deleteById(beerDTO.getId());

		// then

		mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
	}

	@Test
	void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
		// when
		Mockito.doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);

		// then
		mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {

		// given

		QuantityDTO quantityDTO = QuantityDTO.builder().quantity(10).build();

		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

		// when

		Mockito.when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);

		// then

		mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(quantityDTO)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is(beerDTO.getName())))
				.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
				.andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
	}

}