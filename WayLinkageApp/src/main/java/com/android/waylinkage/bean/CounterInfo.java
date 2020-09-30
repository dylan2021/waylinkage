package com.android.waylinkage.bean;

/**
 * Gool Lee
 */
public class CounterInfo {

    private int confirmedQuality;
    private int unreformedQuality;
    private int totalQuality;
    private int pendingApproval;
    /**
     * pendingStartApply : 1
     * pendingFinishApply : 10
     * pendingQuality : 0
     * pendingFin : 5
     * pendingChangeApply : 5
     */

    private int pendingStartApply;
    private int pendingFinishApply;
    private int pendingQuality;
    private int pendingFin;
    private int pendingChangeApply;

    public int getConfirmedQuality() {
        return confirmedQuality;
    }

    public void setConfirmedQuality(int confirmedQuality) {
        this.confirmedQuality = confirmedQuality;
    }

    public int getUnreformedQuality() {
        return unreformedQuality;
    }

    public void setUnreformedQuality(int unreformedQuality) {
        this.unreformedQuality = unreformedQuality;
    }

    public int getTotalQuality() {
        return totalQuality;
    }

    public void setTotalQuality(int totalQuality) {
        this.totalQuality = totalQuality;
    }

    public int getPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(int pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public int getPendingStartApply() {
        return pendingStartApply;
    }

    public void setPendingStartApply(int pendingStartApply) {
        this.pendingStartApply = pendingStartApply;
    }

    public int getPendingFinishApply() {
        return pendingFinishApply;
    }

    public void setPendingFinishApply(int pendingFinishApply) {
        this.pendingFinishApply = pendingFinishApply;
    }

    public int getPendingQuality() {
        return pendingQuality;
    }

    public void setPendingQuality(int pendingQuality) {
        this.pendingQuality = pendingQuality;
    }

    public int getPendingFin() {
        return pendingFin;
    }

    public void setPendingFin(int pendingFin) {
        this.pendingFin = pendingFin;
    }

    public int getPendingChangeApply() {
        return pendingChangeApply;
    }

    public void setPendingChangeApply(int pendingChangeApply) {
        this.pendingChangeApply = pendingChangeApply;
    }
}
