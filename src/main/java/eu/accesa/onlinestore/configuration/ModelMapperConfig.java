package eu.accesa.onlinestore.configuration;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(OrderEntity.class, OrderDto.class)
                .addMapping(orderEntity -> orderEntity.getUser().getId(), OrderDto::setUserId);
        return modelMapper;
    }
}
