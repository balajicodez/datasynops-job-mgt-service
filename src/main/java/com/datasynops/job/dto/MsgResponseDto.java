package com.datasynops.job.dto;

import java.util.ArrayList;

public class MsgResponseDto {

    private float draw;
    private float recordsTotal;
    private float recordsFiltered;
    ArrayList<Object> data = new ArrayList<Object>();
    // Getter Methods

    public float getDraw() {
        return draw;
    }

    public float getRecordsTotal() {
        return recordsTotal;
    }

    public float getRecordsFiltered() {
        return recordsFiltered;
    }

    // Setter Methods

    public void setDraw(float draw) {
        this.draw = draw;
    }

    public void setRecordsTotal(float recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public void setRecordsFiltered(float recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
