package com.thinkbiganalytics.servicemonitor.rest.client.ambari;

import com.thinkbiganalytics.servicemonitor.rest.client.RestClient;
import com.thinkbiganalytics.servicemonitor.rest.client.RestClientConfig;
import com.thinkbiganalytics.servicemonitor.rest.client.TextPlainJackson2HttpMessageConverter;
import com.thinkbiganalytics.servicemonitor.rest.model.ambari.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sr186054 on 10/1/15.
 */
@Component
public class AmbariClient extends RestClient {


    @Autowired
    @Qualifier("ambariRestClientConfig")
    private RestClientConfig clientConfig;


    public AmbariClient() {
        HttpMessageConverter c = new TextPlainJackson2HttpMessageConverter();
        setAdditionalMessageConverters(Arrays.asList(c));
    }

    public RestClientConfig getConfig() {
        return clientConfig;
    }

    public List<String> getAmbariClusterNames() {
        List<String> clusterNames = new ArrayList<>();
        ClusterList clusterList = super.doGet(new AmbariGetClustersCommand());
        if (clusterList != null) {
            List<ClusterItem> items = clusterList.getItems();
            if (items != null) {
                for (ClusterItem item : items) {
                    Cluster cluster = item.getCluster();
                    if (cluster != null) {
                        String clusterName = cluster.getClusterName();
                        clusterNames.add(clusterName);
                    }
                }
            }
        }
        return clusterNames;

    }


    public ServiceComponentInfoSummary getServiceComponentInfo(List<String> clusterNames, String services) throws RestClientException {
        ServiceComponentInfoSummary summary = null;
        for (String clusterName : clusterNames) {
            AmbariServicesComponentInfoCommand servicesComponentInfoCommand = new AmbariServicesComponentInfoCommand(clusterName, services);
            ServiceComponentInfoSummary clusterSummary = super.doGet(servicesComponentInfoCommand);
            if (clusterSummary != null) {
                if (summary == null) {
                    summary = clusterSummary;
                } else {
                    summary.getItems().addAll(clusterSummary.getItems());
                }
            }
        }
        return summary;

    }

    public AlertSummary getAlerts(List<String> clusterNames, String services) throws RestClientException {
        AlertSummary alerts = null;
        for (String clusterName : clusterNames) {
            AmbariAlertsCommand alertsCommand = new AmbariAlertsCommand(clusterName, services);
            AlertSummary alertSummary = super.doGet(alertsCommand);
            if (alerts == null) {
                alerts = alertSummary;
            } else {
                alerts.getItems().addAll(alertSummary.getItems());
            }
        }
        return alerts;
    }

    public void setClientConfig(RestClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}