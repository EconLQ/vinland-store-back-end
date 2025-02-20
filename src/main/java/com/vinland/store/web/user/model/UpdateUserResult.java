package com.vinland.store.web.user.model;

public record UpdateUserResult(UserInfoDTO updatedUser, String newAccessToken, boolean emailOrUsernameChanged) {
}
