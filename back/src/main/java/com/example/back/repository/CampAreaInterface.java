package com.example.back.repository;

import java.util.List;

import org.json.JSONObject;

import com.example.back.domain.CampAreaBean;

public interface CampAreaInterface {
	
	long count(JSONObject obj);

	List<CampAreaBean> find(JSONObject obj);
}
