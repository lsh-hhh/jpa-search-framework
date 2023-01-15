package com.lsh.framework;

import org.hibernate.query.spi.QueryImplementor;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public abstract class BaseSearchRepository<TPageDTO, TPageSearchDTO extends BaseSearchDTO> {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("deprecation")
    public PageDTO<TPageDTO> search(TPageSearchDTO searchDTO, Class<TPageDTO> pageDTOClass) {
        Map<String, Object> params = new HashMap<>();
        String whereSql = this.getWhereSql(searchDTO, searchDTO.getClass(), params);
        long count = count(searchDTO, params, whereSql);

        String orderSql = this.getOrderSql(searchDTO);
        String limitSql = this.getLimitSql(searchDTO);
        String selectQuerySql = String.join(" ", this.selectSql(), whereSql, this.getGroupBySql(), orderSql, limitSql);
        QueryImplementor<TPageDTO> selectQuery = (QueryImplementor) this.entityManager.createNativeQuery(selectQuerySql);
        selectQuery.setResultTransformer(new SearchResultTransformer<>(pageDTOClass));

        this.setParameters(selectQuery, params);
        List<TPageDTO> list = selectQuery.getResultList();
        return new PageDTO<>(list, searchDTO, count);
    }

    public long count(TPageSearchDTO searchDTO) {
        return count(searchDTO, null, null);
    }

    private long count(TPageSearchDTO searchDTO, Map<String, Object> params, String whereSql) {
        String countSql = this.countSql();
        return Optional.ofNullable(params)
                .filter(p -> !StringUtils.isEmpty(whereSql))
                .map(p -> {
                    String countQuerySql = String.join(" ", countSql, whereSql);
                    Query countQuery = this.entityManager.createNativeQuery(countQuerySql);
                    this.setParameters(countQuery, params);
                    return ((BigInteger) countQuery.getSingleResult()).longValue();
                })
                .orElseGet(() -> {
                    Map<String, Object> params1 = new HashMap<>();
                    String whereSql1 = this.getWhereSql(searchDTO, searchDTO.getClass(), params1);
                    String countQuerySql = String.join(" ", countSql, whereSql1);
                    Query countQuery = this.entityManager.createNativeQuery(countQuerySql);
                    this.setParameters(countQuery, params1);
                    return ((BigInteger) countQuery.getSingleResult()).longValue();
                });
    }

    protected void setParameters(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
            query.setParameter(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
    }

    protected abstract String countSql();

    protected abstract String selectSql();

    protected String getGroupBySql() {
        return "";
    }

    protected String getWhereSql(TPageSearchDTO searchDTO, Class<?> searchDTOClass, Map<String, Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("where ");
        stringBuilder.append(searchDTO.getFixedWhere());
        Field[] fields = searchDTOClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (this.isWhereFiled(field)) {
                try {
                    Object value = field.get(searchDTO);
                    if (Objects.nonNull(value)) {
                        PageQueryParam pageQueryParam = field.getAnnotation(PageQueryParam.class);
                        if (pageQueryParam != null) {
                            stringBuilder.append(String.format(" and %s", this.createQuerySql(field, params, value, pageQueryParam)));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return stringBuilder.toString();
    }

    protected String getLimitSql(TPageSearchDTO searchDTO) {
        return String.format("limit %d, %d", (searchDTO.getPage() - 1) * searchDTO.getPageSize(), searchDTO.getPageSize());
    }

    protected String getOrderSql(TPageSearchDTO searchDTO) {
        if (searchDTO.getSortBy() == null || searchDTO.getSortBy().equals("")) {
            return searchDTO.getDefaultOrder();
        } else {
            String direction = searchDTO.isSortDesc() ? "desc" : "asc";
            return String.format("order by %s %s", searchDTO.getSortBy(), direction);
        }
    }

    protected boolean isWhereFiled(Field field) {
        return !field.getName().equals("pageIndex") && !field.getName().equals("pageSize") && !field.getName().equals("sort");
    }

    protected String createQuerySql(Field field, Map<String, Object> params, Object value, PageQueryParam pageQueryParam) {
        if (!pageQueryParam.type().equals(PageQueryParam.QueryType.IN) && !pageQueryParam.type().equals(PageQueryParam.QueryType.NOT_IN)) {
            params.put(field.getName(), value);
        }

        String sqlKeyword = StringUtils.isEmpty(pageQueryParam.sqlKeyword()) ? field.getName() : pageQueryParam.sqlKeyword();
        switch (pageQueryParam.type()) {
            case EQ:
                return String.format("%s = :%s", sqlKeyword, field.getName());
            case NOT_EQ:
                return String.format("%s != :%s", sqlKeyword, field.getName());
            case GT:
                return String.format("%s > :%s", sqlKeyword, field.getName());
            case GE:
                return String.format("%s >= :%s", sqlKeyword, field.getName());
            case LT:
                return String.format("%s < :%s", sqlKeyword, field.getName());
            case LE:
                return String.format("%s <= :%s", sqlKeyword, field.getName());
            case LIKE:
                return String.format("%s like CONCAT('%%',:%s,'%%')", sqlKeyword, field.getName());
            case LEFT_LIKE:
                return String.format("%s like CONCAT(:%s,'%%')", sqlKeyword, field.getName());
            case RIGHT_LIKE:
                return String.format("%s like CONCAT('%%',:%s)", sqlKeyword, field.getName());
            case IN:
                return handleInType(field, params, value, s -> String.format("%s in (%s)", sqlKeyword, s));
            case NOT_IN:
                return handleInType(field, params, value, s -> String.format("%s not in (%s)", sqlKeyword, s));
            case CUSTOM:
            default:
                if (StringUtils.isEmpty(pageQueryParam.customSql())) {
                    throw new RuntimeException("customSql不能为空");
                }
                return pageQueryParam.customSql();
        }
    }

    private String handleInType(Field field, Map<String, Object> params, Object value, Function<String, String> formatFunction) {
        try {
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                StringJoiner joiner = new StringJoiner(",");
                for (Object o : list) {
                    String paramKey = String.format("%s_%s", field.getName(), list.indexOf(o));
                    joiner.add(String.format(":%s", paramKey));
                    params.put(paramKey, o.toString());
                }
                return formatFunction.apply(joiner.toString());
            } else if (value instanceof Object[]) {
                Object[] objects = (Object[]) value;
                StringJoiner joiner = new StringJoiner(",");
                for (int i = 0; i < objects.length; i++) {
                    String paramKey = String.format("%s_%s", field.getName(), i);
                    joiner.add(String.format(":%s", paramKey));
                    params.put(paramKey, objects[i].toString());
                }
                return formatFunction.apply(joiner.toString());
            } else if (value instanceof String) {
                String str = (String) value;
                if (StringUtils.isEmpty(str)) {
                    return "";
                }
                return formatFunction.apply(str);
            } else {
                throw new RuntimeException("参数类型错误");
            }
        } catch (Throwable e) {
            throw new RuntimeException("in查询的情况下，参数只能是List、Object[]、String三种方式之一");
        }
    }
}
