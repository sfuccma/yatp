package de.jmanson.rest.database.provider;

import org.apache.http.HttpEntity;

import java.util.List;
import java.util.Map;

/**
 * The interface Database provider context.
 *
 * @param <T> the type parameter
 */
public interface DatabaseProviderContext<T> {
    /**
     * Find all list.
     *
     * @return the list
     */
    List<T> findAll();

    /**
     * Find by id t.
     *
     * @param id the id
     * @return the t
     */
    T findById(Object id);

    /**
     * Find by filter list.
     *
     * @param filterMap the filter map
     * @return the list
     */
    List<T> findByFilter(Map<String, Object> filterMap);

    /**
     * Delete.
     *
     * @param id the id
     */
    void delete(Object id);

    /**
     * Put t.
     *
     * @param o the o
     * @return the t
     */
    T put(Object o);

    /**
     * Post t.
     *
     * @param o the o
     * @return the t
     */
    T post(Object o);

    /**
     * Get as array t [ ].
     *
     * @param <T>        the type parameter
     * @param resultList the result list
     * @return the t [ ]
     */
    <T> T[] getAsArray(List<T> resultList);

    /**
     * To http entity http entity.
     *
     * @param resultList the result list
     * @return the http entity
     */
    HttpEntity toHTTPEntity(List<T> resultList);
}
