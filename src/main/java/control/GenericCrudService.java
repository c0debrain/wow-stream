/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import org.slf4j.Logger;

/**
 *
 * @author shahin
 */
public class GenericCrudService {

    @Inject
    Provider<Objectify> objectify;
    
    @Inject
    Logger logger;

    public <T> T create(T t) {
        Key<T> key = ofy().save().entity(t).now();
        return ofy().load().key(key).now();
    }

    public <T> void updateAsync(T t) {
        ofy().save().entity(t);
    }

    public  <T> void createAsync(T t) {
        ofy().save().entity(t);
    }

    public  <T> T find(Class<T> type, Long id) {
        return ofy().load().type(type).id(id).now();
    }

    public  <T> List<T> findAll(Class<T> type) {
        return ofy().load().type(type).list();
    }

    public  <T> List<T> findAll(Class<T> type, String[] params, Object[] values) {
        Query<T> query = ofy().load().type(type);
        for (int i = 0; i < params.length; i++) {
            query = query.filter(params[i], values[i]);
        }
        return query.list();
    }

    public  <T> List<T> findAll(Class<T> type, String[] params, Object[] values, String order) {
        Query<T> query = ofy().load().type(type).order(order);
        for (int i = 0; i < params.length; i++) {
            query = query.filter(params[i], values[i]);
        }
        return query.list();
    }

    public  <T> List<T> findAllString(Class<T> type, String field, String val) {
        Query<T> query = ofy().load().type(type);
        query = query.filter(field + " >=", val).filter(field + " <=", val + "\ufffd");
        return query.list();
    }

    public  <T> List<T> findAll(Class<T> type, String[] params, Object[] values, int startPos, int max, String order) {
        Query<T> query = ofy().load().type(type).order(order);
        for (int i = 0; i < params.length; i++) {
            query = query.filter(params[i], values[i]);
        }
        return query.limit(max).offset(startPos).list();
    }

    public  <T> List<T> findAll(Class<T> type, String order) {
        return ofy().load().type(type).order(order).list();
    }

    /**
     * Use to search and paginate. startPosition = 0 maxResult = 10 (variable)
     * startPostion = index * maxResult
     */
    public  <T> List<T> findAll(Class<T> type, int startPos, int max, String order) {
        //Logger.debug("findnig with offset : " + startPos + " max : " + max);
        return ofy().load().type(type).order(order).limit(max).offset(startPos).list();
    }

    public  long count(Class type) {
        return ofy().load().type(type).count();
    }

    public  <T> void delete(Class<T> type) {
        List<Key<T>> keys = ofy().load().type(type).keys().list();
        ofy().delete().keys(keys);
    }
    
    private Objectify ofy(){
        return objectify.get();
    }

}
