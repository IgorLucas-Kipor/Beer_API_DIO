package com.igorlucas.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.igorlucas.dto.BeerDTO;
import com.igorlucas.entity.Beer;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}