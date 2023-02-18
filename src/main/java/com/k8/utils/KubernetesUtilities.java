package com.k8.utils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

@Component
public class KubernetesUtilities {

	public static CoreV1Api coreV1Api() throws IOException {
		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api(client);
		return api;
	}

	public static V1PodList getV1PodListByNamespace(String namespace) throws ApiException, IOException {
		V1PodList v1PodList = coreV1Api().listNamespacedPod(namespace, null, null, null, null, null, null, null, null,
				null, false);
		return v1PodList;
	}

	public static List<String> getPods(String namespace, String serviceName) throws ApiException, IOException {
		V1PodList v1PodList = getV1PodListByNamespace(namespace);
		List<String> podList = v1PodList.getItems().stream().filter(Objects::nonNull)
				.map(s -> s.getMetadata().getName()).collect(Collectors.toList());
		return podList;
	}

	public static List<V1Pod> getV1PodsByService(String namespace, String serviceName)
			throws ApiException, IOException {

		V1PodList v1PodList = getV1PodListByNamespace(namespace);
		List<V1Pod> servicePodList = v1PodList.getItems().stream().filter(Objects::nonNull)
				.filter(m -> m.getMetadata().getName().startsWith(serviceName)).collect(Collectors.toList());
		return servicePodList;
	}

	public static List<String> getPodNamesByService(String namespace, String serviceName)
			throws ApiException, IOException {
		List<String> podList = getV1PodsByService(namespace, serviceName).stream().filter(Objects::nonNull)
				.filter(s -> s.getMetadata().getName().startsWith(serviceName)).map(s -> s.getMetadata().getName())
				.collect(Collectors.toList());
		return podList;
	}

	public static V1Pod getPodDetails(String namespace, String serviceName) throws IOException, ApiException {
		return coreV1Api().readNamespacedPod(serviceName, namespace, null);
	}

	public static List<String> getPodsByStatus(V1PodList v1PodList, Boolean status) {
		return v1PodList.getItems().stream().filter(Objects::nonNull)
				.filter(i -> (i.getStatus().getContainerStatuses().get(0).getStarted()).equals(status))
				.map((pod) -> pod.getMetadata().getName()).toList();
	}

	public String executeEndpoint(String podname, String namespace, String api) throws ApiException, IOException {
		String data = coreV1Api().connectGetNamespacedPodProxyWithPath(podname, namespace, api, null);
		return data;
	}

	public static List<String> getNamespaces() throws IOException, ApiException {
		V1NamespaceList v1NamespaceList = coreV1Api().listNamespace(Boolean.TRUE.toString(), null, null, null, null, 0,
				null, null, Integer.MAX_VALUE, Boolean.FALSE);
		List<String> namespaces = v1NamespaceList.getItems().stream().filter(Objects::nonNull)
				.map(v1Namespace -> v1Namespace.getMetadata().getName()).toList();
		return namespaces;
	}

}
