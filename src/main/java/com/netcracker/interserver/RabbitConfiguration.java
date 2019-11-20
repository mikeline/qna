package com.netcracker.interserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;



//Some queues are defined and bind in ThisNode.java
@Configuration
public class RabbitConfiguration {
    public static final String RABBIT_ADDRESS = "localhost";

    public static final String EXCHANGE_REQUEST_SUMMARY = "qna.exchangeRequestSummary";
    public static final String QUEUE_REQUEST_SUMMARY = "qna.queueRequestSummary";

    public static final String EXCHANGE_REPLY_SUMMARY = "qna.exchangeReplyResume";
    public static final String QUEUE_REPLY_SUMMARY = "qna.queueReplySummary";

    public static final String EXCHANGE_PUBLISH_REPLICATION = "qna.exchangePublishReplication";
    public static final String EXCHANGE_SEARCH_QUERIES = "qna.search_queries";

    public static final String EXCHANGE_AUTH_QUERIES = "qna.auth_queries";
    public static final String QUEUE_AUTH_SUMMARY = "qna.queueAuthSummary";

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(RABBIT_ADDRESS);
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(objectMapper());
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*"); // todo: security?
        jsonConverter.setClassMapper(classMapper);
        jsonConverter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        return jsonConverter;
    }



    // exchanges
    @Bean
    public FanoutExchange exchangeRequestSummary() {
        return new FanoutExchange(EXCHANGE_REQUEST_SUMMARY);
    }

    @Bean
    public FanoutExchange exchangeReplyResume() {
        return new FanoutExchange(EXCHANGE_REPLY_SUMMARY);
    }

    @Primary
    @Bean
    public TopicExchange exchangePublishReplication() {
        return new TopicExchange(EXCHANGE_PUBLISH_REPLICATION);
    }

    @Bean
    public DirectExchange exchangeSearchQueries() {
        return new DirectExchange(EXCHANGE_SEARCH_QUERIES);
    }

    @Bean
    public FanoutExchange exchangeAuthQuery() { return new FanoutExchange(EXCHANGE_AUTH_QUERIES); }

    // queues
    @Bean
    public Queue queueRequestSummary() {
        return new Queue(QUEUE_REQUEST_SUMMARY);
    }

    @Bean
    public Queue queueReplySummary() {
        return new Queue(QUEUE_REPLY_SUMMARY);
    }

    @Bean
    public Queue queueAuthSummary() {
        return new Queue(QUEUE_AUTH_SUMMARY);
    }

    //bindings
    @Bean
    public Binding bindRequestResume() {
        return BindingBuilder
                .bind(queueRequestSummary())
                .to(exchangeRequestSummary());
    }

    @Bean
    public Binding bindReplyResume() {
        return BindingBuilder
                .bind(queueReplySummary())
                .to(exchangeReplyResume());
    }

    @Bean
    public Binding bindAuthResume() {
        return BindingBuilder
                .bind(queueAuthSummary())
                .to(exchangeAuthQuery());
    }
}
