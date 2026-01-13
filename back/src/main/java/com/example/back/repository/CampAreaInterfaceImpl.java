package com.example.back.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.FeatureTagBean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Repository
public class CampAreaInterfaceImpl implements CampAreaInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count(JSONObject obj) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CampAreaBean> root = query.from(CampAreaBean.class);

        query.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(cb.equal(root.get("status"), "Active"));

        if (obj.has("campAreaName") && !obj.isNull("campAreaName")) {
            String name = obj.getString("campAreaName");
            predicates.add(cb.like(root.get("campAreaName"), "%" + name + "%"));
        }

        if (obj.has("location") && !obj.isNull("location")) {
            String location = obj.getString("location");
            predicates.add(cb.like(root.get("location"), "%" + location + "%"));
        }

        if (obj.has("address") && !obj.isNull("address")) {
            String address = obj.getString("address");
            predicates.add(cb.like(root.get("address"), "%" + address + "%"));
        }

        if (obj.has("altitudeRange")) {
            JSONArray rangeArray = obj.getJSONArray("altitudeRange");
            List<Predicate> altitudePredicates = new ArrayList<>();

            for (int i = 0; i < rangeArray.length(); i++) {
                int type = rangeArray.getInt(i);
                switch (type) {
                    case 1 -> altitudePredicates.add(cb.lessThanOrEqualTo(root.get("altitude"), 300));
                    case 2 -> altitudePredicates.add(cb.between(root.get("altitude"), 301, 500));
                    case 3 -> altitudePredicates.add(cb.between(root.get("altitude"), 501, 800));
                    case 4 -> altitudePredicates.add(cb.greaterThanOrEqualTo(root.get("altitude"), 801));
                }
            }

            if (!altitudePredicates.isEmpty()) {
                predicates.add(cb.or(altitudePredicates.toArray(new Predicate[0])));
            }
        }
        String selectedRegion = obj.opt("region") != null ? obj.optString("region", "").trim() : "";
        if (obj.has("region")) {
            Map<String, String> cityToRegion = Map.ofEntries(
                Map.entry("台北市", "北部"), Map.entry("新北市", "北部"), Map.entry("基隆市", "北部"),
                Map.entry("桃園市", "北部"), Map.entry("新竹市", "北部"), Map.entry("新竹縣", "北部"),
                Map.entry("苗栗縣", "中部"), Map.entry("台中市", "中部"), Map.entry("彰化縣", "中部"),
                Map.entry("南投縣", "中部"), Map.entry("雲林縣", "中部"),
                Map.entry("嘉義市", "南部"), Map.entry("嘉義縣", "南部"), Map.entry("台南市", "南部"),
                Map.entry("高雄市", "南部"), Map.entry("屏東縣", "南部"),
                Map.entry("宜蘭縣", "東部"), Map.entry("花蓮縣", "東部"), Map.entry("台東縣", "東部")
            );

            List<Predicate> regionCityPredicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : cityToRegion.entrySet()) {
                if (selectedRegion.equals(entry.getValue())) {
                    regionCityPredicates.add(cb.like(root.get("location"), entry.getKey() + "%"));
                }
            }

            if (!regionCityPredicates.isEmpty()) {
                predicates.add(cb.or(regionCityPredicates.toArray(new Predicate[0])));
            }
        }

        if (obj.has("featureIds")) {
            JSONArray featureArray = obj.getJSONArray("featureIds");
            if (!featureArray.isEmpty()) {
                List<Integer> featureIdList = new ArrayList<>();
                for (int i = 0; i < featureArray.length(); i++) {
                    featureIdList.add(featureArray.getInt(i));
                }

                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<CampAreaBean> subRoot = subquery.from(CampAreaBean.class);
                Join<CampAreaBean, FeatureTagBean> join = subRoot.join("features");

                subquery.select(subRoot.get("campAreaId"))
                        .where(join.get("tagId").in(featureIdList))
                        .groupBy(subRoot.get("campAreaId"))
                        .having(cb.equal(cb.count(join.get("tagId")), featureIdList.size()));

                predicates.add(root.get("campAreaId").in(subquery));
            }
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public List<CampAreaBean> find(JSONObject obj) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CampAreaBean> query = cb.createQuery(CampAreaBean.class);
        Root<CampAreaBean> root = query.from(CampAreaBean.class);

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(cb.equal(root.get("status"), "Active"));
        
        if (obj.has("campAreaName") && !obj.isNull("campAreaName")) {
            String name = obj.getString("campAreaName").trim();
            if (!name.isEmpty()) {
                predicates.add(cb.like(root.get("campAreaName"), "%" + name + "%"));
            }
        }

        if (obj.has("location") && !obj.isNull("location")) {
            String location = obj.getString("location").trim();
            if (!location.isEmpty()) {
                predicates.add(cb.like(root.get("location"), "%" + location + "%"));
            }
        }

        if (obj.has("address") && !obj.isNull("address")) {
            String address = obj.getString("address").trim();
            if (!address.isEmpty()) {
                predicates.add(cb.like(root.get("address"), "%" + address + "%"));
            }
        }

        if (obj.has("altitudeRange")) {
            JSONArray rangeArray = obj.getJSONArray("altitudeRange");
            List<Predicate> altitudePredicates = new ArrayList<>();
            for (int i = 0; i < rangeArray.length(); i++) {
                int type = rangeArray.getInt(i);
                switch (type) {
                    case 1 -> altitudePredicates.add(cb.lessThanOrEqualTo(root.get("altitude"), 300));
                    case 2 -> altitudePredicates.add(cb.between(root.get("altitude"), 301, 500));
                    case 3 -> altitudePredicates.add(cb.between(root.get("altitude"), 501, 800));
                    case 4 -> altitudePredicates.add(cb.greaterThanOrEqualTo(root.get("altitude"), 801));
                }
            }
            if (!altitudePredicates.isEmpty()) {
                predicates.add(cb.or(altitudePredicates.toArray(new Predicate[0])));
            }
        }
        String selectedRegion = obj.opt("region") != null ? obj.optString("region", "").trim() : "";
        if (obj.has("region")) {
            Map<String, String> cityToRegion = Map.ofEntries(
                Map.entry("台北市", "北部"), Map.entry("新北市", "北部"), Map.entry("基隆市", "北部"),
                Map.entry("桃園市", "北部"), Map.entry("新竹市", "北部"), Map.entry("新竹縣", "北部"),
                Map.entry("苗栗縣", "中部"), Map.entry("台中市", "中部"), Map.entry("彰化縣", "中部"),
                Map.entry("南投縣", "中部"), Map.entry("雲林縣", "中部"),
                Map.entry("嘉義市", "南部"), Map.entry("嘉義縣", "南部"), Map.entry("台南市", "南部"),
                Map.entry("高雄市", "南部"), Map.entry("屏東縣", "南部"),
                Map.entry("宜蘭縣", "東部"), Map.entry("花蓮縣", "東部"), Map.entry("台東縣", "東部")
            );

            List<Predicate> regionCityPredicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : cityToRegion.entrySet()) {
                if (selectedRegion.equals(entry.getValue())) {
                    regionCityPredicates.add(cb.like(root.get("location"), entry.getKey() + "%"));
                }
            }
            if (!regionCityPredicates.isEmpty()) {
                predicates.add(cb.or(regionCityPredicates.toArray(new Predicate[0])));
            }
        }

        if (obj.has("featureIds")) {
            JSONArray featureArray = obj.getJSONArray("featureIds");
            if (!featureArray.isEmpty()) {
                List<Integer> featureIdList = new ArrayList<>();
                for (int i = 0; i < featureArray.length(); i++) {
                    featureIdList.add(featureArray.getInt(i));
                }

                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<CampAreaBean> subRoot = subquery.from(CampAreaBean.class);
                Join<CampAreaBean, FeatureTagBean> join = subRoot.join("features");

                subquery.select(subRoot.get("campAreaId"))
                        .where(join.get("tagId").in(featureIdList))
                        .groupBy(subRoot.get("campAreaId"))
                        .having(cb.equal(cb.count(join.get("tagId")), featureIdList.size()));

                predicates.add(root.get("campAreaId").in(subquery));
            }
        }

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        int start = obj.optInt("start", 0);
        int rows = obj.optInt("rows", 10);
        boolean dir = obj.optBoolean("dir", false);
        String sortField = obj.optString("sort", "campAreaId");

        if (dir) {
            query.orderBy(cb.desc(root.get(sortField)));
        } else {
            query.orderBy(cb.asc(root.get(sortField)));
        }

        return entityManager.createQuery(query)
                .setFirstResult(start)
                .setMaxResults(rows)
                .getResultList();
    }

}