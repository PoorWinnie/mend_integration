package com.example.back.specification;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.FeatureTagBean;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class CampAreaSpecification {

    public static Specification<CampAreaBean> filter(
            String keyword,
            String region,
            List<Integer> altitudeRange,
            List<Integer> featureIds
    ) {
        return (Root<CampAreaBean> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            // 加EntityGraph（重點！）
            root.fetch("user", JoinType.LEFT);
            root.fetch("features", JoinType.LEFT);
            root.fetch("campAreaImages", JoinType.LEFT);

            Predicate predicate = cb.conjunction(); // 初始為 true

            if (keyword != null && !keyword.isBlank()) {
                predicate = cb.and(predicate, cb.like(root.get("camp_area_name"), "%" + keyword + "%"));
            }

            if (region != null && !region.isBlank()) {
                predicate = cb.and(predicate, cb.like(root.get("location"), region + "%"));
            }

            if (altitudeRange != null && !altitudeRange.isEmpty()) {
                Predicate altitudePredicate = cb.disjunction();
                for (Integer range : altitudeRange) {
                    switch (range) {
                        case 1 -> altitudePredicate = cb.or(altitudePredicate, cb.le(root.get("altitude"), 300));
                        case 2 -> altitudePredicate = cb.or(altitudePredicate, cb.between(root.get("altitude"), 301, 500));
                        case 3 -> altitudePredicate = cb.or(altitudePredicate, cb.between(root.get("altitude"), 501, 800));
                        case 4 -> altitudePredicate = cb.or(altitudePredicate, cb.ge(root.get("altitude"), 801));
                    }
                }
                predicate = cb.and(predicate, altitudePredicate);
            }

            if (featureIds != null && !featureIds.isEmpty()) {
                Join<CampAreaBean, FeatureTagBean> featureJoin = root.join("features", JoinType.LEFT);
                predicate = cb.and(predicate, featureJoin.get("tag_id").in(featureIds));
                query.distinct(true); // 多對多時加distinct
            }

            return predicate;
        };
    }
}
