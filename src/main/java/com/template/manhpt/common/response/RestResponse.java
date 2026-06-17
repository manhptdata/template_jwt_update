package com.template.manhpt.common.response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
	private int statusCode;
	private String error;
	private Object message;
	private T data;
}
