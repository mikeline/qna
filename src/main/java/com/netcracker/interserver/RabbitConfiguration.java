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
import org.springframework.context.event.EventListener;

@Configuration
public class RabbitConfiguration {
    public static final String RABBIT_ADDRESS = "localhost";

    public static final String EXCHANGE_FANOUT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS = "qna.fanoutSubscribeUnsubscribeRequests";
    public static final String EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS = "qna.directSubscribeUnsubscribeRequests";
    public static final String EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REPLIES = "qna.directSubscribeUnsubscribeReplies";
//    public static final String EXCHANGE_REPLICATE_DATA = "EXCHANGE_REPLICATE_DATA";
//    public static final String EXCHANGE_SEARCH_DATA = "EXCHANGE_SEARCH_DATA";
    public static final String EXCHANGE_CHECK_ROLE = "qna.checkStatus";

    public static final String QUEUE_SUBSCRIBE_UNSUBSCRIBE_REQUESTS = "qna.subscribeUnsubscribeRequests";
    public static final String QUEUE_SUBSCRIBE_UNSUBSCRIBE_REPLIES = "qna.subscribeUnsubscribeReplies";
//    public static final String QUEUE_REPLICATE_DATA = "QUEUE_REPLICATE_DATA";
//    public static final String QUEUE_SEARCH_DATA = "QUEUE_SEARCH_DATA";



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
    public FanoutExchange exchangeFanoutSubscribeUnsubscribeRequests() {
        return new FanoutExchange(EXCHANGE_FANOUT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS);
    }

    @Bean
    public DirectExchange exchangeDirectSubscribeUnsubscribeRequests() {
        return new DirectExchange(EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS);
    }

    @Primary //fixme
    @Bean(EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REPLIES)
    public DirectExchange exchangeDirectSubscribeUnsubscribeReplies() {
        return new DirectExchange(EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REPLIES);
    }



    // queues

    @Bean
    public Queue queueSubscribeUnsubscribeRequests() {
        return new Queue(QUEUE_SUBSCRIBE_UNSUBSCRIBE_REQUESTS);
    }

    @Primary //fixme
    @Bean(QUEUE_SUBSCRIBE_UNSUBSCRIBE_REPLIES)
    public Queue queueSubscribeUnsubscribeReplies() {
        return new Queue(QUEUE_SUBSCRIBE_UNSUBSCRIBE_REPLIES);
    }


    // bindings

    @Bean
    public Binding bindFanoutSubscribeUnsubscribeRequests() {
        return BindingBuilder
                .bind(queueSubscribeUnsubscribeRequests())
                .to(exchangeFanoutSubscribeUnsubscribeRequests());
    }

//    // ??? can I get UUID Here???
//    @Bean
//    public Binding bindDirectSubscribeUnsubscriveRequests() {
//        return BindingBuilder
//                .bind(queueSubscribeUnsubscribeReplies())
//                .to(exchangeDirectSubscribeUnsubscribeReplies())
//                .with()
//    }

//    @Bean
//    public Binding friendSearchBinding() {
//        return BindingBuilder
//                .bind(friendSearchQueue())
//                .to(friendSearchExchange());
//    }

//    @Bean
//    public Binding replicateDataBinding() {
//        rabbitAdmin().removeBinding();
//
//        return BindingBuilder
//                .bind(replicateDataQueue())
//                .to(replicateDataExchange())
//                .with();
//
//    }

//    @Bean
//    public Binding bindSearchData() {
//        return BindingBuilder
//                .bind(searchDataQueue())
//                .to(searchDataExchange())
//                .with();
//    }



}
