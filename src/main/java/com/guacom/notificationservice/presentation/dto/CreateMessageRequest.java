package com.guacom.notificationservice.presentation.dto;

import com.guacom.notificationservice.domain.enums.MessageCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {

    @NotNull(message = "Category is required")
    private MessageCategory category;

    @NotNull(message = "Content is required")
    @Size(min = 10, max = 1000, message = "Content must be between 10 and 1000 characters")
    private String content;
}