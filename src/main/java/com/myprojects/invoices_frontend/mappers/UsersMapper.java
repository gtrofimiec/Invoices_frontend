package com.myprojects.invoices_frontend.mappers;

import com.myprojects.invoices_frontend.domain.Users;
import com.myprojects.invoices_frontend.domain.dtos.UsersDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class UsersMapper {

    public Users mapToUser(final @NotNull UsersDto userDto) {
        InvoicesMapper invoicesMapper = new InvoicesMapper();
        return new Users(
                userDto.getId(),
                userDto.getFullName(),
                userDto.getNip(),
                userDto.getStreet(),
                userDto.getPostcode(),
                userDto.getTown(),
                userDto.isActive(),
                invoicesMapper.mapToInvoicesList(userDto.getInvoicesDtoList())
        );
    }

    public UsersDto mapToUserDto(final @NotNull Users user) {
        InvoicesMapper invoicesMapper = new InvoicesMapper();
        return new UsersDto(
                user.getId(),
                user.getFullName(),
                user.getNip(),
                user.getStreet(),
                user.getPostCode(),
                user.getTown(),
                user.isActive(),
                invoicesMapper.mapToInvoicesDtoList(user.getInvoicesList())
        );
    }

    public List<Users> mapToUsersList(final @NotNull List<UsersDto> usersDtoList) {
        return usersDtoList.stream()
                .map(this::mapToUser)
                .collect(Collectors.toList());
    }

    public List<UsersDto> mapToUsersDtoList(final @NotNull List<Users> usersList) {
        return usersList.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }
}