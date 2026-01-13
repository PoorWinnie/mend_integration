package com.example.back.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.CampSpotBean;
import com.example.back.dto.CampSpotAvailabilityDTO;
import com.example.back.dto.CampSpotSearchRequest;
import com.example.back.repository.BookingRepository;
import com.example.back.repository.CampAreaOffDayRepository;
import com.example.back.repository.CampAreaOffWeekdayRepository;
import com.example.back.repository.CampSpotRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AvailableCampSpotService {

    @Autowired
    private CampSpotRepository campSpotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CampAreaOffDayRepository campAreaOffDayRepository;

    @Autowired
    private CampAreaOffWeekdayRepository campAreaOffWeekdayRepository;
    
	@PersistenceContext
    private EntityManager entityManager;

    public List<CampSpotAvailabilityDTO> findAvailableSpotsWithInfo(CampSpotSearchRequest req) {
        List<CampSpotBean> candidates = campSpotRepository.findAvailableSpotsByFlexibleConditions(req);
        List<CampSpotAvailabilityDTO> result = new ArrayList<>();

        LocalDate checkIn = req.getCheckInDate();
        LocalDate checkOut = req.getCheckOutDate();
        int requestedTents = req.getRequiredTents() == null ? 1 : req.getRequiredTents();

        for (CampSpotBean spot : candidates) {
            CampAreaBean area = spot.getCampSite().getCampArea();
            Integer areaId = area.getCampAreaId();

            boolean hasSingleOff = (checkIn != null && checkOut != null)
                    ? campAreaOffDayRepository.countOffDaysBetween(areaId, checkIn, checkOut) > 0
                    : false;

            Set<Integer> offWeekdays = new HashSet<>(campAreaOffWeekdayRepository.findWeekdaysByCampAreaId(areaId));
            boolean hasWeeklyOff = false;
            if (checkIn != null && checkOut != null && !offWeekdays.isEmpty()) {
                LocalDate d = checkIn;
                while (d.isBefore(checkOut)) {
                    int weekday = d.getDayOfWeek().getValue();
                    int normalized = (weekday == 7) ? 0 : weekday;
                    if (offWeekdays.contains(normalized)) {
                        hasWeeklyOff = true;
                        break;
                    }
                    d = d.plusDays(1);
                }
            }

            boolean hasOffDays = hasSingleOff || hasWeeklyOff;

            int booked = (checkIn != null && checkOut != null)
                    ? bookingRepository.sumTentCountInDateRange(spot.getCampSpotId(), checkIn, checkOut)
                    : 0;
            int remaining = spot.getMaxTents() - booked;

            boolean isAvailable = !hasOffDays && remaining >= requestedTents;

            if (!isAvailable) {
                continue;
            }

            String note = "剩 " + remaining + " 帳";

            CampSpotAvailabilityDTO dto = new CampSpotAvailabilityDTO();
            dto.setCampSpotId(spot.getCampSpotId());
            dto.setCampAreaId(areaId);
            dto.setSpotName(spot.getSpotName());
            dto.setMaxTents(spot.getMaxTents());
            dto.setAvailableTents(Math.max(remaining, 0));
            dto.setIsAvailable(true);
            dto.setHasOffDays(false);
            dto.setPricePerNight(spot.getPricePerNight());
            dto.setPriceWeekend(spot.getPriceWeekend());
            dto.setNote(note);
            dto.setCampAreaName(area.getCampAreaName());
            dto.setAddress(area.getAddress());

            String imageUrl = (area.getCampAreaImages() != null && !area.getCampAreaImages().isEmpty())
                    ? area.getCampAreaImages().get(0).getImageUrl()
                    : "/static/uploads/img/no-image.png";

            dto.setCampAreaImageUrl(imageUrl);

            result.add(dto);
        }

        // 排序
        if ("campSpotId".equalsIgnoreCase(req.getSort())) {
            result.sort(Comparator.comparing(CampSpotAvailabilityDTO::getCampSpotId));
        } else {
            result.sort(Comparator.comparing(CampSpotAvailabilityDTO::getCampAreaId));
        }

        if (Boolean.FALSE.equals(req.getDir())) {
            result.sort(result.get(0).getCampAreaId() != null ?
                    Comparator.comparing(CampSpotAvailabilityDTO::getCampAreaId).reversed() :
                    Comparator.comparing(CampSpotAvailabilityDTO::getCampSpotId).reversed());
        }

        return result;
    }

//    public long countAvailableSpots(CampSpotSearchRequest req) {
//        return findAvailableSpotsWithInfo(req).size();
//    }
//    
    


    public long countAvailableSpots(CampSpotSearchRequest req) {
        // Step 1: 初步篩選符合條件的 CampSpot
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CampSpotBean> cq = cb.createQuery(CampSpotBean.class);
        Root<CampSpotBean> root = cq.from(CampSpotBean.class);

        List<Predicate> predicates = new ArrayList<>();

        if (req.getRegion() != null && !req.getRegion().isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("campSite").get("campArea").get("location")),
                "%" + req.getRegion().toLowerCase() + "%"
            ));
        }

        if (req.getMinAltitude() != null) {
            predicates.add(cb.ge(root.get("campSite").get("campArea").get("altitude"), req.getMinAltitude()));
        }

        if (req.getMaxAltitude() != null) {
            predicates.add(cb.le(root.get("campSite").get("campArea").get("altitude"), req.getMaxAltitude()));
        }


        cq.where(predicates.toArray(new Predicate[0]));
        List<CampSpotBean> candidates = entityManager.createQuery(cq).getResultList();

        // Step 2: 遍歷進階判斷（OffDay、預訂數、剩餘帳篷）
        LocalDate checkIn = req.getCheckInDate();
        LocalDate checkOut = req.getCheckOutDate();
        int requestedTents = req.getRequiredTents() != null ? req.getRequiredTents() : 1;

        long count = 0;

        for (CampSpotBean spot : candidates) {
            CampAreaBean area = spot.getCampSite().getCampArea();
            Integer areaId = area.getCampAreaId();

            // 檢查該期間內是否有指定 off day
            boolean hasSingleOff = (checkIn != null && checkOut != null)
                    && campAreaOffDayRepository.countOffDaysBetween(areaId, checkIn, checkOut) > 0;

            // 檢查固定休假日
            Set<Integer> offWeekdays = new HashSet<>(campAreaOffWeekdayRepository.findWeekdaysByCampAreaId(areaId));
            boolean hasWeeklyOff = false;
            if (checkIn != null && checkOut != null && !offWeekdays.isEmpty()) {
                LocalDate d = checkIn;
                while (d.isBefore(checkOut)) {
                    int weekday = d.getDayOfWeek().getValue();
                    int normalized = (weekday == 7) ? 0 : weekday; // Sunday=0
                    if (offWeekdays.contains(normalized)) {
                        hasWeeklyOff = true;
                        break;
                    }
                    d = d.plusDays(1);
                }
            }

            boolean hasOffDays = hasSingleOff || hasWeeklyOff;

            // 查詢目前帳篷預約數量
            int booked = (checkIn != null && checkOut != null)
                    ? bookingRepository.sumTentCountInDateRange(spot.getCampSpotId(), checkIn, checkOut)
                    : 0;
            int remaining = spot.getMaxTents() - booked;

            if (!hasOffDays && remaining >= requestedTents) {
                count++;
            }
        }

        return count;
    }




}
