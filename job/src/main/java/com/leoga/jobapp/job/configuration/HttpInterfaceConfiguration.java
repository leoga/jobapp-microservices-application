package com.leoga.jobapp.job.configuration;

import com.leoga.jobapp.job.clients.CompanyServiceClient;
import com.leoga.jobapp.job.clients.ReviewServiceClient;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@AllArgsConstructor
public class HttpInterfaceConfiguration {

    private ObservationRegistry observationRegistry;
    private Tracer tracer;
    private Propagator propagator;

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Eureka profile
     * Uses Spring Cloud LoadBalancer + Eureka Discovery
     */
    @Bean
    @LoadBalanced
    @Profile({"default", "gitconfig","docker", "gitdocker"})
    @Qualifier("customRestClientBuilder")
    public RestClient.Builder loadBalancedRestClientBuilder() {

        RestClient.Builder builder = RestClient.builder();

        if (null != observationRegistry) {
            builder.requestInterceptor(createTracingInterceptor());
        }

        return builder
                .defaultStatusHandler(HttpStatusCode::isError,
                        ((request, response) -> {}
                        ));
    }

    /**
     * Kubernetes profile
     * Uses native Kubernetes DNS + Service load balancing
     */
    @Bean
    @Profile({"k8s", "gitk8s"})
    @Qualifier("customRestClientBuilder")
    public RestClient.Builder restClientBuilderK8s() {

        RestClient.Builder builder = RestClient.builder();

        if (null != observationRegistry) {
            builder.requestInterceptor(createTracingInterceptor());
        }

        return builder
                .defaultStatusHandler(HttpStatusCode::isError,
                        ((request, response) -> {}
                        ));
    }

    private ClientHttpRequestInterceptor createTracingInterceptor() {
        return ((request, body, execution) -> {
            if (null != tracer && null != propagator
                    && null != tracer.currentSpan()) {
                propagator.inject(tracer.currentTraceContext().context(),
                        request.getHeaders(),
                        (carrier, key, value) -> carrier.add(key, value));
            }
            return execution.execute(request, body);
        }
        );
    }

    @Bean
    public CompanyServiceClient companyHttpInterface(@Qualifier("customRestClientBuilder") RestClient.Builder restClientBuilder) {

        RestClient restClient = restClientBuilder.baseUrl("http://company-service").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(CompanyServiceClient.class);

    }

    @Bean
    public ReviewServiceClient reviewHttpInterface(@Qualifier("customRestClientBuilder") RestClient.Builder restClientBuilder) {

        RestClient restClient = restClientBuilder.baseUrl("http://review-service").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(ReviewServiceClient.class);

    }

}
