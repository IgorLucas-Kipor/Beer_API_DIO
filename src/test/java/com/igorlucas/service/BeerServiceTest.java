package com.igorlucas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.igorlucas.builder.BeerDTOBuilder;
import com.igorlucas.dto.BeerDTO;
import com.igorlucas.entity.Beer;
import com.igorlucas.exceptions.BeerAlreadyRegisteredException;
import com.igorlucas.mapper.BeerMapper;
import com.igorlucas.repository.BeerRepository;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {
	
	private static final long INVALID_BEER_ID = 1L;
	
	@Mock
	private BeerRepository beerRepository;
	
	private BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@InjectMocks
	private BeerService beerService;
	
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		
		//given
		
		BeerDTO expetedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expetedBeerDTO);
		
		
		//when
		
		Mockito.when(beerRepository.findByName(expetedBeerDTO.getName())).thenReturn(Optional.empty());
		Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		//then
		
		BeerDTO createdBeerDTO = beerService.createBeer(expetedBeerDTO);
		
		MatcherAssert.assertThat(createdBeerDTO.getId(), Matchers.is(Matchers.equalTo(expetedBeerDTO.getId())));
		MatcherAssert.assertThat(createdBeerDTO.getName(), Matchers.is(Matchers.equalTo(expetedBeerDTO.getName())));
		MatcherAssert.assertThat(createdBeerDTO.getQuantity(), Matchers.is(Matchers.equalTo(expetedBeerDTO.getQuantity())));
		
//		assertEquals(expetedBeerDTO.getId(), createdBeerDTO.getId());
//		assertEquals(expetedBeerDTO.getName(), createdBeerDTO.getName());
		
	}

}
