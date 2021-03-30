package com.cn.bookmarktomb.util;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ArrayUtil;
import com.cn.bookmarktomb.excepotion.UniqueIdUsedException;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.service.UserInfoService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * @author fallen-anlgle
 * This is the util to generate mongo query.
 */

public class MongoUtil {

    private static final String USERNAME_FILED_NAME = "UName";
    private static final String USER_EMAIL_FIELD_NAME = "UEml";

    private MongoUtil(){}

    /*-------------------------------------------< Generate Queries >----------------------------------------------*/

    public static Query getEqQueryByParam(String fieldName, Object fieldValue){
        return getEqQueryByMap(Map.of(fieldName, fieldValue));
    }

    public static Query getEqQueryByList(String fieldName, List<?> fieldValues){
        return getEqQueryByMap(Map.of(fieldName, fieldValues));
    }

    /**
     * @param queryMap The map of field name and field value.
     */
    public static Query getEqQueryByMap(Map<String, Object> queryMap){
        return new Query(getEqCriteriaByMap(queryMap));
    }

    /**
     * @param includeField The return filed names.
     */
    public static Query getEqAndFieldQuery(Map<String, Object> queryMap, List<String> includeField) {
        Query query = new Query(getEqCriteriaByMap(queryMap));
        includeField.forEach(field -> query.fields().include(field));
        return query;
    }

    /**
     * @param existFieldNames The must exist value field names.
     */
    public static Query getEqQueryByMapAndExistFields(Map<String, Object> queryMap, String ...existFieldNames) {
        Criteria criteria = getEqCriteriaByMap(queryMap);
        for (String existFieldName: existFieldNames) {
            criteria.and(existFieldName).ne(null);
        }
        return new Query(criteria);
    }

    /**
     * If have may "or" query, can use this function generate "or" query Queries fastly;
     * @param fieldNames The names of fields, the size must same to fieldValues;
     * @param fieldValues The values of every field, the size of every field must same to each other;
     * Ex. filedNames: ["name1", "name2"], fieldValues: [[1,2],[3,4]] -> query {{name1=1, name2=3} or {name1=2, name2=4} }
     */
    public static Query getEqOrQueryByList(List<String> fieldNames, List<?> ...fieldValues) {
        List<Criteria> criteriaList = getEqCriteriaListByList(fieldNames, fieldValues);
        Criteria criteria = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
        return new Query(criteria);
    }

    /**
     * This is the extension of getEqOrQueryByList;
     * @param queryMap  Contains some field have same value;
     * @param fieldNames The names of fields, the size must same to fieldValues;
     * @param fieldValues The values of every field, the size of every field must same to each other;
     */
    public static Query getEqOrQueryByMapAndList(Map<String, Object> queryMap, List<String> fieldNames, List<?> ...fieldValues) {
        List<Criteria> criteriaList = getEqCriteriaListByList(fieldNames, fieldValues);
        Criteria criteria = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
        criteria.andOperator(getEqCriteriaByMap(queryMap));
        return new Query(criteria);
    }

    public static Query getAheadTimeQuery(String fieldName, LocalDateTime fieldValue) {
        return new Query(Criteria.where(fieldName).lte(fieldValue));
    }

    public static Query getLikeQueryByParam(String fieldName, Object fieldValue){
        Pattern pattern = Pattern.compile("^.*" + fieldValue + ".*$", Pattern.CASE_INSENSITIVE);
        return new Query(Criteria.where(fieldName).regex(pattern));
    }

    /*-------------------------------------------< Generate Criteria >----------------------------------------------*/

    private static List<Criteria> getEqCriteriaListByList(List<String> fieldNames, List<?> ...fieldValues) {
        // Detect the amount of field name is same to field value or not.
        if (fieldNames.isEmpty()
                || ArrayUtil.hasNull(fieldValues)
                || fieldNames.size() != fieldValues.length) {
            throw new SystemException("The amount of field name is unmatched with the field value");
        }

        // Detect the amount of field value is same to each other or not.
        int fieldNameAmount = fieldNames.size();
        int fieldValueAmount = fieldValues[0].size();
        for (List<?> fieldValue: fieldValues) {
            if (fieldValueAmount != fieldValue.size()) {
                throw new SystemException("The amount of field value is not as long as each other");
            }
        }

        // Generate the criteria
        List<Criteria> criteriaList = new ArrayList<>();
        for(int indexOfValue = 0; indexOfValue < fieldValueAmount; indexOfValue++) {
            MapBuilder<String, Object> fieldMapBuilder = MapBuilder.create();
            for(int indexOfName = 0; indexOfName < fieldNameAmount; indexOfName++) {
                fieldMapBuilder.put(fieldNames.get(indexOfName), fieldValues[indexOfName].get(indexOfValue));
            }
            Criteria criteria = getEqCriteriaByMap(fieldMapBuilder.map());
            criteriaList.add(criteria);
        }

        return criteriaList;
    }

    private static Criteria getEqCriteriaByMap(Map<String, Object> queryMap){
        Criteria criteria = new Criteria();

        // If the value is list, set query as "in" instead of "is".
        for (Entry<String, Object> entry: queryMap.entrySet()){
            if (entry.getValue() instanceof List) {
                criteria.and(entry.getKey()).in((List<?>)entry.getValue());
            } else {
                criteria.and(entry.getKey()).is(entry.getValue());
            }
        }

        return criteria;
    }

    /*-------------------------------------------< Other Functions >----------------------------------------------*/

    /**
     * Detect the unique id(like tel, email, and so on...) has been used or not.
     */
    public static void detectUserUniqueIdIsUsed(UserInfoService userInfoService, String uidName, String value){
        switch (uidName){
            case USER_EMAIL_FIELD_NAME:
                if (userInfoService.countSameField(uidName, value) > 0) {
                    throw new UniqueIdUsedException("Email", value);
                }
                break;
            case USERNAME_FILED_NAME:
                if (userInfoService.countSameField(uidName, value) > 0) {
                    throw new UniqueIdUsedException("Username", value);
                }
                break;
            default:
                throw new SystemException("Can't resolve " + uidName + " when detect!");
        }
    }

    /**
     * Generate a special mongo update, while the field value is null, will unset the field.
     */
    public static Update getUnsetUpdateWhileFieldNull(List<String> fieldNames, List<?> fieldValues) {
        if (fieldNames.isEmpty() || fieldNames.size() != fieldValues.size()) {
            throw new SystemException("The amount of field name is unmatched with the field value");
        }
        Update update = new Update();
        for (int index = 0; index < fieldNames.size(); index++) {
            if (Objects.isNull(fieldValues.get(index))) {
                update.unset(fieldNames.get(index));
            } else {
                update.set(fieldNames.get(index), fieldValues.get(index));
            }
        }
        return update;
    }

}