package com.igorlucas.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;
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
import com.igorlucas.exceptions.BeerNotFoundException;
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
		
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
		
		
		//when
		
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		//then
		
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);
		
		MatcherAssert.assertThat(createdBeerDTO.getId(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getId())));
		MatcherAssert.assertThat(createdBeerDTO.getName(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getName())));
		MatcherAssert.assertThat(createdBeerDTO.getQuantity(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getQuantity())));
		
		MatcherAssert.assertThat(createdBeerDTO.getQuantity(), Matchers.greaterThan(2));
		
//		assertEquals(expetedBeerDTO.getId(), createdBeerDTO.getId());
//		assertEquals(expetedBeerDTO.getName(), createdBeerDTO.getName());
		
	}
	
	@Test
	void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
		
		//given
		
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
		
		//then
		
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
	}
	
	@Test
	void whenAValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
		
		//given
		
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when
		
		Mockito.when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));
		
        // then
		
        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
	}
	
	@Test
	void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
		
		//given
		
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		//when
		
		Mockito.when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());
		
        // then
		
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
	}
	
	@Test
	void whenListBeerIsCalledThenReturnListOfBeers() {
		
		//given
		
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when
		
		Mockito.when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));
		
		//then
		
		List<BeerDTO> foundListBeersDTO = beerService.listAll();
		
		assertThat(foundListBeersDTO, is(not(empty())));
		assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
		
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
		
		//when
		
		Mockito.when(beerRepository.findAll()).thenReturn(Collections.emptyList());
		
		//then
		
		List<BeerDTO> foundListBeersDTO = beerService.listAll();
		
		assertThat(foundListBeersDTO, is(empty()));
	}
	
	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		
		//given
		
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);
		
		//when
		
		Mockito.when(beerRepository.findById(expectedDeletedBeer.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		Mockito.doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());
		
		//then
		
		beerService.deleteById(expectedDeletedBeerDTO.getId());
		
		Mockito.verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		Mockito.verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}

}
