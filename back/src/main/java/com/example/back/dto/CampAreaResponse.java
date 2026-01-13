package com.example.back.dto;

import java.util.List;

public class CampAreaResponse<T> {
    private Boolean success;
    private String message;
    private Long count;
    private List<T> list;
    
    

    public CampAreaResponse() {
	}

	public CampAreaResponse(Boolean success, String message, List<T> list) {
        this.success = success;
        this.message = message;
        this.list = list;
        this.count = (list != null) ? (long) list.size() : 0L;
    }

    // Getter/Setter
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}


}
