package com.k8.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record KubernetesRequest(String namespace, String servicename, String podname, String endpoint) {
}
