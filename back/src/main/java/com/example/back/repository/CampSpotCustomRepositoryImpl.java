package com.example.back.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.back.domain.CampSpotBean;
import com.example.back.dto.CampSpotSearchRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class CampSpotCustomRepositoryImpl implements CampSpotCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CampSpotBean> findAvailableSpotsByFlexibleConditions(CampSpotSearchRequest req) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CampSpotBean> cq = cb.createQuery(CampSpotBean.class);
        Root<CampSpotBean> spot = cq.from(CampSpotBean.class);
        Join<Object, Object> site = spot.join("campSite");
        Join<Object, Object> area = site.join("campArea");

        List<Predicate> predicates = new ArrayList<>();

        if (req.getRegion() != null && !req.getRegion().isBlank()) {
            predicates.add(cb.like(area.get("location"), req.getRegion() + "%"));
        }
        if (req.getMinAltitude() != null) {
            predicates.add(cb.ge(area.get("altitude"), req.getMinAltitude()));
        }
        if (req.getMaxAltitude() != null) {
            predicates.add(cb.le(area.get("altitude"), req.getMaxAltitude()));
        }
        if (req.getRequiredTents() != null) {
            predicates.add(cb.ge(spot.get("maxTents"), req.getRequiredTents()));
        }

        cq.select(spot).where(predicates.toArray(new Predicate[0]));

        // 動態排序
        if (req.getSort() != null) {
            if ("campSpotId".equalsIgnoreCase(req.getSort())) {
                cq.orderBy(req.getDir() != null && !req.getDir()
                        ? cb.desc(spot.get("campSpotId"))
                        : cb.asc(spot.get("campSpotId")));
            } else {
                cq.orderBy(req.getDir() != null && !req.getDir()
                        ? cb.desc(area.get("campAreaId"))
                        : cb.asc(area.get("campAreaId")));
            }
        }

        TypedQuery<CampSpotBean> query = entityManager.createQuery(cq);

        if (req.getStart() != null && req.getStart() >= 0) {
            query.setFirstResult(req.getStart());
        }
        if (req.getRows() != null && req.getRows() > 0) {
            query.setMaxResults(req.getRows());
        }

        return query.getResultList();
    }

    @Override
    public long countAvailableSpotsByFlexibleConditions(CampSpotSearchRequest req) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<CampSpotBean> spot = cq.from(CampSpotBean.class);
        Join<Object, Object> site = spot.join("campSite");
        Join<Object, Object> area = site.join("campArea");

        List<Predicate> predicates = new ArrayList<>();

        if (req.getRegion() != null && !req.getRegion().isBlank()) {
            predicates.add(cb.like(area.get("location"), req.getRegion() + "%"));
        }
        if (req.getMinAltitude() != null) {
            predicates.add(cb.ge(area.get("altitude"), req.getMinAltitude()));
        }
        if (req.getMaxAltitude() != null) {
            predicates.add(cb.le(area.get("altitude"), req.getMaxAltitude()));
        }
        if (req.getRequiredTents() != null) {
            predicates.add(cb.ge(spot.get("maxTents"), req.getRequiredTents()));
        }

        cq.select(cb.count(spot)).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getSingleResult();
    }
}
