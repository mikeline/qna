package com.netcracker.interserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;


//Some queues are defined and bind in ThisNode.java
@Configuration
public class RabbitConfiguration {
    public static final String RABBIT_ADDRESS = "localhost";

    public static final String EXCHANGE_REQUEST_SUMMARY = "qna.exchangeRequestSummary";
//    public static final String QUEUE_REQUEST_SUMMARY = "qna.queueRequestSummary";

//    public static final String EXCHANGE_REPLY_SUMMARY = "qna.exchangeReplySummary";
//    public static final String QUEUE_REPLY_SUMMARY = "qna.queueReplySummary";

    public static final String EXCHANGE_PUBLISH_REPLICATION = "qna.exchangePublishReplication";
    public static final String EXCHANGE_SEARCH_QUERIES = "qna.search_queries";

    public static final String EXCHANGE_USER_AUTHENTICATION = "qna.userAuthenticationExchange";

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(rabbitTemplate());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(RABBIT_ADDRESS);
    }


    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(objectMapper(), "*");
//        DefaultClassMapper classMapper = new DefaultClassMapper();
//        classMapper.setTrustedPackages("*"); // todo: security?
//        jsonConverter.setClassMapper(classMapper);
        jsonConverter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        return jsonConverter;
    }



    // exchanges

    @Bean("auth_exchange")
    public FanoutExchange exchangeAuthQuery() { return new FanoutExchange(EXCHANGE_USER_AUTHENTICATION); }

    @Bean("summary_request")
    public FanoutExchange exchangeRequestSummary() {
        return new FanoutExchange(EXCHANGE_REQUEST_SUMMARY);
    }

//    @Bean(name = EXCHANGE_REPLY_SUMMARY)
//    public FanoutExchange exchangeReplySummary() {
//        return new FanoutExchange(EXCHANGE_REPLY_SUMMARY);
//    }

    @Bean
    public TopicExchange exchangePublishReplication() {
        return new TopicExchange(EXCHANGE_PUBLISH_REPLICATION);
    }

    @Bean
    public DirectExchange exchangeSearchQueries() {
        return new DirectExchange(EXCHANGE_SEARCH_QUERIES);
    }

    // queues
    @Bean
    @Scope("prototype")
    @Primary
    public Queue queue() {
        return rabbitAdmin().declareQueue();
    }
//
//    @Bean
//    public Queue queueReplySummary() {
//        return new Queue(QUEUE_REPLY_SUMMARY);
//    }

//    //bindings
//    @Bean
//    public Binding bindRequestResume() {
//        return BindingBuilder
//                .bind(queueRequestSummary())
//                .to(exchangeRequestSummary());
//    }
}
