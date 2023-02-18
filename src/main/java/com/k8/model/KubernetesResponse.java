package com.k8.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public record KubernetesResponse (Map<String, Object> data, String detail, String errorMessage, Status status){
	public enum Status {
		OK, FAILED
	}
}
