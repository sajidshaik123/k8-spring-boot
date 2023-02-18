package com.k8.service;

import com.k8.model.KubernetesRequest;
import com.k8.model.KubernetesResponse;

public interface KubernetesService {

	KubernetesResponse kubernetesService(String methodName, KubernetesRequest kubernetesRequest);

}
