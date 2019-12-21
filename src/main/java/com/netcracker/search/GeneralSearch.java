package com.netcracker.search;

import com.netcracker.models.Post;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class GeneralSearch {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Object> search(String text) {

        List<Object> foundPosts = new ArrayList<>();
        foundPosts.addAll(searchEntities(text, "title", Topic.class));
        foundPosts.addAll(searchEntities(text, "body", Post.class));

        return foundPosts;
    }

    public List<Object> searchEntities(String text, String field, Class className) {

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(className).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                    .withPrefixLength(1)
                .onFields(field)
                .matching(text)
                .createQuery();

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query);

        return (List<Object>) jpaQuery.getResultList().stream()
                .map(result -> (Object)result)
                .collect(Collectors.toList());
    }

}
