package com.k8.controller;

import static com.k8.constants.KubernetesConstants.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k8.model.KubernetesRequest;
import com.k8.model.KubernetesResponse;
import com.k8.service.KubernetesService;

import io.kubernetes.client.openapi.ApiException;

@RestController
@RequestMapping(K8)
public class KubernetesController {

	@Autowired
	KubernetesService kubernetesService;

	@GetMapping(GET_NAMESPACES)
	public KubernetesResponse getNamespaces(@RequestBody(required = false) KubernetesRequest kubernetesRequest) throws Exception {
		return kubernetesService.kubernetesService(GET_NAMESPACES, kubernetesRequest);
	}

	@GetMapping(GET_PODS_BY_NAMESPACES)
	public KubernetesResponse getPodsByNamespace(@RequestBody KubernetesRequest kubernetesRequest) throws Exception {
		return kubernetesService.kubernetesService(GET_PODS_BY_NAMESPACES, kubernetesRequest);
	}
	
	@GetMapping(GET_PODS_INFO_BY_SERVICENAME)
	public KubernetesResponse getPodsInfoByServicename(@RequestBody KubernetesRequest kubernetesRequest) throws ApiException, IOException {
		return kubernetesService.kubernetesService(GET_PODS_INFO_BY_SERVICENAME, kubernetesRequest);
	}

	@GetMapping(EXECUTE_REST_CALL)
	public KubernetesResponse executeRestCall(@RequestBody KubernetesRequest kubernetesRequest)
			throws Exception {
		return kubernetesService.kubernetesService(EXECUTE_REST_CALL, kubernetesRequest);
	}

}
