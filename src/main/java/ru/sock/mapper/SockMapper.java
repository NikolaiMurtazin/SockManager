package ru.sock.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.sock.dto.SockDto;
import ru.sock.model.Sock;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SockMapper {

    SockDto toSockDto(Sock sock);
}
