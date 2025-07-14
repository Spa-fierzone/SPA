package com.spazone.dto;

import java.math.BigDecimal;
import java.util.List;

public class MembershipDTO {
    private Integer membershipId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMonths;
    private Integer discountPercent;
    private Integer maxBookingsPerMonth;
    private Boolean priorityBooking;
    private Boolean freeConsultation;
    private String benefits;
    private String status;
    private List<String> benefitsList; // Parsed benefits for display

    // Constructors
    public MembershipDTO() {}

    // Getters and Setters
    public Integer getMembershipId() { return membershipId; }
    public void setMembershipId(Integer membershipId) { this.membershipId = membershipId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public Integer getMaxBookingsPerMonth() { return maxBookingsPerMonth; }
    public void setMaxBookingsPerMonth(Integer maxBookingsPerMonth) { this.maxBookingsPerMonth = maxBookingsPerMonth; }

    public Boolean getPriorityBooking() { return priorityBooking; }
    public void setPriorityBooking(Boolean priorityBooking) { this.priorityBooking = priorityBooking; }

    public Boolean getFreeConsultation() { return freeConsultation; }
    public void setFreeConsultation(Boolean freeConsultation) { this.freeConsultation = freeConsultation; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getBenefitsList() { return benefitsList; }
    public void setBenefitsList(List<String> benefitsList) { this.benefitsList = benefitsList; }
}