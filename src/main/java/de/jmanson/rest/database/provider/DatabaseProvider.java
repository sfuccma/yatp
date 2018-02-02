package de.jmanson.rest.database.provider;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Database provider.
 *
 * @param <T> the type parameter
 */
public abstract class DatabaseProvider<T> {

    private final EntityManager em;
    private final CriteriaBuilder cb;
    private CriteriaQuery<T> q;
    private Root<T> r;
    private final Class<T> objectTypeClass;
    private final String primaryKey;

    private DatabaseProvider() {
        this.em = null;
        this.cb = null;

        this.objectTypeClass = this.objectType();
        this.primaryKey = this.getPrimaryKeyFromEntity();
    }

    /**
     * Instantiates a new Database provider.
     *
     * @param em the em
     */
    public DatabaseProvider(EntityManager em) {
        this.em = em;
        this.cb = this.em.getCriteriaBuilder();
        this.objectTypeClass = this.objectType();
        this.primaryKey = this.getPrimaryKeyFromEntity();
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<T> findAll() {
        q = cb.createQuery(this.objectTypeClass);
        r = q.from(objectTypeClass);

        q.select(r).orderBy(cb.asc(r.get(primaryKey)));

        TypedQuery<T> tq = em.createQuery(q);

        return tq.getResultList();
    }

    /**
     * Find by id t.
     *
     * @param id the id
     * @return the t
     */
    public T findById(Object id) {
        try {
            q = cb.createQuery(this.objectTypeClass);
            r = q.from(objectTypeClass);

            q.select(r).where(
                    cb.and(
                            addPredicate(null, cb.equal(r.get("id"), id))
                    )
            ).orderBy(cb.asc(r.get(primaryKey)));
            TypedQuery<T> tq = em.createQuery(q);

            return tq.getSingleResult();
        } catch (NoResultException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }

    }

    /**
     * Find by filter list.
     *
     * @param map the map
     * @return the list
     */
    public List<T> findByFilter(Map<String, Object> map) {
        return this.listByAndFilter(map);
    }

    /**
     * List by and filter list.
     *
     * @param andMap the and map
     * @return the list
     */
    public List<T> listByAndFilter(Map<String, Object> andMap) {
        q = cb.createQuery(this.objectTypeClass);
        r = q.from(objectTypeClass);
        List<Predicate> p = new ArrayList<>();
        for (Map.Entry<String, Object> entry : andMap.entrySet()) {
            Predicate p1 = cb.equal(r.get(entry.getKey()), entry.getValue());
            p.add(p1);
        }
        q.select(r).where(
                cb.and(
                        addPredicate(null, p.toArray(new Predicate[p.size()]))
                )
        ).orderBy(cb.asc(r.get(primaryKey)));

        TypedQuery<T> tq = em.createQuery(q);

        return tq.getResultList();

    }

    /**
     * Obj by and filter t.
     *
     * @param andMap the and map
     * @return the t
     */
    public T objByAndFilter(Map<String, Object> andMap) {

        q = cb.createQuery(this.objectTypeClass);
        r = q.from(objectTypeClass);
        List<Predicate> p = new ArrayList<>();
        for (Map.Entry<String, Object> entry : andMap.entrySet()) {
            Predicate p1 = cb.equal(r.get(entry.getKey()), entry.getValue());
            p.add(p1);
        }

        q.select(r).where(
                cb.and(
                        addPredicate(null, p.toArray(new Predicate[p.size()]))
                )
        ).orderBy(cb.asc(r.get(primaryKey)));
        TypedQuery<T> tq = em.createQuery(q);

        return tq.getSingleResult();

    }

    /**
     * Put t.
     *
     * @param o the o
     * @return the t
     */
    public T put(Object o) {
        try {
            return (T) this.em.merge(o);
        } catch (NoResultException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Post t.
     *
     * @param o the o
     * @return the t
     */
    public T post(Object o) {
        return this.put(o);
    }

    /**
     * Delete.
     *
     * @param o the o
     */
    public void delete(Object o) {
        try {
            this.em.remove(o);
        } catch (NoResultException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Gets em.
     *
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    private <T> Class<T> objectType() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Class clazz = (Class) parameterizedType.getActualTypeArguments()[0];

        return clazz;
    }

    private String getPrimaryKeyFromEntity() {

        Field[] fields = this.objectTypeClass.getDeclaredFields();
        for (Field f : fields) {
            Annotation a = f.getAnnotation(Id.class);
            if (a != null) {
                return f.getName();
            }
        }

        return "id";
    }

    /**
     * Add predicate predicate [ ].
     *
     * @param p1 the p 1
     * @param p2 the p 2
     * @return the predicate [ ]
     */
    public Predicate[] addPredicate(Predicate[] p1, Predicate... p2) {
        if (p1 == null) {
            p1 = new Predicate[0];
        }
        Predicate[] result = new Predicate[p1.length + p2.length];
        System.arraycopy(p1, 0, result, 0, p1.length);
        for (int i = p1.length, j = 0; i < p2.length + p1.length; i++, j++) {
            result[i] = p2[j];
        }

        return result;
    }
}
