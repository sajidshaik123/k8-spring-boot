package com.k8.service.impl;

import static com.k8.constants.KubernetesConstants.EXECUTE_REST_CALL;
import static com.k8.constants.KubernetesConstants.GET_NAMESPACES;
import static com.k8.constants.KubernetesConstants.GET_PODS_BY_NAMESPACES;
import static com.k8.constants.KubernetesConstants.GET_PODS_INFO_BY_SERVICENAME;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k8.model.KubernetesRequest;
import com.k8.model.KubernetesResponse;
import com.k8.model.KubernetesResponse.Status;
import com.k8.service.KubernetesService;
import com.k8.utils.KubernetesUtilities;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodStatus;

@Service
public class KubernetesServiceImpl implements KubernetesService {

	@Autowired
	KubernetesUtilities k8Utilities;

	Logger logger = LoggerFactory.getLogger(KubernetesService.class);

	public KubernetesResponse kubernetesService(String methodName, KubernetesRequest kubernetesRequest) {
		KubernetesResponse response = null;
		try {
			response = executeK8Method(methodName, response, kubernetesRequest);
		} catch (ApiException e) {
			String errorMessage = e.getResponseBody();
			String details = (e.getCause() == null) ? kubernetesRequest.toString() : e.getCause().getLocalizedMessage();
			response = new KubernetesResponse(null, details, errorMessage, Status.FAILED);
			logger.error(details);
			logger.error(errorMessage);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			String details = (e.getCause() == null) ? kubernetesRequest.toString() : e.getCause().getLocalizedMessage();
			response = new KubernetesResponse(null, details, errorMessage, Status.FAILED);
			logger.error(errorMessage);
			e.printStackTrace();
		}
		return response;
	}

	public KubernetesResponse executeK8Method(String methodName, KubernetesResponse response,
			KubernetesRequest kubernetesRequest) throws IOException, ApiException {
		return switch (methodName) {
		case GET_NAMESPACES -> getNamespaces();
		case GET_PODS_BY_NAMESPACES -> getPodsByNameSpace(kubernetesRequest, response);
		case GET_PODS_INFO_BY_SERVICENAME -> getPodsInfoByServicename(kubernetesRequest, response);
		case EXECUTE_REST_CALL -> executeRestCall(kubernetesRequest, response);
		default -> null;
		};
	};

	public KubernetesResponse getNamespaces() throws IOException, ApiException {
		Map<String, Object> responseMap = new TreeMap<>();
		responseMap.put("namespaces", KubernetesUtilities.getNamespaces());
		return new KubernetesResponse(responseMap, null, null, Status.OK);
	}

	public KubernetesResponse getPodsByNameSpace(KubernetesRequest kubernetesRequest, KubernetesResponse response)
			throws ApiException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
		String namespace = kubernetesRequest.namespace();
		String errorMessage = null;
		if (StringUtils.isEmpty(namespace)) {
			errorMessage = "Parameter -> namespace is empty";
			logger.error(errorMessage);
			return new KubernetesResponse(null, errorMessage, null, Status.FAILED);
		}
		V1PodList v1PodList = KubernetesUtilities.getV1PodListByNamespace(namespace);
		if (CollectionUtils.isNotEmpty(v1PodList.getItems())) {
			// Working Pods
			List<String> workingPods = KubernetesUtilities.getPodsByStatus(v1PodList, Boolean.TRUE);
			if (CollectionUtils.isNotEmpty(workingPods)) {
				responseMap.put("workingPods", workingPods);
				responseMap.put("workingPodsCount", workingPods.size());
			}
			// Non-Working Pods
			List<String> nonWorkingPods = KubernetesUtilities.getPodsByStatus(v1PodList, Boolean.FALSE);
			if (CollectionUtils.isNotEmpty(nonWorkingPods)) {
				responseMap.put("nonWorkingPods", nonWorkingPods);
				responseMap.put("nonWorkingPodsCount", nonWorkingPods.size());
			}
			return new KubernetesResponse(responseMap, kubernetesRequest.toString(), errorMessage, Status.OK);
		}
		errorMessage = "Oops, Empty pods for " + namespace + " namespace";
		logger.error(errorMessage);
		return new KubernetesResponse(responseMap, kubernetesRequest.toString(), errorMessage, Status.FAILED);
	}

	public static KubernetesResponse getPodsInfoByServicename(KubernetesRequest kubernetesRequest,
			KubernetesResponse response) throws ApiException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
		List<V1Pod> servicePodList = KubernetesUtilities.getV1PodsByService(kubernetesRequest.namespace(),
				kubernetesRequest.servicename());
		servicePodList.stream().filter(Objects::nonNull).forEach((v1Pod) -> {
			Map<String, Object> podDetailsMap = new HashMap<>();
			V1PodStatus podStatus = v1Pod.getStatus();
			podDetailsMap.put("ip", podStatus.getPodIP());
			podDetailsMap.put("startTime", podStatus.getStartTime());
			podDetailsMap.put("started", podStatus.getContainerStatuses().get(0).getStarted());
			responseMap.put(v1Pod.getMetadata().getName(), podDetailsMap);
		});
		return new KubernetesResponse(responseMap, null, null, Status.OK);
	}

	public KubernetesResponse executeRestCall(KubernetesRequest kubernetesRequest, KubernetesResponse response)
			throws ApiException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
		String details = "Details ->" + kubernetesRequest.toString();
		String data = k8Utilities.executeEndpoint(kubernetesRequest.podname(), kubernetesRequest.namespace(),
				kubernetesRequest.endpoint());
		if (StringUtils.isEmpty(data)) {
			String message = "Oops, Empty Data";
			logger.error(message);
			return new KubernetesResponse(responseMap, details, message, Status.FAILED);
		}
		responseMap.put("data", data);
		return new KubernetesResponse(responseMap, details, null, Status.OK);
	}

}
