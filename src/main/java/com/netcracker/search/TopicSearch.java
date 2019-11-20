package com.netcracker.search;

import com.netcracker.models.Topic;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class TopicSearch {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Topic> searchTopics(String text) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                                    .buildQueryBuilder().forEntity(Topic.class).get();

        Query query = queryBuilder
                      .keyword()
                      .onFields("title")
                      .matching(text)
                      .createQuery();

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Topic.class);

        return (List<Topic>) jpaQuery.getResultList().stream()
                .map(result -> (Topic)result)
                .collect(Collectors.toList());
    }

}
